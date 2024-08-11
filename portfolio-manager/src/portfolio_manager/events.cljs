(ns portfolio-manager.events
  (:require ["quill$default" :as q]
            [clojure.string :as string]
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
 ::get-articles-success
 (fn [db [_ result]]
   (as-> (:body result) $
     (.parse js/JSON $)
     (js->clj $ :keywordize-keys true)
     (assoc db :articles $))))

(re-frame/reg-event-fx
 ::get-article-by-id-success
 (fn [{db :db} [_ result]]
   (println (:body result))
   (let [article (js->clj (.parse js/JSON (:body result)) :keywordize-keys true)]
     (println "2nd")
     (println (:articles/project_completion article))
     {:db (assoc db
                 :initial-name (or (:articles/name article) (:initial-name db))
                 :initial-project-completion (or (:articles/project_completion article) (:initial-project-completion article))
                 :listed (:articles/listed article))
      ::effects/set-quill-contents
      (if (nil? (:initial-markdown db))
        (:articles/markdown article)
        (.stringify js/JSON (clj->js (:initial-markdown db))))})))

(re-frame/reg-event-fx
 ::set-active-panel
 (fn [{db :db} [_ panel-name {:keys [id]}]]
   (let [set-page (assoc db :active-panel panel-name)
         get-articles-effect
         [:fetch {:method :get
                  :url "http://localhost:3001/api/article"
                  :mode :cors
                  :credentials :omit
                  :on-success [::get-articles-success]}]
         get-article-by-id-effect
         [:fetch {:method :get
                  :url (str "http://localhost:3001/api/article/" id)
                  :mode :cors
                  :credentials :omit
                  :on-success [::get-article-by-id-success]}]
         article-from-local-storage (db/get-article-from-local-storage id)]
     (println (:markdown article-from-local-storage))
     (case panel-name
       :dashboard-panel {:db set-page
                         :fx [get-articles-effect]}
       :upload-article-panel {:db set-page}
       :edit-article-panel {:db (merge (assoc set-page :initial-id id :initial-markdown (:markdown article-from-local-storage))
                                       (into {}
                                             (map
                                              #(first {(keyword (str "initial-" (apply str (rest (str (key %)))))) (val %)})
                                              (filter
                                               (complement nil?)
                                               article-from-local-storage))))
                            :fx [get-article-by-id-effect]}))))

(re-frame/reg-event-db
 ::initial-values-set
 (fn [db _]
   (assoc db :initial-values-set true)))

(re-frame/reg-event-db
 ::upload-success
 (fn [db [_ path id]]
   (println "Success!")
   (set! (.. js/window -location -href) (str "/article/" id))
   (fork/set-submitting db path false)))

(re-frame/reg-event-db
 ::edit-success
 (fn [db [_ path listed]]
   (println "edit success")
   (assoc (fork/set-submitting db path false) :listed listed)))

(re-frame/reg-event-db
 ::delete-success
 (fn [db [_ path]]
   (println "delete success")
   (set! (.. js/window -location -href) "/")
   (fork/set-submitting db path false)))

(re-frame/reg-event-fx
 ::upload
 (fn [_ [_ {:keys [values path]}]]
   {:fx [[:fetch {:method :post
                  :url "http://localhost:3001/api/article"
                  :request-content-type :json
                  :headers {"Accept" "application/json"
                            "Origin" "http://localhost:8280"}
                  :body {:id (values "id")
                         :name "New Article"
                         :markdown nil
                         :project-completion 0
                         :listed false}
                  :mode :cors
                  :credentials :omit
                  :on-success [::upload-success path (values "id")]}]]}))

(re-frame/reg-event-fx
 ::article-form-submit
 (fn [{db :db} [_ {:keys [values path]}]]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (js->clj (.stringify js/JSON (.getContents (q/find editor))))]
     (println (values "id"))
     {:db (fork/set-submitting db path true)
      :fx (let [id (-> (.. js/window -location -pathname)
                       (string/split #"/")
                       (last))
                body {:id id
                      :name (values "name")
                      :markdown delta
                      :project-completion (parse-long (str (values "project-completion")))}]
            (case (values "submit-type")
              :delete
              [[:fetch {:method :delete
                        :url (str "http://localhost:3001/api/article/" id)
                        :mode :cors
                        :credentials :omit
                        :on-success [::delete-success path]}]]
              :de-list
              [[:fetch {:method :put
                        :url (str "http://localhost:3001/api/article/" id)
                        :request-content-type :json
                        :headers {"Accept" "application/json"
                                  "Origin" "http://localhost:8280"}
                        :body (assoc body :listed false)
                        :mode :cors
                        :credentials :omit
                        :on-success [::edit-success path false]}]]
              :upload
              [[:fetch {:method :put
                        :url (str "http://localhost:3001/api/article/" id)
                        :request-content-type :json
                        :headers {"Accept" "application/json"
                                  "Origin" "http://localhost:8280"}
                        :body (assoc body :listed true)
                        :mode :cors
                        :credentials :omit
                        :on-success [::edit-success path true]}]]))})))

(re-frame/reg-event-fx
 ::article-form-changed
 (fn [{db :db} [_ name value]]
   {:fx [[::effects/set-article-local-storage
          {:id (:initial-id db)
           :kv {name value}}]]}))