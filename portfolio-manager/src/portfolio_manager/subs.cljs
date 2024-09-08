(ns portfolio-manager.subs
  (:require [portfolio-manager.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::articles
 (fn [db _]
   (:articles db)))

(re-frame/reg-sub
 ::ordered-articles
 :<- [::articles]
 (fn [articles _]
   (println articles)
   (->> articles
        (group-by
         #(.indexOf (db/get-article-order-from-local-storage) (:articles/id %)))
        (sort-by key)
        (partition-by #(= (key %) -1))
        (reverse)
        (apply concat)
        (mapcat second))))

(re-frame/reg-sub
 ::initial-name
 (fn [db _]
   (:initial-name db)))

(re-frame/reg-sub
 ::initial-project-completion
 (fn [db _]
   (:initial-project-completion db)))

(re-frame/reg-sub
 ::initial-values-should-be-set
 (fn [db _]
   (:initial-values-should-be-set db)))

(re-frame/reg-sub
 ::listed
 (fn [db _]
   (:listed db)))

(re-frame/reg-sub
 ::create-validation-failed
 (fn [db _]
   (:create-validation-failed db)))

(re-frame/reg-sub
 ::confirm-delete
 (fn [db _]
   (:confirm-delete db)))