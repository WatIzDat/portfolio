(ns portfolio-api.routes
  (:require [portfolio-api.handlers.article :as handler]
            [portfolio-api.spec.article :as spec]))

(defn article-routes []
  ["/article"
   ["" {:post {:handler handler/create
               :parameters {:body spec/create-article-command}
               :responses {200 spec/create-article-response}}
        :get {:handler handler/get-all-no-markdown
              :responses {200 spec/get-all-no-markdown-response}}}]
   ["/:id" {:get {:handler handler/get-by-id
                  :parameters {:path spec/get-by-id-request}
                  :responses {200 spec/get-by-id-response}}
            :delete {:handler handler/delete
                     :parameters {:path spec/delete-article-command}
                     :responses {204 nil}}
            :put {:handler handler/edit
                  :parameters {:path spec/edit-article-command-path
                               :body spec/edit-article-command-body}
                  :responses {204 nil}}}]])