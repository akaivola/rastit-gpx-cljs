(ns rastit-gpx.core
  (:require
   [reagent.core :as reagent]
   [rastit-gpx.events :as events]
   [rastit-gpx.subs]
   [rastit-gpx.config :as config]
   [re-frame.core :as re-frame]))

(defn ui []
  [:div
   [:p "Hello Devcards"]])

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")))

(defn mount-root []
  (re-frame/clear-subscription-cache!)
  (reagent/render [ui] (.getElementById js/document "app")))


(defn ^:export init []
  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))
