(defproject portfolio-api "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.12.1"]
                 [ring/ring-jetty-adapter "1.12.1"]
                 [metosin/reitit-ring "0.7.1"]
                 [metosin/reitit-schema "0.7.1"]
                 [metosin/reitit-middleware "0.7.1"]
                 [metosin/muuntaja "0.6.10"]
                 [mount "0.1.18"]
                 [prismatic/schema "1.4.1"]
                 [com.github.seancorfield/next.jdbc "1.3.939"]
                 [com.github.seancorfield/honeysql "2.6.1147"]
                 [org.postgresql/postgresql "42.7.3"]
                 [com.mchange/c3p0 "0.10.1"]

                 [org.clojure/tools.namespace "1.5.0"]]
  :repl-options {:init-ns dev})
