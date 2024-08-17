(ns portfolio-manager.views
  (:require ["quill$default" :as quill]
            [fork.re-frame :as fork]
            [malli.core :as m]
            [malli.error :as me]
            [malli.transform :as mt]
            [malli.util :as mu]
            [portfolio-api.spec.article :as spec]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.events :as events]
            [portfolio-manager.subs :as subs]
            [re-frame.core :as re-frame]
            [reagent.core :as reagent]
            ["react-hot-toast" :refer (Toaster)]))

(def specs {"id"
            (mu/select-keys spec/article [:id])
            "name"
            (mu/select-keys spec/article [:name])
            "project-completion"
            (mu/select-keys spec/article [:project-completion])})

(defn validate [value]
  (println value)
  (reduce
   #(let [spec (specs (key %2))]
      (if (nil? spec)
        %1
        (let [schema-type (mu/find-first
                           spec
                           (fn [s _ _]
                             (let [type (m/type s)]
                               (if (= :map type)
                                 nil
                                 type))))
              coerced
              {(keyword (key %2)) (m/decode schema-type (val %2) mt/string-transformer)}]
          (println (val %2))
          (println coerced)
          (merge %1 (me/humanize (m/explain spec coerced))))))
   {}
   value))

(defn form-input
  ([name label-text type values touched handle-blur handle-change errors]
   (form-input name label-text type values touched handle-blur handle-change errors "" ""))
  ([name label-text type values touched handle-blur handle-change errors label-class input-class]
   [:<>
    [:label {:for name
             :class label-class} label-text]
    [:input.border.rounded-lg.border-gray-400
     {:type type
      :name name
      :id name
      :on-change handle-change
      :on-blur handle-blur
      :value (values name)
      :class (str input-class " " (when (seq (first ((keyword name) errors)))
                                    "border-red-500"))}]
    [:p.text-red-500
     {:class (when (empty? (first ((keyword name) errors)))
               "mb-6")}
     (when (touched name)
       (println errors)
       (first ((keyword name) errors)))]]))

(defn toaster []
  (Toaster {:reverse-order false}))

(defn dashboard-panel []
  (let [modal-open (reagent/atom false)]
    (fn []
      [:div.flex.justify-center.items-center.h-screen
       [:f> toaster]
       (when @modal-open
         [:div.size-full.absolute.flex.justify-center.items-center.h-screen.backdrop-blur-md
          {:id "wrapper"
           :on-mouse-down
           #(let [id (.. % -target -id)]
              (when (= id "wrapper")
                (reset! modal-open false)
                (re-frame/dispatch [::events/reset-create-validation])))}
          [:div.bg-gray-700.text-white.rounded-2xl.flex.flex-col.items-center {:class "size-1/5"}
           [:h1.text-2xl.mt-3 "Create New Article"]
           [fork/form {:path [:modal]
                       :form-id "modal"
                       :validation validate
                       :prevent-default? true
                       :clean-on-unmount? true
                       :on-submit
                       (fn [state]
                         (re-frame/dispatch [::events/create state]))}
            (fn [{:keys [values
                         form-id
                         handle-change
                         handle-blur
                         errors
                         submitting?
                         handle-submit
                         send-server-request]}]
              [:form.flex.flex-col.items-center
               {:id form-id
                :on-submit handle-submit}

               (form-input
                "id" "Please enter the ID of your new article:" "text"
                values (fn [] true)
                handle-blur (fn [evt]
                              (handle-change evt)
                              (send-server-request
                               {:name "id"
                                :value (fork/retrieve-event-value evt)
                                :evt :on-blur
                                :debounce 200}
                               #(re-frame/dispatch [::events/validate-create-request %])))
                (let [failed @(re-frame/subscribe [::subs/create-validation-failed])]
                  (if failed
                    (assoc errors :id ["ID already exists"])
                    errors))
                "text-xl mt-8"
                "border rounded-lg border-gray-400 bg-gray-900 mt-4 mb-1 h-8 w-full")

               (when (seq (:id errors))
                 (re-frame/dispatch [::events/reset-create-validation]))

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
               [:div.flex.flex-col.gap-1.items-end.size-4
                [:p (str (article :articles/project_completion) "%")]
                [:p (if (article :articles/listed) "Listed" "Unlisted")]]]])
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
         [:f> toaster]
         [:button.fixed.size-16.text-5xl
          {:on-click #(set! (.. js/window -location -href) "/")}
          "<"]
         [:div.flex.flex-col.justify-center.items-center.h-screen
          [:div {:class "size-2/3 mb-24"}
           [fork/form {:path [:form]
                       :form-id "form"
                       :validation validate
                       :prevent-default? true
                       :clean-on-unmount? true
                       :on-submit #(re-frame/dispatch [::events/article-form-submit %])}
            (fn [{:keys [values
                         form-id
                         errors
                         touched
                         handle-change
                         handle-blur
                         submitting?
                         handle-submit
                         set-values]}]
              (println errors)
              (let [initial-values-should-be-set @(re-frame/subscribe [::subs/initial-values-should-be-set])
                    name @(re-frame/subscribe [::subs/initial-name])
                    project-completion @(re-frame/subscribe [::subs/initial-project-completion])]
                (when initial-values-should-be-set
                  (println project-completion)
                  (set-values {"name" name
                               "project-completion" project-completion})
                  (re-frame/dispatch [::events/initial-values-set])))

              (let [custom-handle-change
                    (fn [name]
                      (fn [evt]
                        (println evt)
                        (handle-change evt)
                        (re-frame/dispatch [::events/article-form-changed name (fork/retrieve-event-value evt)])))
                    listed @(re-frame/subscribe [::subs/listed])]
                [:form.flex.flex-col.size-full
                 {:id form-id
                  :on-submit handle-submit}
                 [:div.flex
                  [:div.flex.flex-col.mr-4
                   (form-input
                    "name" "Name:" "text"
                    values touched handle-blur (custom-handle-change :name)
                    errors)]
                  [:div.flex.flex-col
                   (form-input
                    "project-completion" "Project Completion (%):" "number"
                    values touched handle-blur (custom-handle-change :project-completion)
                    errors)]]
                 [:div {:class "size-full" :id "editor"}]
                 [:div.flex.flex-row-reverse
                  [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4
                   {:type "submit"
                    :on-click #(set-values {"submit-type" :upload})
                    :disabled submitting?}
                   (if listed "Edit" "Upload")]
                  (when listed
                    [:button.bg-blue-500.text-white.px-4.py-2.rounded-lg.mt-4.mr-4
                     {:type "submit"
                      :on-click #(set-values {"submit-type" :de-list})
                      :disabled submitting?}
                     "De-list"])
                  [:button.bg-red-500.text-white.px-4.py-2.rounded-lg.mt-4.mr-4
                   {:type "submit"
                    :on-click #(set-values {"submit-type" :delete})
                    :disabled submitting?}
                   "Delete"]]]))]]]])})))

(defn not-found-panel []
  [:div.flex.flex-col.justify-center.items-center.h-screen
   [:h1.text-8xl.mb-8 "404 Not Found"]
   [:a.text-4xl.text-blue-500.underline {:href "/"} "Back to Dashboard"]])

(defmulti panels identity)
(defmethod panels :dashboard-panel [] [dashboard-panel])
(defmethod panels :upload-article-panel [] [article-panel false])
(defmethod panels :edit-article-panel [] [article-panel true])
(defmethod panels :not-found-panel [] [not-found-panel])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [::subs/active-panel])]
    (fn []
      (println @active-panel)
      (panels @active-panel))))