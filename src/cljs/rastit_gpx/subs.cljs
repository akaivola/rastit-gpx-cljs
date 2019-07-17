(ns rastit-gpx.subs
  (:require
   [re-frame.core :as r]
   [reagent.ratom :as ratom]))

(r/reg-sub
 :query-db
 (fn [db [_ path]]
   (if (sequential? path)
     (get-in db path)
     (get db path))))
