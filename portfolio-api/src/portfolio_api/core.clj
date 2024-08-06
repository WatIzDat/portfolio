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
            [portfolio-api.db :refer [db]]
            [ring.middleware.cors :as cors]))

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     {:middleware [[cors/wrap-cors
                    :access-control-allow-origin [#"http://localhost:8280"]
                    :access-control-allow-methods [:get :post :put :delete]
                    :access-control-allow-headers #{"accept"
                                                    "accept-encoding"
                                                    "accept-language"
                                                    "authorization"
                                                    "content-type"
                                                    "origin"}]]}
     (routes/article-routes)]
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