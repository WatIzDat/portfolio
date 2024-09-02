(ns portfolio-manager.events.article-form
  (:require [fork.re-frame :as fork]
            [portfolio-manager.coeffects :as coeffects]
            [portfolio-manager.effects :as effects]
            [portfolio-manager.events.delete :as delete]
            [portfolio-manager.events.edit :as edit]
            [portfolio-manager.events.error-handling :as error-handling]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::article-form-submit
 [(re-frame/inject-cofx ::coeffects/delta)
  (re-frame/inject-cofx ::coeffects/html)
  (re-frame/inject-cofx ::coeffects/id)]
 (fn [{:keys [db delta html id]} [_ {:keys [values path]}]]
   {:db (fork/set-submitting db path true)
    :fx (let [body {:id id
                    :name (values "name")
                    :markdown delta
                    :html html
                    :project-completion (parse-long (str (values "project-completion")))}]
          (case (values "submit-type")
            :delete
            [[:fetch {:method :delete
                      :url (str "http://localhost:3001/api/article/" id)
                      :mode :cors
                      :credentials :omit
                      :on-success [::delete/delete-success path]
                      :on-failure [::error-handling/fetch-failure]}]]
            :save
            [[:fetch {:method :put
                      :url (str "http://localhost:3001/api/article/" id)
                      :request-content-type :json
                      :headers {"Accept" "application/json"
                                "Origin" "http://localhost:8280"}
                      :body (assoc body :listed (values "listed"))
                      :mode :cors
                      :credentials :omit
                      :on-success [::edit/edit-success path (values "listed")]
                      :on-failure [::error-handling/fetch-failure]}]]
            :de-list
            [[:fetch {:method :put
                      :url (str "http://localhost:3001/api/article/" id)
                      :request-content-type :json
                      :headers {"Accept" "application/json"
                                "Origin" "http://localhost:8280"}
                      :body (assoc body :listed false)
                      :mode :cors
                      :credentials :omit
                      :on-success [::edit/edit-success path false]
                      :on-failure [::error-handling/fetch-failure]}]]
            :upload
            [[:fetch {:method :put
                      :url (str "http://localhost:3001/api/article/" id)
                      :request-content-type :json
                      :headers {"Accept" "application/json"
                                "Origin" "http://localhost:8280"}
                      :body (assoc body :listed true)
                      :mode :cors
                      :credentials :omit
                      :on-success [::edit/edit-success path true]
                      :on-failure [::error-handling/fetch-failure]}]]))}))

(re-frame/reg-event-fx
 ::article-form-changed
 (fn [{db :db} [_ name value]]
   {:fx [[::effects/set-article-local-storage
          {:id (:initial-id db)
           :kv {name value}}]]}))

(re-frame/reg-event-db
 ::confirm-delete
 (fn [db _]
   (assoc db :confirm-delete true)))