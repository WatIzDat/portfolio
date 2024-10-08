(ns portfolio-api.handlers.article
  (:require [portfolio-api.db :refer [db]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]))

(defn create [{{{:keys [id name markdown project-completion listed]} :body} :parameters}]
  (let [exists (jdbc/execute-one!
                db
                (sql/format
                 {:select 1
                  :from [:articles]
                  :where [:= :id id]}))]
    (when (nil? exists)
      (jdbc/execute!
       db
       (sql/format {:insert-into [:articles]
                    :values [{:id id
                              :name name
                              :markdown markdown
                              :project-completion project-completion
                              :listed listed}]})))
    {:status (if (nil? exists) 200 409)
     :body id}))

(defn get-all-no-markdown [_]
  {:status 200
   :body (jdbc/execute!
          db
          (sql/format {:select [:id :name :project-completion :listed]
                       :from [:articles]}))})

(defn get-by-id [{{{:keys [id]} :path} :parameters}]
  (let [article (jdbc/execute-one!
                 db
                 (sql/format {:select [:name :markdown :project-completion :listed]
                              :from [:articles]
                              :where [:= :id id]}))]
    (if (nil? article)
      {:status 404}
      {:status 200
       :body article})))

(defn delete [{{{:keys [id]} :path} :parameters}]
  (let [exists (jdbc/execute-one!
                db
                (sql/format
                 {:select 1
                  :from [:articles]
                  :where [:= :id id]}))]
    (when exists
      (jdbc/execute!
       db
       (sql/format {:delete-from [:articles]
                    :where [:= :id id]})))
    {:status (if exists 204 404)}))

(defn edit [{{{:keys [id name markdown project-completion listed]} :body
              {prev-id :id} :path} :parameters}]
  (let [exists (jdbc/execute-one!
                db
                (sql/format
                 {:select 1
                  :from [:articles]
                  :where [:= :id prev-id]}))]
    (when exists
      (jdbc/execute!
       db
       (sql/format {:update :articles
                    :set {:id id
                          :name name
                          :markdown markdown
                          :project-completion project-completion
                          :listed listed}
                    :where [:= :id prev-id]})))
    {:status (if exists 204 404)}))

(comment
  (jdbc/execute!
   db
   (sql/format {:update :articles
                :set {:id "this-doesnt-exist"
                      :name "def"
                      :markdown "ghi"
                      :project-completion 9}
                :where [:= :id "testtest"]}))
  (jdbc/execute!
   db
   (sql/format {:select [:name :markdown :project-completion]
                :from [:articles]
                :where [:= :id "does-this-work"]})))