(defproject snakesandladders "0.1.0-SNAPSHOT"
  :description "Functional Snakes and ladders text game written in Clojure"
  :url "http://github.com/danurai"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main ^:skip-aot snakesandladders.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
