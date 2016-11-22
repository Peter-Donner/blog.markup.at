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
            [clojure.string :refer [join split]]
            [net.cgrand.enlive-html :as enlive]))

(defn ^:private template [body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible"
            :content "IE=edge"}]
    [:meta {:http-equiv "Content-Type" :content "text/html; charset=utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
    [:meta {:name "description"
            :content "markup.at Blog"}]
    [:title "markup.at Blog"]]
   [:body
    (map (fn [x] x) body)]))

(defn ^:private markdown-to-html [markdown]
  (md/to-html markdown
              [:smarts :strikethrough :definitions]))

(defn ^:private markdown-pages [pages]
  ;; apparently :strikethrough needs :definitions
  (zipmap (map #(str/replace % #"\.md$" ".html") (keys pages))
          (map #(template
                 (markdown-to-html %))
               (vals pages))))

(defn ^:private slurp-markdown-pages []
  (stasis/slurp-directory "resources/markdown" #"\.md$"))

(defn ^:private all-blogs-in-one-page [request]
  (reduce
   (fn [a b] (markdown-to-html (str a "\n" b)))
   (map (fn [x] (first (rest x)))
        (reverse (slurp-markdown-pages)))))

(defn enlive-test [request]
  (println "enter enlive-test")
  (enlive/sniptest (template [(all-blogs-in-one-page nil)])
                   [:h1] (enlive/content "zonk")
                   ))

(defn get-pages []
  (stasis/merge-page-sources
   {:all {"/" all-blogs-in-one-page}
    :enlive {"/enlive/" enlive-test}
    :markdown (markdown-pages (slurp-markdown-pages))}))

(defn ^:private get-assets []
  [])

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

