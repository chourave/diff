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

(ns plumula.diff.spec
  (:require [clojure.spec.alpha :as s]
            [plumula.diff :as diff]))

(s/def ::diff/text string?)
(s/def ::diff/operation #{::diff/delete ::diff/equal ::diff/insert})
(s/def ::diff/cleanup #{::diff/cleanup-none
                        ::diff/cleanup-semantic
                        ::diff/cleanup-efficiency
                        ::diff/cleanup-semantic-lossless
                        ::diff/cleanup-merge})
(s/def ::diff/check-lines boolean?)

(s/def ::diff/timeout (s/double-in
                        :min 0
                        #?@(:clj [:max (Float/MAX_VALUE)])
                        :infinite? false
                        :NaN? false))
(s/def ::diff/edit-cost (s/int-in 0 32768))

(s/fdef diff/diff
        :args (s/cat :text1 string?
                     :text2 string?
                     :opts (s/keys* :opt [::diff/cleanup
                                          ::diff/check-lines
                                          ::diff/timeout
                                          ::diff/edit-cost]))
        :ret (s/coll-of (s/keys :req [::diff/text ::diff/operation]))
        :fn (s/and #(= (->> %
                            :ret
                            (filter (comp #{::diff/delete ::diff/equal}
                                          ::diff/operation))
                            (map ::diff/text)
                            (apply str))
                       (-> % :args :text1))
                   #(= (->> %
                            :ret
                            (filter (comp #{::diff/insert ::diff/equal}
                                          ::diff/operation))
                            (map ::diff/text)
                            (apply str))
                       (-> % :args :text2))))
