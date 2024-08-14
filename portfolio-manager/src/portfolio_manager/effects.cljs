(ns portfolio-manager.effects
  (:require [re-frame.core :as re-frame]
            ["quill$default" :as quill]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.db :as db]))

(re-frame/reg-fx
 ::set-quill-contents
 (fn [contents]
   (if (nil? contents)
     nil
     (let [editor (.querySelector js/document consts/editor-id)
           delta (.parse js/JSON contents)]
       (println delta)
       (.setContents (quill/find editor) delta)))))

(re-frame/reg-fx
 ::set-article-local-storage
 (fn [map]
   (println (:kv map))
   (db/set-article-local-storage (:id map) (:kv map))))