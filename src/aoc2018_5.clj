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

(def char-vec (->> (zipmap
                    (range
                     (int \A)
                     (int \Z))
                    (range
                     (int \a)
                     (int \z)))
                   (map (fn [item] (into #{} item)))))

(defn remove-chars [polymer char-set]
  (->> polymer
       (filter (fn [item] ((complement contains?) char-set item)))))

;; part1
(comment (->> input
              (react-polymer)
              (count)))

;; part2
(comment (->> char-vec
              (map (fn [char-set]
                     (let [removed-chars (remove-chars input char-set)]
                       {char-set (->> removed-chars
                                      (react-polymer)
                                      (count))})))
              (apply conj)
              (apply min-key val)))