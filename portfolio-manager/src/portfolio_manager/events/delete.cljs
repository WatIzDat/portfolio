(ns portfolio-manager.events.delete
  (:require [portfolio-manager.effects :as effects]
            [portfolio-manager.events.error-handling :as error-handling]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-fx
 ::delete-success
 (fn [_ [_ id]]
   (println "delete success")
   (set! (.. js/window -location -href) "/")
   {:fx [[::effects/remove-article-from-local-storage id]]}))

(re-frame/reg-event-fx
 ::delete
 (fn [_ [_ id]]
   {:fx [[:fetch {:method :delete
                  :url (str "http://localhost:3001/api/article/" id)
                  :mode :cors
                  :credentials :omit
                  :on-success [::delete-success id]
                  :on-failure [::error-handling/fetch-failure]}]]}))