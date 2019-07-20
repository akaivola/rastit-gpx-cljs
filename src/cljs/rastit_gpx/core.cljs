(ns rastit-gpx.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [rastit-gpx.events :as events]
   [rastit-gpx.subs]
   [rastit-gpx.config :as config]
   [rastit-gpx.rastit :refer [route-displays]]))

(defn ui []
  [:div.ui
   [:h1 "Rastit.fi GPX route downloader"]
   [route-displays]])

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
