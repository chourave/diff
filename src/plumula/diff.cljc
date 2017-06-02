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

(ns plumula.diff
  (:require [plumula.diff.impl :as impl]
    #?(:clj [plumula.diff.util :refer [set-field!]]
       :cljs [cljsjs.google-diff-match-patch]))
  #?(:cljs (:require-macros [plumula.diff.util :refer [set-field!]])
     :clj (:import (com.sksamuel.diffpatch DiffMatchPatch))))

#?(:clj  (set! *warn-on-reflection* true)
   :cljs (set! *warn-on-infer* true))

(defn- decode-diff
  "Returns a map representation of the native `diff` object."
  [diff]
  {::operation (impl/decode-operation diff)
   ::text      (impl/decode-text diff)})

(defn- ^#?(:clj DiffMatchPatch :cljs js/diff_match_patch) diff-match-patch
  "Returns a diff-match-patch engine with the requested diffing `timeout`
  (in seconds) and `edit-cost` (in characters)."
  [{:keys [::timeout ::edit-cost]}]
  (cond-> (impl/diff-match-patch)
          timeout (set-field! -Diff_Timeout timeout)
          edit-cost (set-field! -Diff_EditCost edit-cost)))

(def ^:private cleanup-functions
  "A map of keywords to cleanup functions.
  Cleanup functions take a native diff object and optimize it in place for some
  specific usage.
  "
  {::cleanup-none              (fn [_ _])
   ::cleanup-semantic          #(.diff_cleanupSemantic ^#?(:clj DiffMatchPatch :cljs js/diff_match_patch) %2 %1)
   ::cleanup-efficiency        #(.diff_cleanupEfficiency ^#?(:clj DiffMatchPatch :cljs js/diff_match_patch) %2 %1)
   ::cleanup-semantic-lossless #(.diff_cleanupSemanticLossless ^#?(:clj DiffMatchPatch :cljs js/diff_match_patch) %2 %1)
   ::cleanup-merge             #(.diff_cleanupMerge ^#?(:clj DiffMatchPatch :cljs js/diff_match_patch) %2 %1)})

(defn diff
  "Returns a lazy sequence representing the operations to go from `text1` to
  `text2`. (The underlying diff is calculated eagerly, only the transation to
  Clojure data structures is lazy).

  Each operation in the resulting sequence is represented as a map with two
  entries
  - ::text a string representing the text operated on by the operation
  - ::operation one of three keywords, representing the operation that must be
    applied to the text to go from `text1` to `text2`
    - ::delete if the text must be deleted from `text1`
    - ::equal if the text must be carried over from `text1`
    - ::insert if the text must be inserted into `text1`

  Optional keyword arguments:
  - ::check-lines (default true) If true, enable an optimization that
    preprocesses the texts with a line-based diff before applying a
    character-based diff on the lines. This can dramatically speed up the
    computation on long texts, but can produce results with more diff operations
    than necessary.
  - ::timeout (default 1) If the mapping phase of the diff computation takes
    longer than this (in seconds), then the computation is truncated and the
    best solution to date is returned. While guaranteed to be correct, it may
    not be optimal. A timeout of '0' allows for unlimited computation.
  - ::cleanup (default ::cleanup-none) Specifies how the output of diff should
    be optimized after the main algorithm is done. See below for a list of valid
    options
  - ::edit-cost (default: 4) only used for ::cleanup-efficiency - the fixed
    cost overhead of having an additional edit operation. The unit is an
    equivalent number of characters. For instance, with the default value of 4,
    two one-character edits are estimated to have the same cost as one
    six-character edits (the cost is ten).

  The recommended values for ::cleanup are:
  - ::cleanup-none (default) returns the raw, unoptimized diff.
  - ::cleanup-semantic Increase human readability by factoring out commonalities
    which are likely to be coincidental.
  - ::cleanup-efficiency Increase computational efficiency by factoring out
    short commonalities which are not worth the overhead. The larger the
    ::edit-cost, the more agressive the cleanup.

  Furthermore, there are two pared-down versions of the optimizations, that will
  shift operations around to reach a better result, but will not factor out
  commonalities:
  - ::cleanup-semantic-lossless shifts operations to align them to word
    boundaries
  - ::cleanup-merge shifts operations to minimise to merge them
  "
  [text1 text2 & {:keys [::check-lines ::cleanup]
                  :or   {check-lines true, cleanup ::cleanup-none}
                  :as   options}]
  (let [engine (diff-match-patch options)]
    (-> engine
        (.diff_main text1 text2 check-lines)
        (doto ((cleanup-functions cleanup) engine))
        (->> (map decode-diff)))))
