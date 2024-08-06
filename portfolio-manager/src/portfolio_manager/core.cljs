(ns portfolio-manager.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [portfolio-manager.events :as events]
   [portfolio-manager.views :as views]
   [portfolio-manager.config :as config]
   ["quill$default" :as quill]))


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
  (re-frame/dispatch-sync
   [::events/initialize-editor-data
    (new quill "#editor" (js-obj "theme" "snow"))]))

(comment
  (js-obj "theme" "snow")
  (js->clj (. (new quill "#editor" (js-obj "theme" "snow")) getContents)))
