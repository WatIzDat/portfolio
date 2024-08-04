(ns portfolio-api.spec.article
  (:require [schema.core :as s]))

(s/defschema Article
  {:id s/Str
   :name s/Str
   :markdown s/Str
   :project-completion (s/constrained s/Int (fn less-than-100? [x] (<= x 100)))})

(s/defschema CreateArticleCommand Article)
(s/defschema CreateArticleResponse {:body s/Str})