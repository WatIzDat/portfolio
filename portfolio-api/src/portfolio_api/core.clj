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
            [portfolio-api.db :refer [db]]))

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

(comment
  (mount/start)
  (mount/stop)
  (jdbc/execute! db ["SELECT version();"]))