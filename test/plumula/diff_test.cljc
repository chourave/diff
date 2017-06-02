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

(ns plumula.diff-test
  (:require [plumula.diff :as d]
            [clojure.test :refer [deftest is testing run-tests]]
            [plumula.diff.spec]
            [plumula.mimolette.alpha :refer [defspec-test]]))

(deftest test-diff
  (testing "diff"
    (is (= [{::d/operation ::d/delete, ::d/text "a"}
            {::d/operation ::d/equal, ::d/text "bcd"}
            {::d/operation ::d/insert, ::d/text "e"}]
           (d/diff "abcd" "bcde")))))

(defspec-test test-diff-spec `d/diff)
