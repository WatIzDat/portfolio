(ns portfolio-api.routes
  (:require [portfolio-api.handlers.article :as handler]
            [portfolio-api.spec.article :as spec]))

(defn article-routes []
  ["/article" {:post {:handler handler/create
                      :parameters {:body spec/CreateArticleCommand}
                      :responses {200 spec/CreateArticleResponse}}}])