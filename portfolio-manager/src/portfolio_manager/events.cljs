(ns portfolio-manager.events
  (:require ["quill$default" :as q]
            [fork.re-frame :as fork]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.db :as db]
            [portfolio-manager.effects :as effects]
            [re-frame.core :as re-frame]
            [superstructor.re-frame.fetch-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::set-active-panel
 (fn [db [_ panel-name]]
   (assoc db :active-panel panel-name)))

(re-frame/reg-event-fx
 ::get-article-by-id-success
 (fn [_ [_ result]]
   (println (:body result))
   {::effects/set-quill-contents (:body result)}))

(re-frame/reg-event-fx
 ::get-article-by-id
 (fn [_ [_ id]]
   {:fx [[:fetch {:method :get
                  :url (str "http://localhost:3001/api/article/" id)
                  :mode :cors
                  :credentials :omit
                  :on-success [::get-article-by-id-success]}]]}))

(re-frame/reg-event-db
 ::get-articles-success
 (fn [db [_ result]]
   (as-> (:body result) $
     (.parse js/JSON $)
     (js->clj $ :keywordize-keys true)
     (assoc db :articles $))))

(re-frame/reg-event-fx
 ::get-articles
 (fn [_ _]
   {:fx [[:fetch {:method :get
                  :url "http://localhost:3001/api/article"
                  :mode :cors
                  :credentials :omit
                  :on-success [::get-articles-success]}]]}))

(re-frame/reg-event-db
 ::upload-success
 (fn [db [_ path]]
   (println "Success!")
   (fork/set-submitting db path false)))

(re-frame/reg-event-fx
 ::upload
 (fn [{db :db} [_ {:keys [values _ path]}]]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (js->clj (.stringify js/JSON (.getContents (q/find editor))))]
     (println (values "id"))
     {:db (fork/set-submitting db path true)
      :fx [[:fetch {:method :post
                    :url "http://localhost:3001/api/article"
                    :request-content-type :json
                    :headers {"Accept" "application/json"
                              "Origin" "http://localhost:8280"}
                    :body {:id (values "id")
                           :name (values "name")
                           :markdown delta
                           :project-completion (parse-long (values "project-completion"))}
                    :mode :cors
                    :credentials :omit
                    :on-success [::upload-success path]}]]})))