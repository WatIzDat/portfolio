(ns portfolio-site.core
  (:require [stasis.core :as stasis]
            [hiccup.page :refer [html5]]))

(defn layout-page [page]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "../../css/site.css" :rel "stylesheet"}]
    [:title "Portfolio"]]
   [:body.bg-slate-900.text-yellow-50
    [:div
     {:id "body"
      :class "w-5/6 mx-auto my-0 break-words"}
     page]]))

(defn partial-pages [pages]
  (zipmap (keys pages)
          (map layout-page (vals pages))))

(defn get-pages []
  (stasis/merge-page-sources
   {:public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))}))

(def app (stasis/serve-pages get-pages))