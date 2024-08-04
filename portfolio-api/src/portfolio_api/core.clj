(ns portfolio-api.core
  (:require [mount.core :as mount]
            [portfolio-api.routes :as routes]
            [reitit.coercion.schema]
            [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as rrc]
            [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(def ^:private db-spec {:dbtype "postgresql"
                        :dbname "portfolio"
                        :user "postgres"
                        :password "password"})

(def app
  (ring/ring-handler
   (ring/router
    ["/api" (routes/article-routes)]
    {:data {:muuntaja m/instance
            :coercion reitit.coercion.schema/coercion
            :middleware [muuntaja/format-middleware
                         parameters/parameters-middleware
                         rrc/coerce-exceptions-middleware
                         rrc/coerce-request-middleware
                         rrc/coerce-response-middleware]}})))

(mount/defstate server
  :start (jetty/run-jetty app {:port 3001 :join? false})
  :stop (.stop server))

(mount/defstate db
  :start (connection/->pool ComboPooledDataSource db-spec)
  :stop (.close db))

(comment
  (mount/start)
  (mount/stop)
  (jdbc/execute! db ["SELECT version();"]))