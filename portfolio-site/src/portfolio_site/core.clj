(ns portfolio-site.core
  (:require [clojure.java.io :as io]
            [hiccup.page :refer [html5]]
            [stasis.core :as stasis]
            [cheshire.core :as cheshire]))

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

(defn index-page [page-data pages]
  (println pages)
  (html5
   [:head
    [:meta {:charset "utf-8"}]
    [:meta {:name "viewport"
            :content "width=device-width, initial-scale=1.0"}]
    [:link {:href "../../css/site.css" :rel "stylesheet"}]
    [:title "Portfolio"]]
   [:body.bg-slate-900.text-yellow-50
    [:div.flex.flex-col.justify-center.items-center.h-screen.gap-8
     [:h1.text-9xl.font-black "my portfolio"]
     [:div
      {:class "w-1/2"}
      [:h2 "Completed Projects"]
      [:ul.list-none.ml-0.border-4.border-yellow-50.rounded-lg.p-4.flex.flex-col.gap-4
       (->> page-data
            (filter #(= (:project-completion (val %)) 100))
            (map
             (fn [x]
               [:li
                [:button.bg-slate-800.py-4.rounded-lg.w-full
                 {:onclick (format "window.location.href='/%s'" (key x))}
                 [:h3.text-center.m-0 (:name (val x))]
                 [:div.preview.line-clamp-2.overflow-auto
                  {:inert ""}
                  (pages (format "/%s/index.html" (key x)))]]])))]

      [:h2.mt-8 "Work-in-progress Projects"]
      [:ul.list-none.ml-0.border-4.border-yellow-50.rounded-lg.p-4.flex.flex-col.gap-4
       (->> page-data
            (filter #(< (:project-completion (val %)) 100))
            (map
             (fn [x]
               [:li
                [:button.bg-slate-800.py-4.rounded-lg.w-full
                 {:onclick (format "window.location.href='/%s'" (key x))}
                 [:h3.text-center.m-0 (:name (val x))]
                 [:div.preview.line-clamp-2.overflow-auto
                  {:inert ""}
                  (pages (format "/%s/index.html" (key x)))]]])))]]]]))

(defn get-pages []
  (let [dirs (seq (.list (io/file "./resources/partials/")))
        page-data (->> (zipmap dirs
                               (map #(slurp (str "./resources/partials/" % "/data.json")) dirs))
                       (map #(first {(key %) (cheshire/parse-string (val %) true)})))]
    (println page-data)
    (stasis/merge-page-sources
     {:index {"/index.html" (index-page page-data (stasis/slurp-directory "resources/partials" #".*\.html$"))}
      :public (stasis/slurp-directory "resources/public" #".*\.(html|css|js)$")
      :partials (partial-pages (stasis/slurp-directory "resources/partials" #".*\.html$"))})))

(def app (stasis/serve-pages get-pages))