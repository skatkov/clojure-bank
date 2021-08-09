(ns clojure-bank.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure.pprint :as pp]
            [clojure.data.json :as json]
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
    (let [response (app (-> (mock/request :post "/account")
                            (mock/json-body {:name "Mr. Black"})))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/write-str {:account-number 1 :name "Mr. Black" :balance 0})))))

  (testing "adding second account"
    (let [response (app (-> (mock/request :post "/account")
                            (mock/json-body {:name "Mr. Brown"})))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/write-str {:account-number 2 :name "Mr. Brown" :balance 0})))))

  (testing "review account"
    (let [response (app (mock/request :get "/account/1"))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/write-str [{:account-number 1 :name "Mr. Black" :balance 0}
                              {:account-number 2 :name "Mr. Brown" :balance 0}]))))))