(ns portfolio-api.handlers.article
  (:require [portfolio-api.db :refer [db]]
            [next.jdbc :as jdbc]
            [honey.sql :as sql]))

(defn create [{{{:keys [id name markdown project-completion]} :body} :parameters}]
  (jdbc/execute!
   db
   (sql/format {:insert-into [:articles]
                :values [{:id id
                          :name name
                          :markdown markdown
                          :project-completion project-completion}]}))
  {:status 200
   :body id})

(defn get-all-no-markdown [_]
  {:status 200
   :body (jdbc/execute!
          db
          (sql/format {:select [:id :name :project-completion]
                       :from [:articles]}))})

(defn get-only-markdown-by-id [{{{:keys [id]} :path} :parameters}]
  {:status 200
   :body (:articles/markdown (jdbc/execute-one!
                              db
                              (sql/format {:select [:markdown]
                                           :from [:articles]
                                           :where [:= :id id]})))})

(comment
  (jdbc/execute!
   db
   (sql/format {:select [:markdown]
                :from [:articles]})))