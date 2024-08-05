(ns portfolio-manager.views
  (:require
   [re-frame.core :as re-frame]
   [portfolio-manager.subs :as subs]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div.flex.flex-col.justify-center.items-center.h-screen
     [:div {:class "w-2/3 h-1/2" :id "editor"}
      [:p "Hello World"]]
     [:h1 "Hello"]
     [:p "Hello"]]))
