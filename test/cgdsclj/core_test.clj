(ns cgdsclj.core-test
  (:require [clojure.test :refer :all]
            [cgdsclj.core :refer :all]))

(deftest test-headers
  (is (= {:content-type :json
          :connection-timeout 1000
          :accept :json}
         default-headers)))

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
  (is (map? (query-data "https://www.baidu.com" default-headers))))

(deftest test-cancer-studies
  (is (seq? (cancer-studies)))
  (is (seq? (cancer-studies "http://www.cbioportal.org"))))

(deftest test-case-lists
  (is (seq? (case-lists "um_qimr_2016")))
  (is (seq? (case-lists "um_qimr_2016" "http://www.cbioportal.org"))))

(deftest test-genetic-profiles
  (is (seq? (genetic-profiles "um_qimr_2016")))
  (is (seq? (genetic-profiles "um_qimr_2016" "http://www.cbioportal.org"))))

(deftest test-clinical-data
  (is (seq? (clinical-data ["um_qimr_2016_all"] :case-set-id)))
  (is (seq? (clinical-data ["um_qimr_2016_all"] :case-set-id "http://www.cbioportal.org"))))

(deftest test-profile-data
  (is (seq? (profile-data ["EGFR"] ["um_qimr_2016_mutations"] ["um_qimr_2016_all"] :case-set-id)))
  (is (seq? (profile-data ["EGFR"] ["um_qimr_2016_mutations"] ["um_qimr_2016_all"] :case-set-id "http://www.cbioportal.org"))))