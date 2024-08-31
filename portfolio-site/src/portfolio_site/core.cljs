(ns portfolio-site.core
  (:require [rum.core :as rum]))

(rum/defc heading []
  [:<>
   [:h1.ml-60.text-red-500 "another testa1231asdf"]
   [:h2 "yipee i think this finally works"]
   [:p "test"]])

(defn ^:dev/after-load mount-root []
  (rum/mount (heading) (.getElementById js/document "app")))

(defn init []
  (mount-root))