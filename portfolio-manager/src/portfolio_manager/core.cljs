(ns portfolio-manager.core
  (:require ["quill$default" :as quill]
            [portfolio-manager.config :as config]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.events :as events]
            [portfolio-manager.routes :as routes]
            [portfolio-manager.views :as views]
            [re-frame.core :as re-frame]
            [reagent.dom :as rdom]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root)
  (routes/app-routes))

(comment
  (js-obj "theme" "snow")
  (js->clj (. (new quill "#editor" (js-obj "theme" "snow")) getContents))
  (new quill consts/editor-id (js-obj "theme" "snow")))
