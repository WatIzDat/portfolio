(ns portfolio-manager.events.get-articles
  (:require [portfolio-manager.events.error-handling :as error-handling]
            [re-frame.core :as re-frame]))

(re-frame/reg-event-db
 ::get-articles-success
 (fn [db [_ result]]
   (as-> (:body result) $
     (.parse js/JSON $)
     (js->clj $ :keywordize-keys true)
     (assoc db :articles $))))

(def get-articles
  [:fetch {:method :get
           :url "http://localhost:3001/api/article"
           :mode :cors
           :credentials :omit
           :on-success [::get-articles-success]
           :on-failure [::error-handling/fetch-failure]}])