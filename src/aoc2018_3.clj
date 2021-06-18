(ns aoc2018-3
  (:require [clojure.string :as str])
  (:require [clojure.core]))


(defn build-fabric [size]
  (->> (range 0 size)
       (mapcat (fn [n] (->> (range 0 size)
                            (map (fn [m] [[m n] 0]))
                            vec)))
       vec
       (into (sorted-map))))

(defn parse-line [line]
  (into {} (->>
            (-> line
                (str/split #"[^0-9]"))
            (filter (complement str/blank?))
            (map #(. Integer parseInt %))
            (map vector [:id :x :y :w :h]))))

(defn make-coords [line]
  (let [id (:id line)
        x (+ (:x line) 1)
        y (+ (:y line) 1)
        width (:w line)
        height (:h line)]
    (->>
     (range 0 height)
     (mapcat (fn [i] (->> (range 0 width)
                          (map (fn [j] [(+ x j) (+ y i)]))))))))

(defn update-fabric [fabric claim]
  (reduce (fn [acc coord] (update acc coord inc))
          fabric
          (make-coords claim)))

(def claims (->> "input/day3_input.txt"
               (slurp)
               (str/split-lines)
               (map parse-line)))

(def fabric (build-fabric 1001))

(defn solve1 [fabric claims]
  (reduce (fn [acc claim] (update-fabric acc claim))
          fabric
          claims))

(def updated-fabric (solve1 fabric claims))

(defn solve2 [fabric input]
  (loop [[first & rest] input]
    (let [claim (->> (make-coords first)
                     (map (fn [coord] (= (get fabric coord) 1))))]
      (if (every? true? claim)
        first
        (recur rest)))))

;; Part 1
(comment (->>
          updated-fabric
          (filter (fn [[k v]] (> v 1)))
          (count)))
;; Part 2
(comment (:id (solve2 updated-fabric claims)))