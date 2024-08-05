(ns portfolio-api.db
  (:require [next.jdbc.connection :as connection]
            [mount.core :as mount])
  (:import (com.mchange.v2.c3p0 ComboPooledDataSource)))

(def ^:private db-spec {:dbtype "postgresql"
                        :dbname "portfolio"
                        :user "postgres"
                        :password "password"})

(mount/defstate db
  :start (connection/->pool ComboPooledDataSource db-spec)
  :stop (.close db))