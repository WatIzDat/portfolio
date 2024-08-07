(ns portfolio-manager.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [portfolio-manager.events :as events]))

(def routes ["/" {"" :dashboard
                  ["article"] :upload-article
                  ["article/" :id] :edit-article}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [panel-name (keyword (str (name (:handler matched-route)) "-panel"))]
    (println (:route-params matched-route))
    (re-frame/dispatch [::events/set-active-panel panel-name (:route-params matched-route)])))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))