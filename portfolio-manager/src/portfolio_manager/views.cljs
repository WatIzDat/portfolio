(ns portfolio-manager.views
  (:require ["quill$default" :as quill]
            [fork.re-frame :as fork]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.events :as events]
            [portfolio-manager.subs :as subs]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]))

(defn dashboard-panel []
  (let [modal-open (reagent/atom false)]
    (fn []
      [:div.flex.justify-center.items-center.h-screen
       (when @modal-open
         [:div.size-full.absolute.flex.justify-center.items-center.h-screen.backdrop-blur-md
          {:id "wrapper"
           :on-mouse-down
           #(let [id (.. % -target -id)]
              (when (= id "wrapper")
                (reset! modal-open false)))}
          [:div.bg-gray-700.text-white.rounded-2xl.flex.flex-col.items-center {:class "size-1/5"}
           [:h1.text-2xl.mt-4 "Create New Article"]
           [fork/form {:path [:modal]
                       :form-id "modal"
                       :prevent-default? true
                       :clean-on-unmount? true
                       :on-submit
                       (fn [state]
                         (reset! modal-open false)
                         (re-frame/dispatch [::events/upload state]))}
            (fn [{:keys [values
                         form-id
                         handle-change
                         handle-blur
                         submitting?
                         handle-submit]}]
              [:form.flex.flex-col.items-center
               {:id form-id
                :on-submit handle-submit}
               [:label.text-xl.mt-8 {:for "id"} "Please enter the ID of your new article:"]
               [:input.border.rounded-lg.border-gray-400.mb-4.bg-gray-900.mt-4.h-8.w-full
                {:type "text"
                 :name "id"
                 :id "id"
                 :on-change handle-change
                 :on-blur handle-blur
                 :value (values "id")}]
               [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
                {:type "submit"
                 :disabled submitting?}
                "Continue"]])]]])

       [:div.flex.flex-col.justify-center.items-center {:class "size-1/3" :inert (when @modal-open "")}
        [:div.flex.flex-row.w-full.items-end
         [:h1.w-full.text-2xl "Articles"]
         [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.w-72
          {:on-click
           (fn []
             (println "test")
             (reset! modal-open true)
             (println @modal-open))}
          "Create New"]]

        [:div.flex.flex-col.mt-4.bg-gray-300.size-full.rounded-3xl.p-4
         [:ul.flex.flex-col.gap-4.overflow-y-auto
          (map
           (fn [article]
             [:li {:key (article :articles/id)}
              [:button.flex.bg-gray-500.rounded-lg.p-2.text-white.size-full
               {:on-click
                #(set! (.. js/window -location -href) (str "/article/" (article :articles/id)))}
               [:div.flex-grow
                [:h2.text-lg (article :articles/name)]
                [:p (article :articles/id)]]
               [:p (str (article :articles/project_completion) "%")]]])
           @(re-frame/subscribe [::subs/articles]))]]]])))

(defn article-panel []
  (fn []
    (reagent/create-class
     {:component-did-mount
      (fn []
        (let [quill (new quill consts/editor-id (js-obj "theme" "snow"))]
          (.on quill "text-change"
               (fn [_ _ _]
                 (println "contents changed")
                 (re-frame/dispatch [::events/article-form-changed :markdown (.getContents quill)])))))
      :display-name "Edit Article Panel"
      :reagent-render
      (fn []
        [:<>
         [:button.fixed.size-16.text-5xl
          {:on-click #(set! (.. js/window -location -href) "/")}
          "<"]
         [:div.flex.flex-col.justify-center.items-center.h-screen
          [:div {:class "size-2/3 mb-24"}
           [fork/form {:path [:form]
                       :form-id "form"
                       :prevent-default? true
                       :clean-on-unmount? true
                       :on-submit #(re-frame/dispatch [::events/article-form-submit %])}
            (fn [{:keys [values
                         form-id
                         handle-change
                         handle-blur
                         submitting?
                         handle-submit
                         set-values]}]
              (let [initial-values-set @(re-frame/subscribe [::subs/initial-values-set])
                    name @(re-frame/subscribe [::subs/initial-name])
                    project-completion @(re-frame/subscribe [::subs/initial-project-completion])]
                (when (and (not initial-values-set) (not (nil? name)))
                  (set-values {"name" name
                               "project-completion" project-completion})
                  (re-frame/dispatch [::events/initial-values-set])))

              (let [custom-handle-change
                    (fn [name]
                      (fn [evt]
                        (println evt)
                        (handle-change evt)
                        (re-frame/dispatch [::events/article-form-changed name (fork/retrieve-event-value evt)])))]
                [:form.flex.flex-col.size-full
                 {:id form-id
                  :on-submit handle-submit}
                 [:div.flex
                  [:div.flex.flex-col.mr-4
                   [:label {:for "name"} "Name:"]
                   [:input.border.rounded-lg.border-gray-400.mb-4
                    {:type "text"
                     :name "name"
                     :id "name"
                     :on-change (custom-handle-change :name)
                     :on-blur handle-blur
                     :value (values "name")}]]
                  [:div.flex.flex-col
                   [:label {:for "project-completion"} "Project Completion (%):"]
                   [:input.border.rounded-lg.border-gray-400.mb-4
                    {:type "text"
                     :name "project-completion"
                     :id "project-completion"
                     :on-change (custom-handle-change :project-completion)
                     :on-blur handle-blur
                     :value (values "project-completion")}]]]
                 [:div {:class "size-full" :id "editor"}]
                 [:div.flex.flex-row-reverse
                  [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
                   {:type "submit"
                    :on-click #(set-values {"is-delete" false})
                    :disabled submitting?}
                   "Save"]
                  [:button.bg-red-500.text-white.px-4.py-2.rounded-lg.mt-4.mr-4
                   {:type "submit"
                    :on-click #(set-values {"is-delete" true})
                    :disabled submitting?}
                   "Delete"]]]))]]]])})))

(defmulti panels identity)
(defmethod panels :dashboard-panel [] [dashboard-panel])
(defmethod panels :upload-article-panel [] [article-panel false])
(defmethod panels :edit-article-panel [] [article-panel true])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (fn []
      (println @active-panel)
      (panels @active-panel))))