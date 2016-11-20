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
            [clojure.string :refer [join split]]))

(defn template [body]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:http-equiv "X-UA-Compatible"
            :content "IE=edge"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1, shrink-to-fit=no"}]
    [:meta {:name "description"
            :content "markup.at Blog"}]
    [:title "markup.at Blog"]]
   [:body
    (map (fn [x] x) body)]))

(defn markdown-pages [pages]
  (zipmap (map #(str/replace % #"\.md$" ".html") (keys pages))
          (map #(template (md/to-html %)) (vals pages))))

(defn index-page [request]
  (template
   [[:h1 "Lessions Learned als langjähriger Softwareentwickler"]
    [:p [:b "Peter Donner, 2016-11-11:"]
     " Nach 18 Jahren als Softwareentwickler in mehreren sehr unterschiedlichen Firmen glaube ich nun zu wissen, wie man es richtig und auch, wie man es falsch macht. Diese Erkenntnisse möchte ich in nächster Zeit in dieses Blog schreiben und starte einmal - vor allem für mich selbst - mit etwas Brainstorming."]
    [:ul
     [:li "Verwende Datenbanken (immer!)"]
      [:li "Schränke dich ein, treffe Entscheidungen, revidiere schlechte Entscheidungen"]
      [:li "Schreib niemals einen Server from Scratch"]
      [:li "Denke abstrakt, finde konkrete User Scenarien"]
      [:li "Meide Code-Generatoren"]
      [:li "Meide ORM Tools"]
      [:li "Meide Debugger"]
      [:li "Logge"]
      [:li "Meide Software Technologien, die automatisches Testen erschweren"]
      [:li "Eclipse Projekte machen immer Probleme - immer Immer IMMER"]
      [:li "Meide Closed Source"]
      [:li "Benutze Versionsverwaltung für alles"]
      [:li "Kümmere dich um technische Schulden"]
      [:li "Teste automatisiert"]
      [:li "Automatisiere den Build"]
      [:li "Automatisiere alles!!!11"]
      [:li "Lerne eine funktionale Programmiersprache"]
      [:li "Bevorzuge unveränderliche Datenstrukturen"]
      [:li "Lege Wert auf Plattformunabhängigkeit"]
      [:li "Verwende Continuous Integration"]
      [:li "Unicode"]
      [:li "Sei nie zufrieden"]]

    [:h1 "Nach dem 20jährigen Maturatreffen"]
    [:p [:b "Peter Donner, 2016-10-23:"]
     " Schade, dass wir nicht ganz vollzählig waren. Eine Schulkollegin kam mit ihrem süßen 6 Wochen alten Baby (und verließ uns verständlicherweise vor dem Essen). Wir sahen einen gut gemachten HTL Promotion Film in Dolby hast du nicht gesehen. Besonders der Fahrzeugtechnik Bereich der HTL scheint extrem erfolgreich zu sein. Interessant ist, dass HTL-Abgänger jetzt einem Bachelor gleichgestellt werden. Leider war nur wenig Zeit um die Räumlichkeiten der HTL anzusehen, ich werde vielleicht wieder einmal den Tag der offenen Tür besuchen."]
    [:p "Das Essen im Mader war ausgezeichnet und die Atmosphäre war recht angenehm. Wir reichten Fotos durch, vor allem von der Maturareise. Ich gehörte zum harten Kern der länger unterwegs war. Steyr ist eine Stadt in der man gut Bescheid wissen muss, wo etwas los ist, besonders wenn es schon Morgen wird, deshalb waren wir in eher langweiligen Lokalen."]

    [:h1 "20jähriges Maturatreffen"]
    [:p [:b "Peter Donner, 2016-10-14:"]
     " Heute haben wir unser 20jähriges HTL Maturatreffen, <b>das nutze ich doch gerne um einen Blog zu starten</b>. Es mag die große Welt wenig interessieren - egal, ich schreibe einmal alle Stichworte zusammen, die mir auf Anhieb einfallen (ohne in die Maturazeitung zu schauen oder so)&hellip;"
     [:ul
            [:li "Rage Against the Machine (Killing in the Name)"]
      [:li "Guns N' Roses (Get in the Ring Tour)"]
      [:li "Nirvana"]
      [:li "Metallica"]
      [:li "Hans Söllner"]
      [:li "U2 (Achtung Baby)"]
      [:li "Faith No More"]
      [:li "Soundgarden"]
      [:li "Metal T-Shirts"]
      [:li "Lange Haare"]
      [:li "Treff"]
      [:li "4.44er Formel; Eisen Zementit Diagramm; 4mm Schrifthöhe, 1mm Zeilenabstand"]
      [:li "Die schache Kernkraft, welche auch stark ist"]
      [:li "Scheiß digitaler EU Kaffee"]
      [:li "Sagen Sie's zhaus"]
      [:li "Des Kilo Papier wü i sehn"]
      [:li "0% FPÖ (bei simulierter Wahl, ist hoffentlich immer noch so)"]
      [:li "Dschungel Bier"]
      [:li "Nackte Frau: ok, nackter Mann: <b>Skandal!</b>"]
      [:li "Rave"]
      [:li "Skateboard"]
      [:li "7400, TTL Friedhof, 8051, PIC, Signalprozessor, Zip-Disk"]
      [:li "RG58 Netzwerk"]
      [:li "Finger weg vom 110V Schalter am PC Netzteil!"]
      [:li "Borland C"]
      [:li "Novell Netzwerk (Hidden Directories, die beim Ausloggen nicht gelöscht werden - oje&hellip;)"]
      [:li "Quadrophonie"]
      [:li "Ringkerntrafo"]
      [:li "Feilen"]
      [:li "Plastik picken"]
      [:li "Fehler in der Fernmeldewerkstätte erschnüffeln (Lack auf Relais Kontakten)"]
      [:li "Drehmaschine (Gefahr!)"]
      [:li "Wolfenstein, Doom"]
      [:li [:b "Das Internet"]
        [:ul
         [:li "Langsam zzZ"]
         [:li "Teuer (100 Schilling pro Stunde, 28.8er Modem)"]
         [:li [:span {:style  "text-decoration:line-through"} "Google"] " (1998)"]
         [:li [:span {:style  "text-decoration:line-through"} "Twitter"] " (2006)"]
         [:li [:span {:style  "text-decoration:line-through"} "Facebook"] " (2004)"]
         [:li [:span {:style  "text-decoration:line-through"} "YouTube"] " (2005)"]]]]]]))


(defn get-pages []
  (stasis/merge-page-sources
   {:programmed {"/" index-page}
    :markdown (markdown-pages
               (stasis/slurp-directory "resources/markdown" #"\.md$"))}))

(defn get-assets []
  [])

(def app
  (optimus/wrap (stasis/serve-pages get-pages)
                get-assets
                optimizations/all
                serve-live-assets))

(def export-dir "dist")

(defn export []
  (let [assets (optimizations/all (get-assets) {})]
    (stasis/empty-directory! export-dir)
    (optimus.export/save-assets (remove :outdated assets) export-dir)
    (stasis/export-pages (get-pages) export-dir {:optimus-assets assets})))

