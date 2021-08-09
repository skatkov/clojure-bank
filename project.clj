(defproject clojure-bank "0.1.0-SNAPSHOT"
  :description "Bank API"
  :url "https://skatkov.com"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.1.2"]
                 [org.clojure/data.json "0.2.6"]
                 ]
  :plugins [[lein-ring "0.12.5"][lein-cljfmt "0.8.0"]]
  :ring {:handler clojure-bank.handler/app}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]]}})
