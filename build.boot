; Copyright 2017 Frederic Merizen
;
; Licensed under the Apache License, Version 2.0 (the "License");
; you may not use this file except in compliance with the License.
; You may obtain a copy of the License at
;
; http://www.apache.org/licenses/LICENSE-2.0
;
; Unless required by applicable law or agreed to in writing, software
; distributed under the License is distributed on an "AS IS" BASIS,
; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
; See the License for the specific language governing permissions and
; limitations under the License.

(set-env! :dependencies '[[plumula/soles "0.4.1" :scope "test"]])
(require '[plumula.soles :refer :all])

(soles! 'plumula/diff "0.1.2-SNAPSHOT"
        :dependencies '((:provided
                          [org.clojure/clojure "1.9.0-alpha17"]
                          [org.clojure/clojurescript "1.9.562"])
                         (:compile
                           [com.sksamuel.diff/diff "1.1.11"]
                           [cljsjs/google-diff-match-patch "20121119-2"])
                         (:test
                           [plumula/mimolette "0.2.0"])))

(task-options!
  pom #(assoc % :description "A fast text diff library for Clojure and ClojureScript."
                :url "https://github.com/plumula/diff"
                :scm {:url "https://github.com/plumula/diff"}
                :license {"Apache-2.0" "http://www.apache.org/licenses/LICENSE-2.0"}))
