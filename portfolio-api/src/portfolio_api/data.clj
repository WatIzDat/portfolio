(ns portfolio-api.data
  (:require [schema.core :as s]))

(s/defschema Article
  {(s/required-key :id) s/Str
   (s/required-key :name) s/Str
   (s/required-key :markdown) s/Str
   (s/required-key :project-completion) (s/constrained s/Num (fn less-than-one [x] (<= x 1)))})