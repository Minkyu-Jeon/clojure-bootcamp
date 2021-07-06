(ns aoc2018-7
  (:require [clojure.string]
            [clojure.core]))

(defn build-tree [input]
  (->> input
       (reduce (fn [acc [k v]] (update acc k (fnil conj (sorted-set)) v))
               {})))

(defn parse-line [line]
  (let [split-line (-> line
                       (clojure.string/split #" "))]
    [(nth split-line 1) (nth split-line 7)]))

(def input (->>
            (slurp "input/day7_input.txt")
            (clojure.string/split-lines)
            (map parse-line)
            build-tree))


(defn asdf [ss xs])