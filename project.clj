(defproject cgdsclj "0.1.0-SNAPSHOT"
  :description "Clojure based API for accessing the Cancer Genomics Data Server (CGDS)."
  :url "https://github.com/go-choppy/cgdsclj"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :exclusions [org.clojure/clojure]
  :dependencies [[clj-http "3.10.0"]]
  :global-vars {*warn-on-reflection* false}
  :min-lein-version "2.0.0"
  :resource-paths ["resources"]
  :target-path "target/%s"
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.0"]
                                  [slingshot "0.12.2"]
                                  [org.clojure/tools.logging "0.2.3"]]
                   :plugins [[lein-ancient "0.6.15"]]}
             :1.6 {:dependencies [[org.clojure/clojure "1.6.0"]]}
             :1.7 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :1.8 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :1.9 {:dependencies [[org.clojure/clojure "1.9.0"]]}}
  :aliases {"all" ["with-profile" "dev,1.6:dev,1.7:dev,1.8:dev,1.9:dev"]}
  :test-selectors {:default  #(not (:integration %))
                   :integration :integration
                   :all (constantly true)})
