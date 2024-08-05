(ns portfolio-api.routes
  (:require [portfolio-api.handlers.article :as handler]
            [portfolio-api.spec.article :as spec]))

(defn article-routes []
  ["/article"
   ["" {:post {:handler handler/create
               :parameters {:body spec/CreateArticleCommand}
               :responses {200 spec/CreateArticleResponse}}
        :get {:handler handler/get-all-no-markdown
              :responses {200 spec/GetAllNoMarkdownResponse}}}]
   ["/:id" {:get {:handler handler/get-only-markdown-by-id
                  :parameters {:path spec/GetOnlyMarkdownByIdRequest}
                  :responses {200 spec/GetOnlyMarkdownByIdResponse}}}]])