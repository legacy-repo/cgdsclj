(ns cgdsclj.core
  "Clojure based API for accessing the Cancer Genomics Data Server (CGDS)."
  (:require [clj-http.client :as client]))

(def headers
  {:content-type :json
   :connection-timeout 1000
   :accept :json})

(defn strip-slash [url]
  "Strip slash from a string"
  (clojure.string/replace url #"/$" ""))

(defn new-url [url path & more-path]
  "Join url with path."
  (str (strip-slash url) path (clojure.string/join more-path)))

(defn string->list [string re]
  "Convert string to a list."
  (clojure.string/split string re))

(defn list->map [list]
  "Convert list to map"
  (let [header (first list)
        body (rest list)
        gen-map (fn [header body] (map #(zipmap (map keyword header) %) body))]
    (cond (string? header)
          (gen-map list [(take (count list) (repeat nil))])
          (= (count body) 0)
          (gen-map header [(take (count header) (repeat nil))])
          :else
          (gen-map header body))))

(defn join-with-sep
  "Returns a string of all elements separated by a separator."
  ([sep] (case String ""))
  ([sep x] (cast String x))
  ([sep x & more] (clojure.string/join sep (cons (join-with-sep sep x) more))))

(def join-with-comma (partial join-with-sep ","))

(defn query-data [url headers]
  "Get data from a specified url."
  (client/get url headers))

(defn body-data
  "Get body data from a http response map."
  [url headers] (:body (query-data url headers)))

(defn string->nested-list [string]
  "Convert string that seperated by tab and enter to a nested list."
  (map #(string->list % #"\t") (string->list string #"\n")))

(def not-nil? (complement nil?))

(defn cancer-studies [url]
  "Get cancer studies from data portal."
  (-> (new-url url "/webservice.do?cmd=getCancerStudies")
      (body-data headers)
      (string->nested-list)))

(defn case-lists [url cancer-study-id]
  "Get case list from data portal"
  (-> (new-url url "/webservice.do?cmd=getCaseLists"
               "&cancer_study_id=" cancer-study-id)
      (body-data headers)
      (string->nested-list)))

(defn genetic-profiles [url cancer-study-id]
  "Get genetic profiles from data portal"
  (-> (new-url url "/webservice.do?cmd=getGeneticProfiles"
               "&cancer_study_id=" cancer-study-id)
      (body-data headers)
      (string->nested-list)))

(defn mutation-data [url cancer-study-id case-list-id genetic-profile-id genes]
  "Get mutation data from data portal"
  (-> (new-url url "/webservice.do?cmd=getMutationData"
               "&cancer_study_id=" cancer-study-id
               "&case_set_id=" case-list-id
               "&genetic_profile_id=" genetic-profile-id
               "&gene_list=" (clojure.string/join genes))
      (body-data headers)
      (string->nested-list)))

(def case-type-map {:case-list "&case_list="
                    :case-ids-key "&case_ids_key="
                    :case-set-id "&case_set_id="})

(defn profile-data [url genes genetic-profiles cases case-type]
  "Get profile data from data portal"
  (-> (new-url url "/webservice.do?cmd=getProfileData"
               "&gene_list=" (clojure.string/join genes)
               "&genetic_profile_id=" (clojure.string/join genetic-profiles)
               "&id_type=" "gene_symbol"
               (case-type case-type-map)
               (clojure.string/join cases))
      (body-data headers)
      (string->nested-list)))

(defn clinical-data [url cases case-type]
  "Get clinical data from data portal"
  (-> (new-url url "/webservice.do?cmd=getClinicalData"
               (case-type case-type-map)
               (clojure.string/join cases))
      (body-data headers)
      (string->nested-list)))
