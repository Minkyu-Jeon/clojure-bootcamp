(ns aoc2018-4
  (:require [clojure.string]
            [clojure.core]))


(defn parse-line [line]
  (let [[time & action] (-> line
                            (clojure.string/replace #"[\[\]#]" "")
                            (clojure.string/split #" ")
                            (rest))]
    {:time (->> (clojure.string/split time #":") (fnext) (. Integer parseInt))
     :guard-id (when-let [guard-id (re-matches #"[0-9]+" (nth action 1))]
                 (. Integer parseInt guard-id))}))

(defn update-guard-ids [parse-fn lines]
  (loop [new-lines []
         guard-id nil
         [first & rest] (map parse-fn lines)]
    (if first
      (if-let [new-guard-id (:guard-id first)]
        (recur new-lines new-guard-id rest)
        (recur (conj new-lines (assoc first :guard-id guard-id)) guard-id rest))
      new-lines)))


(def input (->> "input/day4_input.txt"
                (slurp)
                (clojure.string/split-lines)
                (sort)
                (update-guard-ids parse-line)))

(defn calc-sleeping-time [lines]
  (loop [minutes {}
         [sleep & [awake & rest]] lines]
    (if (empty? rest)
      minutes
      (let [next-minutes (->> (range (:time sleep) (:time awake))
                              (reduce (fn [acc min]
                                        (update acc min (fnil conj []) (:guard-id sleep)))
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
                         (if (get acc2 guard-id)
                           (update acc2 guard-id + times)
                           (assoc acc2 guard-id times)))
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
                                       (mapv (fn [[k v]] [k (get-max v)]))
                                       (apply max-key #(second (second %1))))]
           (* min guard-id)))