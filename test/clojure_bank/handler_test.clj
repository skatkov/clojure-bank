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
    (let [response (app (mock/request :get "/account/666"))]
      (is (= (:status response) 400))
      (is (= (:body response) (json/write-str {:error "Account is missing"})))))

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
             (json/write-str {:account-number 1 :name "Mr. Black" :balance 0}))))
    (let [response (app (mock/request :get "/account/2"))]
      (is (= (:status response) 200))
      (is (= (:body response)
             (json/write-str {:account-number 2 :name "Mr. Brown" :balance 0})))))

  (testing "adding deposit to existing account"
    (let [response (app (-> (mock/request :post "/account/1/deposit")
                            (mock/json-body {:amount 100})))]

      (is (= (:status response) 200))
      (is (= (:body response) (json/write-str {:account-number 1 :name "Mr. Black" :balance 100}))))

    (let [response (app (-> (mock/request :post "/account/1/deposit")
                            (mock/json-body {:amount 50})))]

      (is (= (:status response) 200))
      (is (= (:body response) (json/write-str {:account-number 1 :name "Mr. Black" :balance 150})))))

  (testing "adding deposit to account that is missing"
    (let [response (app (-> (mock/request :post "/account/666/deposit")
                            (mock/json-body {:amount 500})))]

      (is (= (:status response) 400))
      (is (= (:body response) (json/write-str {:error "Account is missing"})))))
  (testing "adding negative deposit"
    (let [response (app (-> (mock/request :post "/account/1/deposit")
                            (mock/json-body {:amount -100})))]

      (is (= (:status response) 400))
      (is (= (:body response) (json/write-str {:error "You can only deposit a positive amount of money."})))))

  (testing "withdraw money from account"
  		(let [response (app (-> (mock/request :post "/account/1/withdraw")
                            (mock/json-body {:amount 50})))]

  			(is (= (:status response) 200))
  			(is (= (:body response) (json/write-str {:account-number 1 :name "Mr. Black" :balance 100})))
  		)
  )
)
