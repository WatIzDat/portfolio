(ns portfolio-manager.coeffects
  (:require ["quill$default" :as q]
            [clojure.string :as string]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.db :as db]
            [re-frame.core :as re-frame]))

(re-frame/reg-cofx
 ::delta
 (fn [coeffects _]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (js->clj (.stringify js/JSON (.getContents (q/find editor))))]
     (assoc coeffects :delta delta))))

(re-frame/reg-cofx
 ::html
 (fn [coeffects _]
   (let [editor (.querySelector js/document consts/editor-id)
         html (.getSemanticHTML (q/find editor))]
     (assoc coeffects :html html))))

(re-frame/reg-cofx
 ::id
 (fn [coeffects _]
   (let [id (-> (.. js/window -location -pathname)
                (string/split #"/")
                (last))]
     (assoc coeffects :id id))))

(re-frame/reg-cofx
 ::get-article-from-local-storage
 (fn [coeffects _]
   (assoc coeffects :get-article-from-local-storage (partial db/get-article-from-local-storage))))