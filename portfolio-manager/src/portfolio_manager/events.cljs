(ns portfolio-manager.events
  (:require
   [re-frame.core :as re-frame]
   [portfolio-manager.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::initialize-editor-data
 (fn [db [_ quill]]
   (assoc db :delta (js->clj (.stringify js/JSON (.getContents ^Delta quill))))))

(re-frame/reg-event-db
 ::upload
 (fn [_ [_ delta]]
   (println delta)))