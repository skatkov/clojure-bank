(ns clojure-bank.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn get-account [id] (str "The user ID is " id))

(defroutes app-routes
  (GET "/" [] "Hello World")
  (GET "/account/:id" [id] (get-account id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
