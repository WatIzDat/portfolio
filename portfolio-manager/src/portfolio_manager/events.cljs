(ns portfolio-manager.events
  (:require
   [re-frame.core :as re-frame]
   [portfolio-manager.db :as db]
   ))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))
