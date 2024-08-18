(ns portfolio-manager.effects
  (:require [re-frame.core :as re-frame]
            ["quill$default" :as quill]
            [portfolio-manager.consts :as consts]
            [portfolio-manager.db :as db]
            ["react-hot-toast$default" :as toast]))

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

(re-frame/reg-fx
 ::remove-article-from-local-storage
 (fn [id]
   (db/remove-article-from-local-storage id)))

(re-frame/reg-fx
 ::toast
 (fn [map]
   (if (= (:status map) :success)
     (toast/success (:msg map))
     (toast/error (:msg map)))))