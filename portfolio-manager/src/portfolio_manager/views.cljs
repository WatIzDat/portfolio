(ns portfolio-manager.views
  (:require
   [re-frame.core :as re-frame]
   [portfolio-manager.subs :as subs]
   [trix]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div.flex.flex-col.justify-center.items-center.h-screen
     [:trix-toolbar {:id "toolbar"}]
     [:trix-editor {:class "w-2/3 h-2/3" :toolbar "toolbar"}]]))
