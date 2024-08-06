(ns portfolio-manager.views
  (:require ["quill$default" :as quill]
            [fork.re-frame :as fork]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.events :as events]
            [portfolio-manager.subs :as subs]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn dashboard-panel []
  (fn []
    (reagent/create-class
     {:component-did-mount #(re-frame/dispatch [::events/get-articles])
      :display-name "Dashboard"
      :reagent-render
      (fn []
        [:div.flex.justify-center.items-center.h-screen
         [:div.flex.flex-col.justify-center.items-center {:class "size-1/3"}
          [:div.flex.flex-row.w-full.items-end
           [:h1.w-full.text-2xl "Articles"]
           [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.w-72
            "Create New"]]

          [:div.flex.flex-col.mt-4.bg-gray-300.size-full.rounded-3xl.p-4
           [:ul.flex.flex-col.gap-4.overflow-y-auto
            [:li
             [:div.flex.bg-gray-500.rounded-lg.p-2.text-white
              [:div.flex-grow
               [:h2.text-lg "Test Project"]
               [:p "test-project"]]
              [:p "67%"]]]]]]])})))

(defn article-panel []
  (fn []
    (reagent/create-class
     {:component-did-mount #(new quill consts/editor-id (js-obj "theme" "snow"))
      :display-name "Upload Form"
      :reagent-render
      (fn []
        [:div.flex.flex-col.justify-center.items-center.h-screen
         [:div {:class "size-2/3 mb-24"}
          [fork/form {:path [:form]
                      :form-id "form"
                      :prevent-default? true
                      :clean-on-unmount? true
                      :on-submit #(re-frame/dispatch [::events/upload %])}
           (fn [{:keys [values
                        form-id
                        handle-change
                        handle-blur
                        submitting?
                        handle-submit]}]
             [:form.flex.flex-col.size-full
              {:id form-id
               :on-submit handle-submit}
              [:div.flex
               [:div.flex.flex-col.mr-4
                [:label {:for "id"} "ID:"]
                [:input.border.rounded-lg.border-gray-400.mb-4
                 {:type "text"
                  :name "id"
                  :id "id"
                  :on-change handle-change
                  :on-blur handle-blur
                  :value (values "id")}]]
               [:div.flex.flex-col.mr-4
                [:label {:for "name"} "Name:"]
                [:input.border.rounded-lg.border-gray-400.mb-4
                 {:type "text"
                  :name "name"
                  :id "name"
                  :on-change handle-change
                  :on-blur handle-blur
                  :value (values "name")}]]
               [:div.flex.flex-col
                [:label {:for "project-completion"} "Project Completion (%):"]
                [:input.border.rounded-lg.border-gray-400.mb-4
                 {:type "text"
                  :name "project-completion"
                  :id "project-completion"
                  :on-change handle-change
                  :on-blur handle-blur
                  :value (values "project-completion")}]]]
              [:div {:class "size-full" :id "editor"}
               [:p "Hello World"]]
              [:div.flex.flex-row-reverse
               [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
                {:type "submit"
                 :disabled submitting?}
                "Upload"]]])]]])})))

(defmulti panels identity)
(defmethod panels :dashboard-panel [] [dashboard-panel])
(defmethod panels :article-panel [] [article-panel])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (fn []
      (println @active-panel)
      (panels @active-panel))))