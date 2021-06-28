(ns aoc2018-5
  (:require [clojure.string]
            [clojure.core]))


(def input (->> "input/day5_input.txt"
                (slurp)
                (map int)))

(defn abs [n] (max n (- n)))

(defn reacted? [x y] (= (abs (- x y)) 32))

(defn react-polymer [polymer]
  (->> polymer
       (reduce (fn [reduced-polymer first-item]
                 (if (empty? reduced-polymer)
                   (conj reduced-polymer first-item)
                   (if-let [_ (reacted? (peek reduced-polymer) first-item)]
                     (pop reduced-polymer)
                     (conj reduced-polymer first-item))))
               [])))

(defn remove-chars [polymer char-set]
  (filter #((complement contains?) char-set %) polymer))

(defn generate-polymer-seq-with-removed-chars [polymer]
  (for [i (range (int \a) (int \z))]
    (remove-chars polymer #{i (- i 32)})))

;; part1
(comment (->> input
              react-polymer
              count))

;; part2
(comment (->> input
              generate-polymer-seq-with-removed-chars
              (map #(react-polymer %))
              (map count)
              (apply min)))