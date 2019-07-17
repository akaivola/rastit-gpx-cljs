(ns rastit-gpx.workspaces.main
  (:require
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.card-types.react :as ct.react]
   [re-frame.core :as re-frame]
   [reagent.core :as reagent]
   [rastit-gpx.core :as core]
   [rastit-gpx.events :as events]))

(ws/defcard rastit-gpx
  (do
    (re-frame/dispatch-sync [::events/initialize-db])
    (core/dev-setup)
    (ct.react/react-card
     (reagent/as-element [core/ui]))))

(defonce init (ws/mount))
