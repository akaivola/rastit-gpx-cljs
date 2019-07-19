(ns rastit-gpx.rastit
  (:require
   [reagent.core :as reagent]
   [re-frame.core :as r]
   [hickory.core :as hc]
   [hickory.select :as s]
   [cljs.core.match :refer-macros [match]]
   [taoensso.timbre :refer-macros [debug spy]]))

(defn tds->route-description [td-cells]
  (match (vec td-cells)
         [{:tag :a :attrs {:href link} :content [date]}
          {:tag :a :content [location]}
          {:tag :a :content [municipality]}
          {:tag :a :content [number-of-routes]}
          _ ;; skip, don't know what this means
          {:tag :a :content [is-reitit]}]
         {:href (apply str (butlast (drop 1 link)))
          :location location
          :municipality municipality
          :routes number-of-routes ;; reitit.fi supports multiple routes
          :reitit? (= "Kyllä" is-reitit)}
         :else nil))

(defn- load-routes []
  (->> @(r/subscribe [:http-rastit {:url "/"}])
       :body
       hc/parse
       hc/as-hickory
      (s/select (s/child (s/class "rowlink") (s/tag :td)))
      (mapcat :content) ;; they're all :td, go straight to :content
      (partition 6) ;; six td cells in tr
      (map tds->route-description)
      (filterv some?)))

(def r (vec (load-routes)))

(r/reg-sub
 :rastit/routes
 (fn [db _]
   (when-let [rastit (load-routes)]
     (spy (:body rastit))
     (:body rastit))))

(defn available-rastit-routes []
  (let [routes @(r/subscribe [:rastit/routes])]
    [:div routes]
    ))
