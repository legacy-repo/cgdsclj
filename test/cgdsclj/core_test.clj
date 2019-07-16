(ns cgdsclj.core-test
  (:require [clojure.test :refer :all]
            [cgdsclj.core :refer :all]))

(deftest test-headers
  (is (= {} (headers))))

(deftest test-body
  (is (= {} (body))))

(deftest test-json-req-header
  (is (= {:content-type :json
          :connection-timeout 1000
          :accept :json}
         (json-req-header)))
  (is (= {:content-type :json
          :connection-timeout 100
          :accept :json}
         (json-req-header 100))))
