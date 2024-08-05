(ns dev
  (:require [mount.core :as mount]
            [clojure.tools.namespace.repl :as tn]))

(defn start []
  (mount/start))

(defn stop []
  (mount/stop))

(defn refresh []
  (stop)
  (tn/refresh))

(defn refresh-all []
  (stop)
  (tn/refresh-all))

(defn go
  "starts all states defined by defstate"
  []
  (start)
  :ready)

(defn reset
  "stops all states defined by defstate, reloads modified source files, and restarts the states"
  []
  (stop)
  (tn/refresh :after 'dev/go))

(mount/in-clj-mode)

(defn load-data-readers!
  "Refresh *data-readers* with readers from newly acquired dependencies."
  []
  (#'clojure.core/load-data-readers)
  (set! *data-readers* (.getRawRoot #'*data-readers*)))

(load-data-readers!)