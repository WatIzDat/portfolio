(ns portfolio-api.spec.article
  (:require [malli.core :as m]
            [malli.util :as mu]))

(def article
  (m/schema
   [:map
    [:id [:string {:min 1}]]
    [:name :string]
    [:markdown [:maybe :string]]
    [:project-completion [:int {:min 0 :max 100}]]
    [:listed :boolean]]))

(def create-article-command article)
(def create-article-response (mu/select-keys article [:id]))

(def get-all-no-markdown-response (mu/dissoc article :markdown))

(def get-by-id-request (mu/select-keys article [:id]))
(def get-by-id-response (mu/dissoc article :id))

(def delete-article-command (mu/select-keys article [:id]))

(def edit-article-command-path (mu/select-keys article [:id]))
(def edit-article-command-body article)