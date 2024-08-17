(ns portfolio-manager.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::active-panel
 (fn [db _]
   (:active-panel db)))

(re-frame/reg-sub
 ::articles
 (fn [db _]
   (:articles db)))

(re-frame/reg-sub
 ::initial-name
 (fn [db _]
   (:initial-name db)))

(re-frame/reg-sub
 ::initial-project-completion
 (fn [db _]
   (:initial-project-completion db)))

(re-frame/reg-sub
 ::initial-values-should-be-set
 (fn [db _]
   (:initial-values-should-be-set db)))

(re-frame/reg-sub
 ::listed
 (fn [db _]
   (:listed db)))

(re-frame/reg-sub
 ::create-validation-failed
 (fn [db _]
   (:create-validation-failed db)))