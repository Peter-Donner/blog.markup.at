(defproject blog-markup-at "0.1.0-SNAPSHOT"
  :description "blog.markup.at"
  :url "https://github.com/Peter-Donner/blog.markup.at"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-shell "0.5.0"]]
  :dependencies [[hiccup "1.0.5"]
                 [org.clojure/clojure "1.8.0"]
                 [stasis "2.3.0"]
                 [ring "1.4.0"]
                 [me.raynes/cegdown "0.1.1"]
                 [enlive "1.1.6"]
                 [clygments "0.1.1"]
                 [optimus "0.19.0"]]
  :ring {:handler at.markup.blog.core/app}
  :aliases {"build-site" ["run" "-m" "at.markup.blog.core/export"]}
  :profiles {:dev {:plugins [[lein-ring "0.8.10"]]}
             :test {:dependencies [[midje "1.6.0"]]
                    :plugins [[lein-midje "3.1.3"]]}}
  :min-lein-version "2.6.1"
  :source-paths ["src/clj"]
  :test-paths ["test/clj"])
