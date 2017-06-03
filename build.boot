(set-env! :dependencies '[[plumula/soles "0.4.1" :scope "test"]])
(require '[plumula.soles :refer :all])

(soles! 'plumula/diff "0.1.1"
        :dependencies '((:provided
                          [org.clojure/clojure "1.9.0-alpha17"]
                          [org.clojure/clojurescript "1.9.562"])
                         (:compile
                           [com.sksamuel.diff/diff "1.1.11"]
                           [cljsjs/google-diff-match-patch "20121119-2"])
                         (:test
                           [plumula/mimolette "0.2.0"])))

(task-options!
  pom #(assoc % :description "A _fast_ text diff library for Clojure and ClojureScript."
                :url "https://github.com/plumula/diff"
                :scm {:url "https://github.com/plumula/diff"}
                :license {"Apache-2.0" "http://www.apache.org/licenses/LICENSE-2.0"}))
