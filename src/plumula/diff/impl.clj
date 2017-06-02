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
  (:import (com.sksamuel.diffpatch DiffMatchPatch DiffMatchPatch$Diff)))

(set! *warn-on-reflection* true)

(defn decode-operation
  "Translates an operation type from Java enum to namespaced Clojure keyword."
  [^DiffMatchPatch$Diff d]
  (-> d
      .operation
      .name
      clojure.string/lower-case
      (->> (keyword "plumula.diff"))))

(defn decode-text
  "Extracts the text form a Java diff"
  [^DiffMatchPatch$Diff d]
  (.text d))

(defn ^DiffMatchPatch diff-match-patch
  "Constructs a new diffing engine."
  []
  (DiffMatchPatch.))
