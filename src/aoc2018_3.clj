(ns aoc2018-3
  (:require [clojure.string :as str])
  (:require [clojure.core]))

(defn parse-line
  "Example:
   (parse-line \"#1 @ 704,926: 5x4\")
   => {:id 1 :x 704 :y 926 :w 5 :h 4}"
  [line]
  (->> (str/split line #"[^0-9]")
       (filter (complement str/blank?))
       (map #(. Integer parseInt %))
       (zipmap [:id :x :y :w :h])))

(def claims (->> "input/day3_input.txt"
                 (slurp)
                 (str/split-lines)
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
                            (update acc coord + 1)
                            (assoc acc coord 1)))
          fabric
          (make-coords claim)))

(defn validate-not-overlaped
  "fabric(전체 맵), claim정보를 받아 해당 영역에 겹치는 부분이 없는지 판단하여
   없으면 line, 있으면 false를 리턴"
  [fabric claim]
  (let [area (->> (make-coords claim)
                  (map #(= (fabric %) 1)))]
    (if (every? true? area)
      claim
      false)))


(def updated-fabric (->> claims
                         (reduce (fn [acc claim] (update-fabric acc claim))
                                 {})))

(defn find-not-overlapped-area [fabric input]
  (loop [[first & rest] input]
    (if-let [claim (validate-not-overlaped fabric first)]
      claim
      (recur rest))))

;; Part 1
(comment (->>
          updated-fabric
          (filter (fn [[_ v]] (> v 1)))
          (count)))
;; Part 2
(comment (:id (find-not-overlapped-area updated-fabric claims)))