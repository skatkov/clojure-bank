(ns clojure-bank.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]
            [ring.middleware.json :as middleware]
            [clojure.pprint :as pp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def account-collection (atom []))

(defn add-account [name]
  (swap! account-collection conj {:name name}))

(defn get-account [id]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body  (str (json/write-str @account-collection))})

(defn getparameter [req pname] (get (:params req) pname))

(defn add-account-body-handler [body]
		(pp/pprint (type body))
  (pp/pprint body)
  (pp/pprint (body "name"))
  (str (json/write-str body)))

(defn add-account-handler [req]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (->
          (let [p (partial getparameter req)]
            (str (json/write-str (add-account (p :name))))))})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/account" [] add-account-handler)
  (POST "/account" {body :body} (add-account-body-handler body))
  (GET "/account/:id" [id] (get-account id))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
