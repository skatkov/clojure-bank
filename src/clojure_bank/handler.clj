(ns clojure-bank.handler
  (:use ring.util.response)
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
  (let [new-id (next-account-number)]
    (swap! account-collection assoc new-id {:account-number new-id :name name :balance 0})))

(defn add-deposit [id amount]
  (let [new-balance (+ amount (:balance (@account-collection id)))]
    (swap! account-collection assoc-in [id :balance] new-balance)))

(defn missing-account-error []
  {:status 400
   :headers {"Content-Type" "text/json"}
   :body  (str (json/write-str {:error "Account is missing"}))})

(defn negative-amount-error []
  {:status 400
   :headers {"Content-Type" "text/json"}
   :body  (str (json/write-str {:error "You can only deposit a positive amount of money."}))})

(defn get-account-handler [id]
  (let [account (@account-collection (Integer/parseInt id))]
    (if (nil? account) (missing-account-error) (response account))))

(defn add-deposit-resp [id body]
  (add-deposit (Integer/parseInt id) (body "amount"))
  (get-account-handler id))

(defn add-account-handler [body]
  (response (last (vals (add-account (body "name"))))))

(defn add-deposit-handler [id body]
		(cond
				(> 0 (body "amount")) (negative-amount-error)
				(nil? (@account-collection (Integer/parseInt id))) (missing-account-error)
				:else (add-deposit-resp id body)
		)
)

(defroutes app-routes
  (GET "/" [] "Hello World")
  (context "/account" [] (defroutes account-routes
                           (POST "/" {body :body} (add-account-handler body))
                           (context "/:id" [id] (defroutes account-routes
                                                  (GET "/" [] (get-account-handler id))
                                                  (POST "/deposit" {body :body} (add-deposit-handler id body))))))
  (route/not-found "Not Found"))

(def app
  (-> (handler/api app-routes)
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)))