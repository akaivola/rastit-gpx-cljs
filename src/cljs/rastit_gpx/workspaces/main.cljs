(ns rastit-gpx.workspaces
  (:require
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.card-types.react :as ct.react]
   [reagent.core :as reagent]
   [rastit-gpx.core :as core]))

(ws/defcard rastit-gpx
  (do
    (re-frame/dispatch-sync [::events/initialize-db])
    (core/dev-setup)
    (ct.react/react-card
     (reagent/as-element [core/ui]))))

(defonce init (ws/mount))
