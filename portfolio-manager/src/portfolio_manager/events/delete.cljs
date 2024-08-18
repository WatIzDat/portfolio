(ns portfolio-manager.events.delete
  (:require [portfolio-manager.effects :as effects]
            [portfolio-manager.events.error-handling :as error-handling]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::delete-success
 (fn [_ [_ id]]
   (println "delete success")
   {:fx [[::effects/remove-article-from-local-storage id]
         [::effects/set-window-location "/"]]}))

(re-frame/reg-event-fx
 ::delete
 (fn [{db :db} [_ id id-confirmation]]
   (let [delete-confirmed (= id id-confirmation)]
     {:db (if delete-confirmed
            db
            (assoc db :confirm-delete false))
      :fx [(if delete-confirmed
             [:fetch {:method :delete
                      :url (str "http://localhost:3001/api/article/" id)
                      :mode :cors
                      :credentials :omit
                      :on-success [::delete-success id]
                      :on-failure [::error-handling/fetch-failure]}]
             [::effects/toast {:status :error :msg "The ID didn't match. Cancelling delete."}])]})))