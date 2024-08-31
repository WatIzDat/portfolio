(ns portfolio-site.core
  (:require [portfolio-site.routes :as routes]
            [portfolio-site.state :as state]
            [rum.core :as rum]))

(rum/defc home-panel []
  [:<>
   [:h1.ml-60.text-red-500 "Home"]
   [:h2 "yipee i think this finally works"]
   [:p "test"]])

(rum/defc test-panel []
  [:h1 "Test"])

(rum/defc not-found-panel []
  [:h1 "Not Found"])

(defmulti panels identity)
(defmethod panels :home [] [(home-panel)])
(defmethod panels :test [] [(test-panel)])
(defmethod panels :not-found [] [(not-found-panel)])

(rum/defc main-panel < rum/reactive []
  (panels (rum/react state/panel)))

(defn ^:dev/after-load mount-root []
  (rum/mount (main-panel) (.getElementById js/document "app")))

(defn init []
  (mount-root)
  (routes/app-routes))