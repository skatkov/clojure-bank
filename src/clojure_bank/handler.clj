(ns clojure-bank.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]
            [ring.middleware.json :as middleware]
            [clojure.pprint :as pp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def account-collection (atom {}))

(defn next-account-number []
  (+ 1 (if (empty? @account-collection) 0 (key (last @account-collection)))))

(defn add-account [name]
  (swap! account-collection assoc (next-account-number) {:account-number (next-account-number) :name name :balance 0}))

(defn get-account [id]
		(def account (get @account-collection (Integer/parseInt id)))
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body  (str (json/write-str (if (nil? account) {:error "Account is missing"} account)))})

(defn getparameter [req pname] (get (:params req) pname))

(defn add-account-handler [body]
		{:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str (last (vals (add-account (body "name"))))))})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context "/account" [] (defroutes account-routes
  		(POST "/" {body :body} (add-account-handler body))
  		(GET "/:id" [id] (get-account id))
  		))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))