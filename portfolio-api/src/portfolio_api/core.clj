(ns portfolio-api.core
  (:require [portfolio-api.data :as data]
            [ring.adapter.jetty :as jetty]
            [reitit.ring :as ring]
            [mount.core :as mount]
            [schema.core :as s]))

(defn handler [_]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello Test"})

(def app
  (ring/ring-handler
   (ring/router
    ["/api"
     ["/article" {:post {:handler handler}}]])))

(mount/defstate server
  :start (jetty/run-jetty app {:port 3001 :join? false})
  :stop (.stop server))

(comment
  (mount/start)
  (mount/stop)
  (s/validate data/Article {:id "test" :name "Test" :markdown 10 :project-completion 10}))