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

(ns plumula.diff.impl
  (:require [cljsjs.google-diff-match-patch]))

(set! *warn-on-infer* true)

(def ^:private operations
  "Maps the integer operation codes used by the JavaScript library to
  ClojureScript keywords.
  "
  {js/DIFF_DELETE :plumula.diff/delete
   js/DIFF_EQUAL  :plumula.diff/equal
   js/DIFF_INSERT :plumula.diff/insert})

(defn decode-operation
  "Translates an operation type from integer to namespaced Clojure keyword."
  [d]
  (operations (aget d 0)))

(defn decode-text
  "Extracts the text form a JavaScript diff"
  [d]
  (aget d 1))

(defn ^js/diff_match_patch diff-match-patch
  "Constructs a new diffing engine."
  [] (js/diff_match_patch.))
