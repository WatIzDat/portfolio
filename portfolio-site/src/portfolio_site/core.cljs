(ns portfolio-site.core
  (:require [portfolio-site.routes :as routes]
            [portfolio-site.state :as state]
            [rum.core :as rum]))

(rum/defc home-panel []
  [:div.flex.justify-center.items-center.h-screen
   [:h1.text-9xl.font-black.drop-shadow- "my portfolio"]])

(rum/defc not-found-panel []
  [:h1 "Not Found"])

(defmulti panels identity)
(defmethod panels :home [] [(home-panel)])
(defmethod panels :not-found [] [(not-found-panel)])

(rum/defc main-panel < rum/reactive []
  (panels (rum/react state/panel)))

(defn ^:dev/after-load mount-root []
  (rum/mount (main-panel) (.getElementById js/document "app")))

(defn init []
  (mount-root)
  (routes/app-routes))