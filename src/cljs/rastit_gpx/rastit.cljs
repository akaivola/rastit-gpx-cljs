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
         {:route-number (apply str (butlast (drop 1 link)))
          :date date
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

(r/reg-sub
 :rastit/routes
 (fn [db _]
   (load-routes)))

(defn direct-route-download []
  (let [dl (reagent/atom nil)]
    [:div
     [:label {:for "dl"} "Reitin numero:"]
     [:input#dl {:type      "number"
                 :on-change #(reset! dl (-> % .-target .-value))}]
     [:button {:on-click #(when (pos? @dl) (js/fetchGpx @dl))} "Lataa"]]))

(defn available-rastit-routes []
  [:div.available-rastit-routes
   [:h1 "Viimeisimm&auml;t reitit"]
   (into
    [:div.routes]
    (for [{:keys [route-number date location municipality]}
          @(r/subscribe [:rastit/routes])]
      [:div.route
       {:on-click
        #(js/fetchGpx
          route-number
          (str date "_" location "_" route-number))}
       [:h1 date]
       [:h2 municipality]
       [:h3 location]]))])

(defn route-displays []
  [:div.route-displays
   [direct-route-download]
   [available-rastit-routes]])