(ns rastit-gpx.ajax
  (:require-macros [cljs.core.async.macros :refer [go alt!]])
  (:require
   [re-frame.core :as r]
   [cljs-http.client :as http]
   [cljs.core.async :refer [<! >! timeout chan]]
   [taoensso.timbre :refer-macros [spy debug error]]))

(defn default-error-handler [response]
  (r/dispatch [:state-update
               (fn [db]
                 (assoc-in db [:flash :error]
                           (case (:status response)
                             "Tuntematon virhe palvelua kutsuttaessa.")))])
  (println "XHR Error" (str response)))

(defn- set-busy! []
  (r/dispatch [:state-update (fn [db] (assoc db :xhr-busy? true))]))

(defn- clear-busy! []
  (r/dispatch [:state-update (fn [db] (assoc db :xhr-busy? false))]))

(defn http-op [{:keys [url method json-params form-params query-params handler error-handler]
                :or   {method :get
                       error-handler default-error-handler
                       handler identity}}]
  (go
    (set-busy!)
    (let [f       (case method
                    :get   http/get
                    :post  http/post
                    :patch http/patch)
          request (f
                    url
                    (merge
                      {:with-credentials? false}
                      ;;(token/authorization)
                      (or (when json-params
                            {:json-params json-params})
                          (when form-params
                            {:form-params form-params})
                          (when query-params
                            {:query-params query-params}))))]
      (alt! (timeout 15000) (do
                              (clear-busy!)
                              (error-handler))
            request        ([response]
                            (clear-busy!)
                            (if (<= 400 (:status response))
                              (error-handler response)
                              (handler response)))))))
