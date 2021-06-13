(ns ch6)

;;6.200
(def baselist (list :apple :tomato))
(def lst1 (cons :banana baselist))
(def lst2 (cons :orange baselist))

(= (next lst1) (next lst2))
(identical? (next lst1) (next lst2))

{:val 5 :L nil :R nil}
(defn xconj [t v]
  (cond
    (nil? t) {:val v :L nil :R nil}))

(xconj nil 5)

(let [some-local :a-key]  
  (get {:a-key 42} some-local)) ;; good (명확)
(let [some-local :a-key]
  (some-local {:a-key 42})) ;; bad (불명확)


(defn xconj [t v]
  (cond
    (nil? t) {:val v :L nil :R nil}
    (< v (:val t)) {:val (:val t)
                    :L   (xconj (:L t) v)
                    :R   (:R t)}))

(def tree1 (xconj nil 5))
(def tree2 (xconj tree1 3))
(def tree3 (xconj tree2 2))

(defn xseq [t]
  (when t
    (concat (xseq (:L t)) [(:val t)] (xseq (:R t)))))

(xseq tree3)

(defn xconj [t v]
  (cond
    (nil? t) {:val v :L nil :R nil}
    (< v (:val t)) {:val (:val t)
                    :L   (xconj (:L t) v)
                    :R   (:R t)}
    :else {:val (:val t)
           :L   (:L t)
           :R   (xconj (:R t) v)}))

(def tree4 (xconj tree3 7))
(xseq tree4)
(identical? (:L tree3) (:L tree4))

;;6.3 lazy evaluation
;;6.1

(defn if-chain [x y z]
  (if x
    (if y
      (if z
        (do
          (println "Made it!")
          :all-truthy)))))

(if-chain () 42 true)
(if-chain true true false)

(defn and-chain [x y z]
  (and x y z (do (println "Made it!") :all-truthy)))

(and-chain () 42 true)
(and-chain true true false)

;;6.3.2
;(steps [1 2 3 4]) => [1 [2 [3 [4 []]]]]
(defn steps [[x & more]]
  (if x
    [x (steps more)]
    []))

(steps [1 2 3 4])
;(steps (range 10000000)) => stackoverflow!

;;compare rest and next
(def very-lazy (-> (iterate #(do (print \.) (inc %)) 1)
                   rest
                   rest
                   rest))

(def less-lazy (-> (iterate #(do (print \.) (inc %)) 1)
                   next
                   next
                   next))

(println (first very-lazy))
(println (first less-lazy))

;; does this work?
(defn lazy-steps [s]
  (lazy-seq
    (if (seq s)
      [(first s) (lazy-steps (rest s))]
      [])))

(defn simple-range [i limit]
  (lazy-seq
    (when (< i limit)
      (cons i (simple-range (inc i) limit)))))

(simple-range 0 10)

;; 6.3 ignoring head
;(let [r (range 1e9)]
;  (first r)
;  (last r))
;
;(let [r (range 1e9)]
;  (last r)
;  (first r))


;(iterate (fn [n] (/ n 2)) 1)
(defn triangle [n]
  (/ (* n (+ n 1)) 2))

(triangle 10)

(map triangle (range 1 11))
(def tri-nums (map triangle (iterate inc 1)))
(take 10 tri-nums)
(take 10 (filter even? tri-nums))
(nth tri-nums 99)
(double (reduce + (take 1000 (map / tri-nums))))
(take 2 (drop-while #(< % 10000) tri-nums))

;;6.3.5 delay and force macro
(defn defer-expensive [cheap expensive]
  (if-let [good (force cheap)]
    good
    (force expensive)))

(defer-expensive (delay :cheap)
                 (delay (do (Thread/sleep 5000) :expensive)))

(delay? (delay :cheap))

;(defer-expensive (delay false)
;                 (delay (do (Thread/sleep 5000) :expensive)))

(defn inf-triangles [n]
  {:head (triangle n)
   :tail (delay (inf-triangles (inc n)))})

(defn head [l] (:head l))
(defn tail [l] (force (:tail l)))

(def tri-nums (inf-triangles 1))
(head tri-nums)
;=> 1
(head (tail tri-nums))
;=> 3
(head (tail (tail tri-nums)))

(defn taker [n l]
  (loop [t n, src l, ret []]
    (if (zero? t)
      ret
      (recur (dec t) (tail src) (conj ret (head src))))))
(defn nthr [l n]
  (if (zero? n)
    (head l)
    (recur (tail l) (dec n))))
(taker 10 tri-nums)
;=> [1 3 6 10 15 21 28 36 45 55]
(nthr tri-nums 99)
;=> 5050


;;6.4 quicksort
(defn rand-ints [n]
  (take n (repeatedly #(rand-int n))))

(rand-ints 10)

(defn sort-parts [work]
  (lazy-seq
    (loop [[part & parts] work]
      (if-let [[pivot & xs] (seq part)]
        (let [smaller? #(< % pivot)]
          (recur (list*
                   (filter smaller? xs)
                   pivot
                   (remove smaller? xs)
                   parts)))
        (when-let [[x & parts] parts] (cons x (sort-parts parts)))))))

(defn qsort [xs] (sort-parts (list xs)))
(qsort [2 1 4 3])
(qsort (rand-ints 20))
(first (qsort (rand-ints 100)))

(take 10 (qsort (rand-ints 10000)))