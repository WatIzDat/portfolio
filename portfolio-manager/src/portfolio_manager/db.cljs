(ns portfolio-manager.db)

(def default-db
  {:active-panel :dashboard-panel
   :initial-values-should-be-set false})

(def local-storage-key "portfolio-manager")
(def local-storage-article-separator ":")
(def local-storage-internal-separator ";")

(defn set-article-local-storage [id map]
  (let [ls-key (str local-storage-key local-storage-article-separator id)
        article (js->clj (.parse js/JSON (.getItem js/localStorage ls-key)) :keywordize-keys true)
        new-article {:id (or id (:id article))
                     :name (or (:name map) (:name article))
                     :markdown (or (:markdown map) (:markdown article))
                     :project-completion (or (:project-completion map) (:project-completion article))}]

    (.setItem js/localStorage ls-key (.stringify js/JSON (clj->js new-article)))))

(defn set-article-order-local-storage
  ([]
   (let [ls-key (str local-storage-key local-storage-internal-separator "article-order")]
     (.setItem js/localStorage ls-key (.stringify js/JSON (clj->js [])))))
  ([newest-id]
   (let [ls-key (str local-storage-key local-storage-internal-separator "article-order")
         item (.parse js/JSON (.getItem js/localStorage ls-key))]
     (if (nil? item)
       (.setItem js/localStorage ls-key (.stringify js/JSON (clj->js [newest-id])))
       (.setItem js/localStorage ls-key (.stringify js/JSON (clj->js (cons newest-id (js->clj item)))))))))

(defn get-article-from-local-storage [id]
  (js->clj (.parse js/JSON (.getItem js/localStorage (str local-storage-key local-storage-article-separator id))) :keywordize-keys true))

(defn get-article-order-from-local-storage []
  (let [article-order (.getItem js/localStorage (str local-storage-key local-storage-internal-separator "article-order"))]
    (if article-order
      (js->clj (.parse js/JSON article-order))
      (do
        (set-article-order-local-storage)
        []))))

(defn remove-article-from-local-storage [id]
  (.removeItem js/localStorage (str local-storage-key local-storage-article-separator id)))