(ns portfolio-manager.events
  (:require
   [re-frame.core :as re-frame]
   [portfolio-manager.db :as db]
   ["quill$default" :as q]
   [portfolio-manager.consts :as consts]
   [superstructor.re-frame.fetch-fx]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

(re-frame/reg-event-db
 ::upload-success
 (fn [_ _]
   (println "Success!")))

(re-frame/reg-event-fx
 ::upload
 (fn [_ _]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (js->clj (.stringify js/JSON (.getContents (q/find editor))))]
     {:fx [[:fetch {:method :post
                    :url "http://localhost:3001/api/article"
                    :request-content-type :json
                    :headers {"Accept" "application/json"
                              "Origin" "http://localhost:8280"}
                    :body {:id "5"
                           :name "Upload Test"
                           :markdown delta
                           :project-completion 50}
                    :mode :cors
                    :credentials :omit
                    :on-success [::upload-success]}]]})))