(ns portfolio-manager.events.get-article-by-id
  (:require [re-frame.core :as re-frame]
            [portfolio-manager.effects :as effects]
            [portfolio-manager.events.error-handling :as error-handling]))

(re-frame/reg-event-fx
 ::get-article-by-id-success
 (fn [{db :db} [_ result]]
   (println (:body result))
   (let [article (js->clj (.parse js/JSON (:body result)) :keywordize-keys true)]
     (println "2nd")
     (println (:articles/project_completion article))
     {:db (assoc db
                 :initial-name (or (:initial-name db) (:articles/name article))
                 :initial-project-completion (or (:initial-project-completion db) (:articles/project_completion article))
                 :listed (:articles/listed article)
                 :initial-values-should-be-set true)
      ::effects/set-quill-contents
      (if (nil? (:initial-markdown db))
        (:articles/markdown article)
        (.stringify js/JSON (clj->js (:initial-markdown db))))})))

(re-frame/reg-event-fx
 ::get-article-by-id-failure
 (fn [_ [_ result]]
   (when (= (:status result) 404)
     {:fx [[:dispatch [::error-handling/set-not-found-panel]]]})))

(defn get-article-by-id [id]
  [:fetch {:method :get
           :url (str "http://localhost:3001/api/article/" id)
           :mode :cors
           :credentials :omit
           :on-success [::get-article-by-id-success]
           :on-failure [::get-article-by-id-failure]}])