(ns portfolio-manager.effects
  (:require [re-frame.core :as re-frame]
            ["quill$default" :as quill]
            [portfolio-manager.consts :as consts]))

(re-frame/reg-fx
 ::set-quill-contents
 (fn [contents]
   (let [editor (.querySelector js/document consts/editor-id)
         delta (.parse js/JSON contents)]
     (.setContents (quill/find editor) delta))))