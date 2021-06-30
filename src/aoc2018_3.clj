(ns aoc2018-3
  (:require [clojure.string])
  (:require [clojure.core]))

(defn parse-line
  "Example:
   (parse-line \"#1 @ 704,926: 5x4\")
   => {:id 1 :x 704 :y 926 :w 5 :h 4}"
  [line]
  (->> (clojure.string/split line #"[^0-9]")
       (filter (complement clojure.string/blank?))
       (map #(Integer/parseInt %))
       (zipmap [:id :x :y :w :h])))

(def claims (->> (slurp "input/day3_input.txt")
                 (clojure.string/split-lines)
                 (map parse-line)))

(defn make-coords [line]
  (let [{:keys [x y w h]} line
        x (inc x)
        y (inc y)]
    (->>
     (range 0 h)
     (mapcat (fn [i] (->> (range 0 w)
                          (map (fn [j] [(+ x j) (+ y i)]))))))))

(defn update-fabric [fabric claim]
  (reduce (fn [acc coord] (if (get acc coord)
                            (update acc coord inc)
                            (assoc acc coord 1)))
          fabric
          (make-coords claim)))

(defn not-overlaped?
  "fabric(전체 맵), claim정보를 받아 해당 영역에 겹치는 부분이 없는지 판단하여
   없으면 line, 있으면 false를 리턴"
  [fabric claim]
  (let [area (->> (make-coords claim)
                  (map #(= (fabric %) 1)))]
    (every? true? area)))


(def updated-fabric (->> claims
                         (reduce (fn [acc claim] (update-fabric acc claim))
                                 {})))

(defn find-not-overlapped-area [fabric claims]
  (loop [[claim & rest] claims]
    (if (not-overlaped? fabric claim)
      claim
      (recur rest))))

;; Part 1
(comment (->>
          updated-fabric
          (filter (fn [[_ v]] (> v 1)))
          count))
;; Part 2
(comment (:id (find-not-overlapped-area updated-fabric claims)))