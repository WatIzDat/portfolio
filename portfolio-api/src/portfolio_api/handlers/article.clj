(ns portfolio-api.handlers.article
  (:require [clojure.java.io :as io]
            [honey.sql :as sql]
            [next.jdbc :as jdbc]
            [portfolio-api.db :refer [db]]
            [cheshire.core :as cheshire]))

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
                    :where [:= :id id]}))
      (let [dir (str "../portfolio-site/resources/partials/" id)]
        (io/delete-file (str dir "/index.html") true)
        (io/delete-file (str dir "/data.json") true)
        (io/delete-file dir)))
    {:status (if exists 204 404)}))

(defn edit [{{{:keys [id name markdown project-completion listed html]} :body
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
                    :where [:= :id prev-id]}))
      (let [dir (str "../portfolio-site/resources/partials/" id)
            index (str dir "/index.html")
            data (str dir "/data.json")]
        (if listed
          (do
            (.mkdir (io/file dir))
            (spit index html)
            (spit
             data
             (cheshire/generate-string
              {:name name :project-completion project-completion})))
          (do
            (io/delete-file index true)
            (io/delete-file data true)
            (io/delete-file dir)))))
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
                :where [:= :id "does-this-work"]}))
  (seq (.list (io/file "../portfolio-site/resources/partials")))
  (.mkdir (io/file "../portfolio-site/resources/partials/test"))
  (spit "../portfolio-site/resources/partials/test/index.html" "test"))