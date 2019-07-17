(ns rastit-gpx.events
  (:require
   [re-frame.core :as r]
   [rastit-gpx.db :as db]
   [taoensso.timbre :refer-macros [spy debug]]))

(r/reg-event-db
 ::initialize-db
 (fn  [_ _]
   db/default-db))

(r/reg-event-db
 :set-db
 (fn [db [_  path value]]
   (assoc-in db
             (if (keyword? path)
               [path]
               path)
             value)))

(r/reg-event-db
 :state-update
 (fn [db [_ f]]
   (or (f db)
       db)))
