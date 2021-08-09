(ns clojure-bank.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [clojure-bank.handler :refer :all]))

(deftest test-app
  (testing "main route"
    (let [response (app (mock/request :get "/"))]
      (is (= (:status response) 200))
      (is (= (:body response) "Hello World"))))

  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))))

		(testing "account route"
				(let [response (app (mock/request :get "/account/1"))]
					(is (= (:status response) 200)))
					(is (= (:body response) "Hello World")))

  )