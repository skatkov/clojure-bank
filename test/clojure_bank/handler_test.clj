(ns clojure-bank.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.pprint :as pp]
            [clojure-bank.handler :refer :all]))

(deftest main-routes
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
(deftest app-accounts
  (testing "no accounts"
    (let [response (app (mock/request :get "/account/1"))]
      (is (= (:status response) 200))
      (is (= (:body response) "[]"))))

  (testing "adding an account"
    (app (mock/request :get "/account?name=test1"))

    (let [response (app (mock/request :get "/account/1"))]
      (is (= (:status response) 200))
      (is (= (:body response) "[{\"name\":\"test1\"}]")))))