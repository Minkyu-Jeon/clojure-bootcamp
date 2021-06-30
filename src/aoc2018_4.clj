(ns aoc2018-4
  (:require [clojure.string]
            [clojure.core]))


(defn parse-line [line]
  (let [[time & action] (-> line
                            (clojure.string/replace #"[\[\]#]" "")
                            (clojure.string/split #" ")
                            rest)]
    {:time (->> (clojure.string/split time #":")
                fnext
                Integer/parseInt)
     :guard-id (when-let [guard-id (re-matches #"[0-9]+" (nth action 1))]
                 (Integer/parseInt guard-id))}))

(defn update-guard-ids [parse-fn lines]
  (let [result (->> lines
                    (map parse-fn)
                    (reduce (fn [acc line]
                              (if-let [new-guard-id (:guard-id line)]
                                (assoc acc :guard-id new-guard-id)
                                (update acc :new-lines conj (assoc line :guard-id (:guard-id acc)))))
                            {:new-lines [] :guard-id nil}))]
    (:new-lines result)))


(def input (->> (slurp "input/day4_input.txt")
                clojure.string/split-lines
                sort
                (update-guard-ids parse-line)))

(defn calc-sleeping-time [lines]
  (->> lines
       (partition 2)
       (map (fn [[sleep & [awake]]]
              [(range (:time sleep) (:time awake))
               (:guard-id sleep)]))
       (reduce (fn [minutes [sleep-range & [guard-id]]]
                 (->> sleep-range
                      (reduce (fn [acc min]
                                (update acc min (fnil conj []) guard-id))
                              minutes)))
               {})))

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
                         (update acc2 guard-id (fnil + 0) times))
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
       key))

;; part 1: 가장 오랜 시간 잠들어있었던 가드의 ID * 그 가드가 가장 빈번하게 잠들어있던 분
(comment (let [laziest-guard-id (->> frequency-map
                                     get-total-sleeping-time-by-guard
                                     get-max
                                     key)
               most-frequent-minute (->> laziest-guard-id
                                         (get-most-frequent-minute frequency-map))]
           (* most-frequent-minute laziest-guard-id)))

;; part 2: 한 가드가 가장 오래 잠들어있던 분 * 그 가드의 ID
(comment (let [[min [guard-id _]] (->> frequency-map
                                       (mapv (fn [[k v]] [k (get-max v)]))
                                       (apply max-key #(second (second %1))))]
           (* min guard-id)))