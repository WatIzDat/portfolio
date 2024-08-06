(ns portfolio-manager.views
  (:require [portfolio-manager.events :as events]
            [re-frame.core :as re-frame]))

(defn main-panel []
  [:div.flex.flex-col.justify-center.items-center.h-screen
   [:div {:class "size-2/3 mb-24"}
    [:div.flex
     [:div.flex.flex-col.mr-4
      [:label {:for "id"} "ID:"]
      [:input.border.rounded-lg.border-gray-400.mb-4
       {:type "text"
        :name "id"
        :id "id"}]]
     [:div.flex.flex-col.mr-4
      [:label {:for "name"} "Name:"]
      [:input.border.rounded-lg.border-gray-400.mb-4
       {:type "text"
        :name "name"
        :id "name"}]]
     [:div.flex.flex-col
      [:label {:for "project-completion"} "Project Completion (%):"]
      [:input.border.rounded-lg.border-gray-400.mb-4
       {:type "text"
        :name "project-completion"
        :id "project-completion"}]]]
    [:div {:id "editor"}
     [:p "Hello World"]]
    [:div.flex.flex-row-reverse
     [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
      {:on-click
       #(re-frame/dispatch [::events/upload])}
      "Upload"]]]])
