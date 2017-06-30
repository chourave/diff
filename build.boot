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

(def +project+ 'plumula/diff)
(def +version+ "0.1.2-SNAPSHOT")

(set-env! :dependencies '[[plumula/soles "0.5.0" :scope "test"]])
(require '[plumula.soles :refer [add-dependencies! add-dir! deploy-local deploy-snapshot deploy-release old testing]])

(add-dependencies! '(:provided
                      [org.clojure/clojure "1.9.0-alpha17"]
                      [org.clojure/clojurescript "1.9.671"])
                   '(:compile
                      [com.sksamuel.diff/diff "1.1.11"]
                      [cljsjs/google-diff-match-patch "20121119-2"])
                   '(:test
                      [adzerk/boot-cljs "2.0.0"]
                      [adzerk/boot-cljs-repl "0.3.3"]
                      [adzerk/boot-reload "0.5.1"]
                      [adzerk/boot-test "1.2.0"]
                      [com.cemerick/piggieback "0.2.2"]
                      [crisptrutski/boot-cljs-test "0.3.0"]
                      [doo "0.1.7"]
                      [org.clojure/tools.nrepl "0.2.13"]
                      [pandeiro/boot-http "0.8.3"]
                      [plumula/mimolette "0.2.1"]
                      [weasel "0.7.0"]))

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
         '[adzerk.boot-reload :refer [reload]]
         '[adzerk.boot-test :as test]
         '[adzerk.bootlaces :refer [bootlaces!]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs report-errors!]]
         '[boot.lein :as lein]
         '[pandeiro.boot-http :refer [serve]])

(add-dir! :source-paths "src")

(bootlaces! +version+)
(lein/generate)

(task-options!
  cljs { :compiler-options {:infer-externs true}}
  pom {:project     +project+
       :version     +version+
       :description "A fast text diff library for Clojure and ClojureScript."
                :url "https://github.com/plumula/diff"
                :scm {:url "https://github.com/plumula/diff"}
                :license {"Apache-2.0" "http://www.apache.org/licenses/LICENSE-2.0"}}
  serve {:dir "target"}
  test-cljs {:js-env        :node
             :update-fs?    true
             :keep-errors?  true
             :optimizations :simple})

(deftask dev
         "Launch Immediate Feedback Development Environment."
         []
         (comp
           (testing)
           (serve)
           (watch)
           (test-cljs :exclusions #{#"^plumula\.diff\.slow\."})
           (test/test :exclude #"^plumula\.diff\.slow\.")
           (report-errors!)
           (reload)
           (cljs-repl)
           (cljs)
           (target)))

(deftask slow-tests
         "Continuously run slow tests"
         []
         (comp
           (testing)
           (watch)
           (test-cljs :namespaces #{#"^plumula\.diff\.slow\."})
           (test/test :include #"^plumula\.diff\.slow\.")
           (report-errors!)))
