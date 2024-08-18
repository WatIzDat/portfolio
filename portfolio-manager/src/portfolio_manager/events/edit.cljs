(ns portfolio-manager.events.edit
  (:require [fork.re-frame :as fork]
            [portfolio-manager.effects :as effects]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::edit-success
 (fn [{db :db} [_ path listed]]
   (println "edit success")
   {:db (assoc (fork/set-submitting db path false) :listed listed)
    :fx [[::effects/toast {:status :success
                           :msg "Success!"}]]}))