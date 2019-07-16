(ns cgdsclj.core
  "Clojure based API for accessing the Cancer Genomics Data Server (CGDS)."
  (:require [clj-http.client :as client]))

(defn headers []
  {})

(defn body []
  {})

(defn json-req-header
  "Generate a header for json request."
  ([] (json-req-header 1000))
  ([conn-timeout] {:content-type :json
                   :connection-timeout conn-timeout
                   :accept :json}))

(defn process-url [url]
  "Get data from a specified url."
  (client/get url (into {} [(json-req-header), (headers), (body)])))

(defn strip-slash [url]
  "Strip slash from a string"
  (clojure.string/replace url #"/$" ""))

(defn new-url [url path & more-path]
  "Join url with path."
  (str (strip-slash url) path (clojure.string/join more-path)))

(defn string->list [string sep]
  "Convert string to a list."
  (clojure.string/split string sep))

(defn list->map [list]
  "Convert list to map"
  (let [header (first list)
        body (rest list)]
    (map #(zipmap (map keyword header) %) body)))

(defn join-str-or-list
  ([str-or-list] (join-str-or-list "," str-or-list))
  ([sep str-or-list] (clojure.string/join sep (concat str-or-list))))

(defn body-data [url]
  (:body (process-url url)))

(def not-nil? (complement nil?))

(defn cancer-studies [url]
  "Get cancer studies from data portal."
  (let [body (body-data (new-url url "/webservice.do?cmd=getCancerStudies&"))]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn case-lists [url cancer-study-id]
  "Get case list from data portal"
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getCaseLists"
                       "&cancer_study_id=" cancer-study-id))]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn genetic-profiles [url cancer-study-id]
  "Get genetic profiles from data portal"
  (let [body (body-data (new-url url "/webservice.do?cmd=getGeneticProfiles"
                                 "&cancer_study_id=" cancer-study-id))]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn mutation-data [url cancer-study-id case-list-id genetic-profile-id gene-list]
  "Get mutation data from data portal"
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getMutationData"
                       "&cancer_study_id=" cancer-study-id
                       "&case_set_id=" case-list-id
                       "&genetic_profile_id=" genetic-profile-id
                       "&gene_list=" (join-str-or-list "," gene-list)))]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn profile-data [url gene-list genetic-profiles case-list cases case-ids-key]
  "Get profile data from data portal"
  (let [url (new-url url "webservice.do?cmd=getProfileData"
                     "&gene_list" (join-str-or-list "," gene-list)
                     "&genetic_profile_id=" (join-str-or-list "," genetic-profiles)
                     "&id_type" "gene_symbol")]
    (cond (> (count cases) 0)
          (let [body (body-data
                      (new-url url "&case_list=" (join-str-or-list "," cases)))]
            (map #(string->list % #"\t") (string->list body #"\n")))
          (> (count case-ids-key) 0)
          (let [body (body-data
                      (new-url url "&case_ids_key=" case-ids-key))]
            (map #(string->list % #"\t") (string->list body #"\n")))
          :else
          (let [body (body-data
                      (new-url url "&case_set_id" case-list))]
            (map #(string->list % #"\t") (string->list body #"\n"))))))

(defn clinical-data [url case-list cases case-ids-key]
  "Get clinical data from data portal"
  (let [url (new-url url "webservice.do?cmd=getProfileData")]
    (cond (> (count cases) 0)
          (let [body (body-data
                      (new-url url "&case_list=" (join-str-or-list "," cases)))]
            (map #(string->list % #"\t") (string->list body #"\n")))
          (> (count case-ids-key) 0)
          (let [body (body-data
                      (new-url url "&case_ids_key=" case-ids-key))]
            (map #(string->list % #"\t") (string->list body #"\n")))
          :else
          (let [body (body-data
                      (new-url url "&case_set_id" case-list))]
            (map #(string->list % #"\t") (string->list body #"\n"))))))