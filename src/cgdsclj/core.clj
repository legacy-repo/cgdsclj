(ns cgdsclj.core
  "Clojure based API for accessing the Cancer Genomics Data Server (CGDS)."
  (:require [clj-http.client :as client])
  (:use [slingshot.slingshot :only [throw+ try+]])
  (:use [clojure.tools.logging :as log]))

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
  (try+
   (client/get url headers)
   (catch [:status 403] {:keys [request-time headers body]}
     (log/warn "403" request-time headers))
   (catch [:status 404] {:keys [request-time headers body]}
     (log/warn "NOT Found 404" request-time headers body))
   (catch Object _
     (log/error (:throwable &throw-context) "unexpected error")
     (throw+))))

(defn body-data
  [url headers] (:body (query-data url headers)))

(def not-nil? (complement nil?))

(defn cancer-studies [url]
  "Get cancer studies from data portal."
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getCancerStudies")
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn case-lists [url cancer-study-id]
  "Get case list from data portal"
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getCaseLists"
                       "&cancer_study_id=" cancer-study-id)
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn genetic-profiles [url cancer-study-id]
  "Get genetic profiles from data portal"
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getGeneticProfiles"
                       "&cancer_study_id=" cancer-study-id)
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn mutation-data [url cancer-study-id case-list-id genetic-profile-id genes]
  "Get mutation data from data portal"
  (let [body (body-data
              (new-url url "/webservice.do?cmd=getMutationData"
                       "&cancer_study_id=" cancer-study-id
                       "&case_set_id=" case-list-id
                       "&genetic_profile_id=" genetic-profile-id
                       "&gene_list=" (clojure.string/join genes))
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(def case-type-map {:case-list "&case_list="
                    :case-ids-key "&case_ids_key="
                    :case-set-id "&case_set_id="})

(defn profile-data [url genes genetic-profiles cases case-type]
  "Get profile data from data portal"
  (let [url (new-url url "/webservice.do?cmd=getProfileData"
                     "&gene_list=" (clojure.string/join genes)
                     "&genetic_profile_id=" (clojure.string/join genetic-profiles)
                     "&id_type=" "gene_symbol")
        body (body-data
              (new-url url
                       (case-type case-type-map)
                       (clojure.string/join cases))
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))

(defn clinical-data [url cases case-type]
  "Get clinical data from data portal"
  (let [url (new-url url "/webservice.do?cmd=getClinicalData")
        body (body-data
              (new-url url
                       (case-type case-type-map)
                       (clojure.string/join cases))
              headers)]
    (map #(string->list % #"\t") (string->list body #"\n"))))
