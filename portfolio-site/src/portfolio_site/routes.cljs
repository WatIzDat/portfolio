(ns portfolio-site.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [portfolio-site.state :as state]))

(def routes ["/" [["" :home]
                  [true :not-found]]])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- set-active-panel [matched-route]
  (println (:handler matched-route))
  (reset! state/panel (:handler matched-route)))

(defn app-routes []
  (pushy/start! (pushy/pushy set-active-panel parse-url)))

(comment
  (parse-url "/test"))