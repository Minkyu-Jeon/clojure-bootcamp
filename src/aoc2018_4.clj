(ns aoc2018-4
  (:require [clojure.string :as str])
  (:require [clojure.core]))


(defn parse-line [line]
  (let [[time & action] (-> line
                            (str/replace #"[\[\]#]" "")
                            (str/split #" ")
                            (rest))]
    {:time (->> (str/split time #":") (fnext) (. Integer parseInt))
     :guard-id (if-let [guard-id (re-matches #"[0-9]+" (nth action 1))]
                 (. Integer parseInt guard-id)
                 nil)}))

(defn update-guard-ids [lines]
  (loop [new-lines []
         guard-id nil
         [first & rest] lines]
    (if (nil? first)
      new-lines
      (if-let [new-guard-id (:guard-id first)]
        (recur new-lines new-guard-id rest)
        (recur (conj new-lines (assoc first :guard-id guard-id)) guard-id rest)))))


(def input (->> "input/day4_input.txt"
                (slurp)
                (str/split-lines)
                (sort)
                (map parse-line)
                (update-guard-ids)))

(defn calc-sleeping-time [lines]
  (loop [minutes {}
         [sleep & [awake & rest]] lines]
    (if (empty? rest)
      minutes
      (let [next-minutes (->> (range (:time sleep) (:time awake))
                              (reduce (fn [acc min]
                                        (if (nil? (get acc min))
                                          (assoc acc min [(:guard-id sleep)])
                                          (update acc min #(conj %1 (:guard-id sleep)))))
                                      minutes))]
        (recur next-minutes rest)))))

(defn get-frequency-map-by-minute [sleep-time-map]
  (->> sleep-time-map
       (map (fn [[k v]] {k (frequencies v)}))
       (apply conj)))

(def frequency-map
  (->> (calc-sleeping-time input)
       (get-frequency-map-by-minute)))

(defn get-total-sleeping-time-by-guard [frequency-map]
  (->> frequency-map
       (reduce
        (fn [acc [_ frequencies-by-minute]]
          (->> frequencies-by-minute
               (reduce (fn [acc2 [guard-id times]]
                         (if (nil? (get acc2 guard-id))
                           (assoc acc2 guard-id times)
                           (update acc2 guard-id + times)))
                       acc)))
        {})))

(defn get-max [map] (apply max-key val map))

(defn get-most-frequent-minute [frequency-map guard-id]
  (->> frequency-map
       (map (fn [[min freq]] (if-let [count (get freq guard-id)]
                               {min count}
                               nil)))
       (filter (complement nil?))
       (apply conj)
       (apply max-key val)
       (key)))

;; part 1: 가장 오랜 시간 잠들어있었던 가드의 ID * 그 가드가 가장 빈번하게 잠들어있던 분
(comment (let [laziest-guard-id (->> frequency-map
                                     (get-total-sleeping-time-by-guard)
                                     (get-max)
                                     (key))
               most-frequent-minute (->> laziest-guard-id
                                         (get-most-frequent-minute frequency-map))]
           (* most-frequent-minute laziest-guard-id)))

;; part 2: 한 가드가 가장 오래 잠들어있던 분 * 그 가드의 ID
(comment (let [[min [guard-id _]] (->> frequency-map
                                       (map (fn [[k v]] [k (get-max v)]))
                                       (vec)
                                       (apply max-key #(second (second %1))))]
           (* min guard-id)))