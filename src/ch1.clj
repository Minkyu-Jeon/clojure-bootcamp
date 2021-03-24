(ns ch1
  (:require [java-time :as t]))

;1.1.5
(for [x [:a :b], y (range 5) :when (odd? y)]
  [x y])

(doseq [x [:a :b], y (range 5) :when (odd? y)]
  (prn x y))

;1.2.2
(+ 1 (* 2 3))

(defn r->lfix
  ([a op b] (op a b))
  ([a op1 b op2 c] (op1 a (op2 b c)))
  ([a op1 b op2 c op3 d] (op1 a (op2 b (op3 c d)))))

(r->lfix 1 + 2)
(r->lfix 1 + 2 + 3)
(r->lfix 10 * 2 + 3)

(defn l->rfix
  ([a op b] (op a b))
  ([a op1 b op2 c] (op2 c (op1 a b)))
  ([a op1 b op2 c op3 d] (op3 d (op2 c (op1 a b)))))

(l->rfix 10 * 2 + 3)
(l->rfix 1 + 2 * 3)

(def order {+ 0
            - 0
            * 1
            / 1})

(defn infix3 [a op1 b op2 c]
  (if (< (get order op1) (get order op2))
    (r->lfix a op1 b op2 c)
    (l->rfix a op1 b op2 c)))


(def && #(and % %2))
(def || #(or  % %2))

(def ^:dynamic *rank* (zipmap [- + * / < > && || =]
                              (iterate inc 1)))

(defn- infix*
  [[a b & [c d e & m]]]
  (cond
    (vector? a) (recur (list* (infix* a) b c d e m))
    (vector? c) (recur (list* a b (infix* c) d e m))
    (ifn? b) (if (and d (< (*rank* b 0) (*rank* d 0)))
                (recur (list a b (infix* (list* c d e m))))
                (recur (list* (b a c) d e m)))
    :else a))

(defn infix [& args]
  (infix* args))


(+ 1 2 3 4 5 6 7 8 9 10)
(def numbers (range 1 11))
(apply + numbers)

(< 0 42)
(< 0 1 3 9 110 23232)
(< 0 1 3 9 110 -1 23232)

;1.4.3
(defprotocol Concatenatable
  (cat [this other]))

(extend-type String
  Concatenatable
  (cat [this other]
    (.concat this other)))

(cat "House" " of Cards")

(extend-type java.util.List
  Concatenatable
  (cat [this other]
    (concat this other)))

(cat [1 2 3] [4 5 6])


;1.5
(defn initial-board []
  [\r \n \b \q \k \b \n \r
   \p \p \p \p \p \p \p \p
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \- \- \- \- \- \- \- \-
   \P \P \P \P \P \P \P \P
   \R \N \B \Q \K \B \N \R])

(def ^:dynamic *file-key* \a)
(def ^:dynamic *rank-key* \0)
(defn- file-component [file]
  (- (int file) (int *file-key*)))
(defn- rank-component [rank]
  (->> (int *rank-key*)
       (- (int rank))
       (- 8)
       (* 8)))
(defn- index [file rank]
  (+ (file-component file) (rank-component rank)))
(defn lookup [board pos]
  (let [[file rank] pos]
    (board (index file rank))))
(lookup (initial-board) "a1")

(letfn [(index [file rank]
          (let [f (- (int file) (int \a))
                r (* 8 (- 8 (- (int rank) (int \0))))]

            (+ f r)))]
  (defn lookup2 [board pos]
    (let [[file rank] pos]
      (board (index file rank)))))
(lookup2 (initial-board) "a1")


(defn lookup3 [board pos]
  (let [[file rank] (map int pos)
        [fc rc]     (map int [\a \0])
        f (- file fc)
        r (* 8 (- 8 (- rank rc)))
        index (+ f r)]
    (board index)))
(lookup3 (initial-board) "a1")

;; for loop
(defmacro for-loop [[sym init check change :as params] & steps]
  `(loop [~sym ~init value# nil]
     (if ~check
       (let [new-value# (do ~@steps)]
         (recur ~change new-value#))
       value#)))

;; Usage:
(for-loop [i 0 (< i 10) (inc i)]
          (prn i))



(comment
  "Some Cool Clojure functions!")

(comment
  "into")
(into {} [[:a 1] [:b 2] [:c 3] [:d 4]])
(into [] (range 10))
(into (sorted-map) {:b 2 :c 3 :a 1})
(into (sorted-set) [1 1 2 2 2 3 3 3 3 3 3 3 3 3 3 99])


(comment
  "mapcat = map + concat")
(defn double-triple [x]
  [(* x 2) (* x 3)])
(map double-triple (range 5))
(mapcat double-triple (range 5))


(comment
  "empty")
(empty (range 10))
(empty [1 2 3 4 5 6 7])
(empty {:body {:apiKey "abssjiejb==v12dQWEz"
               :name   "helloworld"}})


(comment
  "fnil, the default provider")
(defn intro [job restaurant]
  (format "저는 %s 엔지니어고 가장 좋아하는 점심메뉴는 %s 입니다." job restaurant))
(def intro-with-defaults
  (fnil intro "백엔드" "백채김치찌개"))
(intro-with-defaults "프론트엔드" "부대찌개")
(intro-with-defaults nil "라멘")
(intro-with-defaults "데브옵스" nil)


(comment
  "juxt = ((juxt a b c) x) => [(a x) (b x) (c x)]")
(def data
  {:temperature    10
   :humidity       20
   :wind_direction 15
   :wind_velocity  85})
(prn ((juxt :temperature :humidity :wind_direction :wind_velocity) data))

(defn add-and-multiply
  [n_add n_multiply original]
  ((juxt #(+ n_add %) #(* n_multiply %)) original))
(add-and-multiply 10 2 5)

(comment
  "partial")
(def add-five
  (partial + 5))
(add-five 10)
(defn greet [greeting person]
  (format "%s, %s!" greeting person))
(greet "Yo" "Chuck")
(def greet-korean
  (partial greet "안녕!"))
(greet-korean "Clojure")

(comment
  "comp")
(defn sum-stringify-reverse
  [input]
  (->> input
       (apply (comp reverse str +))))
(sum-stringify-reverse (range 100))                         ;(\0 \5 \9 \4)

(comment
  "iterate = infinite lazy sequence!")
(take 10 (iterate (partial * 3) 20))                        ;(20 60 180 540 1620 4860 14580 43740 131220 393660)
(defn ndays
  "vector of 7 days"
  [input]
  (->> (t/local-date)
       (iterate #(t/plus % (t/days 1)))
       (take input)))
(ndays 15)

(comment
  "cycle")
(take 10 (cycle [1 2 3]))                                   ;(1 2 3 1 2 3 1 2 3 1)
(partition 9 (cycle (range 10)))                            ;No!
(take 99 (partition 9 (cycle (range 10))))