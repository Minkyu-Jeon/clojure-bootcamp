(ns ch5
  (:require [clojure.set :as set]))

;;5.1.1
(def ds (into-array [:brian :jorge :anupam]))
(seq ds)

(aset ds 1 :quentin)
(seq ds)

(def ds [:brian :jorge :anupam])
(def ds1 (replace {:brian :uqbar} ds))

;;5.1.2
; collection [1 2], {:a 1}, #{1 2}
; sequential collection [1 2 3 4], (1 2 3 4)
; sequence (map func collection)의 결과
; Seq (seq[]) => nil, (seq [1 2]) => nil

(= [1 2 3] '(1 2 3))
(= [1 2 3] #{1 2 3})

(class (hash-map :a 1))
(seq (hash-map :a 1))
(class (seq (hash-map :a 1)))

(seq (keys (hash-map :a 1)))
(class (keys (hash-map :a 1)))

;;5.2.1
(vec (range 10))
(let [my-vector [:a :b :c]]
  (into my-vector (range 10)))

(into (vector-of :int) [Math/PI 2 1.3])
(into (vector-of :char) [100 101 102])
; (into (vector-of :int) [1 2 12321421421421214124243436436])

;;5.2.2 big vectors
(def a-to-j (vec (map char (range 65 75))))
(nth a-to-j 4)
(get a-to-j 4)
(a-to-j 4)

(seq a-to-j)
(rseq a-to-j)

(assoc a-to-j 4 "no longer E")
(replace {2 :a 4 :b} [1 2 3 2 3 4])

(def matrix
  [[1 2 3]
   [4 5 6]
   [7 8 9]])

(get-in matrix [1 2])
(assoc-in matrix [1 2] 'x)

(defn neighbors
  ([size yx] (neighbors [[-1 0] [1 0] [0 -1] [0 1]]
                        size
                        yx))
  ([deltas size yx]
   (filter (fn [new-yx]
             (every? #(< -1 % size) new-yx))
           (map #(vec (map + yx %))
                deltas))))

(neighbors 3 [0 0])
(neighbors 3 [1 1])

(map #(get-in matrix %) (neighbors 3 [0 0]))

;;5.2.3
(def my-stack [1 2 3])
(peek my-stack)

(pop my-stack)
(conj my-stack 4)

(+ (peek my-stack) (peek (pop my-stack)))

;;5.2.4
(defn strict-map1 [f coll]
  (loop [coll coll
         acc nil]
    (if (empty? coll)
      (reverse acc)
      (recur (next coll)
             (cons (f (first coll)) acc)))))
(strict-map1 - (range 5))


(defn strict-map2 [f coll]
  (loop [coll coll
         acc []]
    (if (empty? coll)
      acc
      (recur (next coll)
             (conj acc (f (first coll)))))))
(strict-map2 - (range 5))


;;5.2.5 subvector
(subvec a-to-j 3 6)

;;5.2.6
(first {:width  10
        :height 20
        :depth  15})

(vector? (first {:width  10
                 :height 20
                 :depth  15}))

(doseq [[dimension amount]
        {:width  10
         :height 20
         :depth  15}]
  (println (str (name dimension) ":") amount "inches"))


;;5.3.1
(cons 1 '(2 3))
(conj '(2 3) 1)


;;5.4.1 Empty queue
(defmethod print-method clojure.lang.PersistentQueue [q w]
  (print-method '<- w)
  (print-method (seq q) w)
  (print-method '-< w))

(def schedule
  (conj clojure.lang.PersistentQueue/EMPTY
        :wake-up
        :shower
        :brush-teeth))

(peek schedule)
(pop schedule)
(rest schedule)
(peek (pop schedule))


;;5.5.1
(#{:a :b :c :d} :c)
(#{:a :b :c :d} :e)
(get #{:a 1 :b 2} :b)
(get #{:a 1 :b 2} :z :nothing-doing)

(into #{[]} [()])
(into #{[1 2]} '[(1 2)])
(into #{[] #{} {}} [()])

(some #{:b} [:a 1 :b 2])
(some #{1 :b} [:a 1 :b 2])

;;5.5.2 sorted-setColor
(sorted-set :b :c :a)
(sorted-set [3 4] [1 2])
;(sorted-set :b 2 :c :a 3 1)

(def my-set (sorted-set :a :b))
;(conj my-set "a")

;;5.3.3 contains?

(contains? #{1 2 4 3} 4)
(contains? [1 2 4 3] 4)

;;5.5.4 clojure.set namespace
(set/intersection #{:humans :fruit-bats :zombies}
                  #{:chupacabra :zombies :humans})

(set/intersection #{:humans :fruit-bats :zombies}
                  #{:chupacabra :zombies :humans}
                  #{:snowman :zombies :elves})

(set/union #{:a :b :c}
           #{:c "a" :a})

(set/difference #{1 2 3 4} #{3 4 5 6})


;;5.6.1
(hash-map :a 1 :b 2 :c 3 :d 4 :e 5)
(let [m {:a 1 1 :b [1 2 3] "4 5 6"}]
  [(get m :a) (get m [1 2 3])])

(let [m {:a 1 1 :b [1 2 3] "4 5 6"}]
  [(m :a) (m [1 2 3])])

(seq {:a 1 :b 2})

(into {} [[:a 1] [:b 2]])

(into {} (map vec '[(:a 1) (:b 2)]))
(apply hash-map [:a 1 :b 2])
(zipmap [:a :b] [1 2])

;;5.6.2
(sorted-map :thx 1138 :r2d 2)
(sorted-map "bac" 2 "abc" 9)
(sorted-map-by #(compare (subs %1 1) (subs %2 1)) "bac" 2 "abc" 9)
;(sorted-map :a 1 "b" 2)

(assoc {1 :int} 1.0 :float)
(assoc (sorted-map 1 :int) 1.0 :float)

;;5.6.3
(seq (hash-map :a 1 :b 2 :c 3))
(seq (array-map :a 1 :b 2 :c 3))

;;5.7
(defn pos [e coll]
  (let [cmp (if (map? coll)
              #(= (second %1) %2)
              #(= %1 %2))]
    (loop [s coll idx 0]
      (when (seq s)
        (if (cmp (first s) e)
          (if (map? coll)
            (first (first s))
            idx)
          (recur (next s) (inc idx)))))))

(defn index [coll]
  (cond
    (map? coll) (seq coll)
    (set? coll) (map vector coll coll)
    :else (map vector (iterate inc 0) coll)))

(index [:a 1 :b 2 :c 3 :d 4])
(index {:a 1 :b 2 :c 3 :d 4})
(index #{:a 1 :b 2 :c 3 :d 4})

(defn pos [e coll]
  (for [[i v] (index coll) :when (= e v)] i))
(pos 3 [:a 1 :b 2 :c 3 :d 4])
(pos 3 {:a 1, :b 2, :c 3, :d 4})
(pos 3 [:a 3 :b 3 :c 3 :d 4])
(pos 3 {:a 3, :b 3, :c 3, :d 4})
(pos #{3 4} {:a 1 :b 2 :c 3 :d 4})
(pos even? [2 3 6 7])

(defn pos [pred coll]
 (for [[i v] (index coll) :when (pred v)] i))
