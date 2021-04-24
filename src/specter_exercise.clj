(ns specter-exercise
  (:use [com.rpl.specter]))

(def data {:a [{:aa 1 :bb 2}
               {:cc 3}]
           :b [{:dd 4}]})

;; Manual Clojure
(defn map-vals [m afn]
  (->> m (map (fn [[k v]] [k (afn v)])) (into (empty m))))

(map-vals data
          (fn [v]
            (mapv
              (fn [m]
                (map-vals
                   m
                   (fn [v] (if (even? v) (inc v) v))))
              v)))

;; Specter
(transform [MAP-VALS ALL MAP-VALS even?] inc data)
