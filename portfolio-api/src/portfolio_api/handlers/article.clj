(ns portfolio-api.handlers.article
  (:require [portfolio-api.db :refer [db]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]))

(defn create [{{{:keys [id name markdown project-completion listed]} :body} :parameters}]
  (jdbc/execute!
   db
   (sql/format {:insert-into [:articles]
                :values [{:id id
                          :name name
                          :markdown markdown
                          :project-completion project-completion
                          :listed listed}]}))
  {:status 200
   :body id})

(defn get-all-no-markdown [_]
  {:status 200
   :body (jdbc/execute!
          db
          (sql/format {:select [:id :name :project-completion :listed]
                       :from [:articles]}))})

(defn get-by-id [{{{:keys [id]} :path} :parameters}]
  {:status 200
   :body (jdbc/execute-one!
          db
          (sql/format {:select [:name :markdown :project-completion :listed]
                       :from [:articles]
                       :where [:= :id id]}))})

(defn delete [{{{:keys [id]} :path} :parameters}]
  (jdbc/execute!
   db
   (sql/format {:delete-from [:articles]
                :where [:= :id id]}))
  {:status 204})

(defn edit [{{{:keys [id name markdown project-completion listed]} :body
              {prev-id :id} :path} :parameters}]
  (println prev-id)
  (jdbc/execute!
   db
   (sql/format {:update :articles
                :set {:id id
                      :name name
                      :markdown markdown
                      :project-completion project-completion
                      :listed listed}
                :where [:= :id prev-id]}))
  {:status 204})

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