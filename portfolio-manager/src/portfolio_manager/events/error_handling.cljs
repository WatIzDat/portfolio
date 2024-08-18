(ns portfolio-manager.events.error-handling
  (:require [portfolio-manager.effects :as effects]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::fetch-failure
 (fn [_ [_ problem]]
   {:fx [[::effects/toast
          {:status :error
           :msg (case (:status problem)
                  400 "There was a problem with your request."
                  404 "The requested resource was not found."
                  409 "The requested resource already exists."
                  500 "There was an internal server error.")}]]}))

(re-frame/reg-event-db
 ::set-not-found-panel
 (fn [db _]
   (assoc db :active-panel :not-found)))