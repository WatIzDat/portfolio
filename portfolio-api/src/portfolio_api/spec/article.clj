(ns portfolio-api.spec.article
  (:require [schema.core :as s]))

(s/defschema Article
  {:id s/Str
   :name s/Str
   :markdown (s/maybe s/Str)
   :project-completion (s/constrained s/Int (fn less-than-100? [x] (<= x 100)))
   :listed s/Bool})

(s/defschema CreateArticleCommand Article)
(s/defschema CreateArticleResponse {:body (:id Article)})

(s/defschema GetAllNoMarkdownResponse (dissoc Article :markdown))

(s/defschema GetByIdRequest {:id (:id Article)})
(s/defschema GetByIdResponse (dissoc Article :id))

(s/defschema DeleteArticleCommand {:id (:id Article)})

(s/defschema EditArticleCommandPath {:id (:id Article)})
(s/defschema EditArticleCommandBody Article)