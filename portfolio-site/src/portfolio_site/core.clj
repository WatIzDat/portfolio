(ns portfolio-site.core
  (:require [clojure.java.io :as io]
            [hiccup.page :refer [html5]]
            [stasis.core :as stasis]))

(def page-data
  (let [dirs (seq (.list (io/file "./resources/partials/")))]
    (zipmap dirs
            (map #(slurp (str "./resources/partials/" % "/data.json")) dirs))))

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

(defn index-page [pages]
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "../../css/site.css" :rel "stylesheet"}]
    [:title "Portfolio"]]
   [:body.bg-slate-900.text-yellow-50
    [:div.flex.justify-center.items-center.h-screen
     [:h1.text-9xl.font-black "my portfolio"]]]))

(defn get-pages []
  (println page-data)
  (stasis/merge-page-sources
   {:index {"/index.html" index-page}
    :public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
    :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))}))

(def app (stasis/serve-pages get-pages))