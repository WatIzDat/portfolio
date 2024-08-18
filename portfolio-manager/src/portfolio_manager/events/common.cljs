(ns portfolio-manager.events.common
  (:require [portfolio-manager.coeffects :as coeffects]
            [portfolio-manager.db :as db]
            [portfolio-manager.events.get-article-by-id :as get-article-by-id]
            [portfolio-manager.events.get-articles :as get-articles]
            [re-frame.core :as re-frame]
            [superstructor.re-frame.fetch-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-fx
 ::set-active-panel
 [(re-frame/inject-cofx ::coeffects/get-article-from-local-storage)]
 (fn [{:keys [db get-article-from-local-storage]} [_ panel-name {:keys [id]}]]
   (let [set-page (assoc db :active-panel panel-name)
         article-from-local-storage (get-article-from-local-storage id)]
     (println (:markdown article-from-local-storage))
     (case panel-name
       :dashboard-panel {:db set-page
                         :fx [get-articles/get-articles]}
       :upload-article-panel {:db set-page}
       :edit-article-panel {:db (merge (assoc set-page :initial-id id :initial-markdown (:markdown article-from-local-storage))
                                       (into {}
                                             (map
                                              #(first {(keyword (str "initial-" (apply str (rest (str (key %)))))) (val %)})
                                              (filter
                                               (complement nil?)
                                               article-from-local-storage))))
                            :fx [(get-article-by-id/get-article-by-id id)]}
       :not-found-panel {:db set-page}))))

(re-frame/reg-event-db
 ::initial-values-set
 (fn [db _]
   (assoc db :initial-values-should-be-set false)))
