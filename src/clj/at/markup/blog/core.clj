(ns at.markup.blog.core
  (:require [optimus.assets :as assets]
            [optimus.optimizations :as optimizations]
            [optimus.prime :as optimus]
            [optimus.link :refer [file-path]]
            [optimus.strategies :refer [serve-live-assets]]
            [optimus.export]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [hiccup.page :refer [html5]]
            [me.raynes.cegdown :as md]
            [stasis.core :as stasis]
            [net.cgrand.enlive-html :as enlive]
            [clygments.core :as pygments]))

(defn ^:private highlight [node]
  (let [code (->> node :content (apply str))
        lang (->> node :attrs :class keyword)]
    (if (nil? lang)
      ;; code blocks with undefined language
      (str "<div class=\"highlight\"><pre>" code "</pre></div>")
      (pygments/highlight code lang :html))))

(defn ^:private highlight-code-blocks [page]
  (enlive/sniptest
   page
   [:pre :code] #(assoc % :content (enlive/html-snippet (highlight  %)))))

(def ^:private blog-homepage-link [:a {:href "/"} "&uarr; Blog Index"])

(def ^:private kontakt-link
  [:a {:href "mailto:peter@markup.at"} "Kontakt (peter@markup.at)"])

(defn ^:private head [request title]
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:http-equiv "X-UA-Compatible"
           :content "IE=edge"}]
   [:meta {:http-equiv "Content-Type" :content "text/html; charset=utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
   [:meta {:name "description"
           :content "markup.at Blog"}]
   [:link {:rel "stylesheet" :href (file-path request "/css/style.css")}]
   [:title title]])

(defn ^:private index-template [request body]
  (html5
   (head request "markup.at Blog")
   [:body
    kontakt-link
    body]))

(defn ^:private article-template [request body]
  (let [header [blog-homepage-link " | " kontakt-link], footer header]
    (html5
     (head request "markup.at Blog")
     [:body
      (map (fn [x] x) header)
      body
      (map (fn [x] x) footer)])))

(defn markdown-to-html [markdown]
  ;; apparently :strikethrough needs :definitions
  (highlight-code-blocks (md/to-html markdown [:smarts
                                               :strikethrough
                                               :definitions
                                               :fenced-code-blocks])))

(defn ^:private slurp-markdown-pages []
  (stasis/slurp-directory "resources/markdown" #"\.md$"))

(defn wrap-with-link [page link]
  ;; TODO use enlive
  (clojure.string/replace page #"^<h1>(.+?)</h1>" (str "<h1><a href=\"" link "\">$1</a></h1>")))

(defn ^:private all-blogs-in-one-page [request]
  (index-template
   request
   (clojure.string/join 
    (map (fn [[k v]]
           (wrap-with-link (markdown-to-html v) (str/replace k #"\.md$" ".html")))
         (reverse (sort-by first (seq (slurp-markdown-pages))))))))

(defn ^:private markdown-pages [pages]
  (zipmap (map #(str/replace % #"\.md$" ".html") (keys pages))
          (map #(fn [request]
                  (article-template
                   request (markdown-to-html %))) (vals pages))))

(defn get-pages []
  (stasis/merge-page-sources
   {:all {"/" all-blogs-in-one-page}
    :markdown (markdown-pages (slurp-markdown-pages))}))

(defn ^:private get-assets [] (assets/load-assets "public" [#".*"]))

(def ^:private export-dir "dist")

(def app
  (optimus/wrap (stasis/serve-pages get-pages)
                get-assets
                optimizations/all
                serve-live-assets))

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets (remove :outdated assets) export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))
