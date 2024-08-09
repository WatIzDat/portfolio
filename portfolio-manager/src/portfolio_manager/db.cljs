(ns portfolio-manager.db)

(def default-db
  {:active-panel :dashboard-panel
   :initial-values-set false})

(def local-storage-key "portfolio-manager")
(def local-storage-separator ":")

(defn set-article-local-storage [id map]
  (let [ls-key (str local-storage-key local-storage-separator id)
        article (js->clj (.parse js/JSON (.getItem js/localStorage ls-key)) :keywordize-keys true)
        new-article {:id (or id (:id article))
                     :name (or (:name map) (:name article))
                     :markdown (or (:markdown map) (:markdown article))
                     :project-completion (or (:project-completion map) (:project-completion article))}]

    (.setItem js/localStorage ls-key (.stringify js/JSON (clj->js new-article)))))

(defn get-article-from-local-storage [id]
  (js->clj (.parse js/JSON (.getItem js/localStorage (str local-storage-key local-storage-separator id))) :keywordize-keys true))