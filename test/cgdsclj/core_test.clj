(ns cgdsclj.core-test
  (:require [clojure.test :refer :all]
            [cgdsclj.core :refer :all]))

(deftest test-headers
  (is (= {:content-type :json
          :connection-timeout 1000
          :accept :json}
         headers)))

(deftest test-strip-slash
  (is (= (strip-slash "https://www.baidu.com") "https://www.baidu.com"))
  (is (= (strip-slash "https://www.baidu.com/") "https://www.baidu.com"))
  (is (not= (strip-slash "https://www.baidu.com/") "https://www.baidu.com/")))

(deftest test-new-url
  (is (= (new-url "https://www.baidu.com/" "/path1") "https://www.baidu.com/path1"))
  (is (not= (new-url "https://www.baidu.com/" "//path1") "https://www.baidu.com/path1")))

(deftest test-string->list
  (is (= (string->list "www.baidu.com" #"\.") ["www" "baidu" "com"]))
  (is (= (string->list "www.baidu.com" #",") ["www.baidu.com"])))

(deftest test-list->map
  (is (= (list->map ["gene_id" "value"]) [{:gene_id nil :value nil}]))
  (is (= (list->map [["gene_id" "value"]]) [{:gene_id nil :value nil}])))

(deftest test-join-with-sep
  (is (= (join-with-sep ",") ""))
  (is (= (join-with-sep "," "test") "test"))
  (is (= (join-with-sep "," "test" "join")) "test,join"))

(deftest test-query-data
  (is (map? (query-data "https://www.baidu.com" headers))))