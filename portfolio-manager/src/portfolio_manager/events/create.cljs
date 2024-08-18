(ns portfolio-manager.events.create
  (:require [fork.re-frame :as fork]
            [portfolio-manager.events.error-handling :as error-handling]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::create-success
 (fn [db [_ path id]]
   (println "Success!")
   (set! (.. js/window -location -href) (str "/article/" id))
   (fork/set-submitting db path false)))

(re-frame/reg-event-fx
 ::create
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
                  :on-success [::create-success path (values "id")]
                  :on-failure [::error-handling/fetch-failure]}]]}))

(re-frame/reg-event-db
 ::reset-create-validation
 (fn [db _]
   (println "reset-create-validation")
   (assoc db :create-validation-failed false)))

(re-frame/reg-event-db
 ::create-validation-failure
 (fn [db [_ path]]
   (assoc (fork/set-waiting db path "id" false) :create-validation-failed true)))

(re-frame/reg-event-fx
 ::create-validation-success
 (fn [{db :db} [_ path problem]]
   (if (= (:status problem) 404)
     {:db (assoc (fork/set-waiting db path "id" false) :create-validation-failed false)}
     {:fx [[:dispatch [::error-handling/fetch-failure problem]]]})))

(re-frame/reg-event-fx
 ::validate-create-request
 (fn [_ [_ {:keys [values path]}]]
   {:fx [[:fetch {:method :get
                  :url (str "http://localhost:3001/api/article/" (values "id"))
                  :mode :cors
                  :credentials :omit
                  :on-success [::create-validation-failure path]
                  :on-failure [::create-validation-success path]}]]}))