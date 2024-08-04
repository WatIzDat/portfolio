(ns portfolio-api.handlers.article
  (:require [portfolio-api.core :refer [db]]
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
   :body "Hello"})