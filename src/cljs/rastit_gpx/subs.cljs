(ns rastit-gpx.subs
  (:require
   [re-frame.core :as r]
   [rastit-gpx.ajax :as ajax]
   [reagent.ratom :as ratom]))

(defn- query-ajax [db request]
  (ajax/http-op (merge
                 request
                 {:handler
                  (fn [response]
                    (let [processed ((or (:handler request) identity) response)]
                      (r/dispatch-sync
                       [:set-db [:http request] processed])
                      processed))}))
  (ratom/make-reaction
   (fn []
     (get-in @db [:http request]))))

(r/reg-sub
 :query-db
 (fn [db [_ path]]
   (if (sequential? path)
     (get-in db path)
     (get db path))))

(r/reg-sub-raw
 :http
 (fn [db [_ request]]
   (query-ajax db request)))

(r/reg-sub-raw
 :http-rastit
 (fn [db [_ request]]
   (query-ajax
    db
    (update
     request
     :url
     (fn [url]
       (str "https://cors-anywhere.herokuapp.com/https://www.rastit.fi" url))))))
