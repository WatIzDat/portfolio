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
 ::set-active-panel
 (fn [db [_ panel-name]]
   (assoc db :active-panel panel-name)))

(re-frame/reg-event-db
 ::initial-values-set
 (fn [db _]
   (assoc db :initial-values-set true)))

(re-frame/reg-event-fx
 ::get-article-by-id-success
 (fn [{db :db} [_ result]]
   (println (:body result))
   (let [article (js->clj (.parse js/JSON (:body result)) :keywordize-keys true)]
     {:db (assoc db
                 :initial-name (:articles/name article)
                 :initial-project-completion (:articles/project_completion article))
      ::effects/set-quill-contents (:articles/markdown article)})))

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

(re-frame/reg-event-db
 ::edit-success
 (fn [db [_ path]]
   (println "edit success")
   (fork/set-submitting db path false)))

(re-frame/reg-event-fx
 ::article-form-submit
 (fn [{db :db} [_ {:keys [values _ path]} edit?]]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (js->clj (.stringify js/JSON (.getContents (q/find editor))))]
     (println (values "id"))
     {:db (fork/set-submitting db path true)
      :fx (if (not edit?)
            [[:fetch {:method :post
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
                      :on-success [::upload-success path]}]]
            (let [id (-> (.. js/window -location -pathname)
                         (string/split #"/")
                         (last))]
              [[:fetch {:method :put
                        :url (str "http://localhost:3001/api/article/" id)
                        :request-content-type :json
                        :headers {"Accept" "application/json"
                                  "Origin" "http://localhost:8280"}
                        :body {:id id
                               :name (values "name")
                               :markdown delta
                               :project-completion (parse-long (str (values "project-completion")))}
                        :mode :cors
                        :credentials :omit
                        :on-success [::edit-success path]}]]))})))