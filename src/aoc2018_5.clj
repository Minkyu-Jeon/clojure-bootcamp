(ns aoc2018-5
  (:require [clojure.string]
            [clojure.core]))


(def input (->> "input/day5_input.txt"
                (slurp)
                (map int)))

(defn abs [n] (if (< n 0)
                (* -1 n)
                n))

(defn reacted? [x y] (= (abs (- x y)) 32))

(defn react-polymer [state]
  (loop [result []
         [first-item & rest] state]
    (if rest
      (if (not-empty result)
        (let [last-item (peek result)]
          (if (reacted? last-item first-item)
            (recur (pop result) rest)
            (recur (conj result first-item) rest)))
        (recur (conj result first-item) rest))
      (conj result first-item))))


(comment (->> input
              (react-polymer)
              (count)))

