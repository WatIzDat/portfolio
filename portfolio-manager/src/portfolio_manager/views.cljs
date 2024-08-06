(ns portfolio-manager.views
  (:require [portfolio-manager.events :as events]
            [portfolio-manager.subs :as subs]
            [re-frame.core :as re-frame]))

(defn main-panel []
  (let [quill @(re-frame/subscribe [::subs/delta])]
    (println quill)
    [:div.flex.flex-col.justify-center.items-center.h-screen
     [:div {:class "size-2/3 mb-12"}
      [:div {:id "editor"}
       [:p "Hello World"]]
      [:div.flex.flex-row-reverse
       [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
        {:on-click
         #(re-frame/dispatch [::events/upload quill])}
        "Upload"]]]]))
