(ns rastit-gpx.core
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as re-frame]
   [rastit-gpx.events :as events]
   [rastit-gpx.subs]
   [rastit-gpx.config :as config]
   [rastit-gpx.rastit :refer [available-rastit-routes]]))

(defn ui []
  [:div
   [:h1 "Rastit.fi GPX route downloader"]
   [:a {:href "#" :on-click #(js/fetchGpx "9717" "test_9717")} "test"]
   [available-rastit-routes]])

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
