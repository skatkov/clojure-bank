(ns clojure-bank.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [compojure.handler :as handler]
            [clojure.data.json :as json]
            [ring.middleware.json :as middleware]
            [clojure.pprint :as pp]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(def account-collection (atom []))

(defn next-account-number []
  (+ 1 (or (:account-number (peek @account-collection)) 0)))

(defn add-account [name]
  (swap! account-collection conj {:account-number (next-account-number) :name name :balance 0}))

(defn get-account [id]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body  (str (json/write-str @account-collection))})

(defn getparameter [req pname] (get (:params req) pname))

(defn add-account-handler [body]
  {:status 200
   :headers {"Content-Type" "text/json"}
   :body (str (json/write-str (peek (add-account (body "name")))))})

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context "/account" [] (defroutes account-routes
  		(POST "/" {body :body} (add-account-handler body))
  		(GET "/:id" [id] (get-account id))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))
