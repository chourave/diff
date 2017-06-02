(set-env! :dependencies '[[plumula/soles "0.4.1" :scope "test"]])
(require '[plumula.soles :refer :all])

(soles! 'plumula/diff "0.1.0"
        :dependencies '((:provided
                          [org.clojure/clojure "1.9.0-alpha17"]
                          [org.clojure/clojurescript "1.9.562"])
                         (:compile
                           [com.sksamuel.diff/diff "1.1.11"]
                           [cljsjs/google-diff-match-patch "20121119-2"])
                         (:test
                           [plumula/mimolette "0.2.0"])))
