(ns at.markup.blog.web-test
  (:require [at.markup.blog.core :refer [app get-pages]]
            [midje.sweet :refer [=> fact]]))

(fact
 "200 check"
 (doseq [url (keys (get-pages))]
   (let [status (:status (app {:uri url}))]
     (println url)
     [url status] => [url 200])))
