(ns ch2
  (:require [clojure.set :as s]
            [clojure.string :refer (capitalize)])
  (:import [java.util HashMap]
           [java.util.concurrent.atomic AtomicLong]))

(def numbers
  [127 0x7F 0177 32r3V 2r01111111])
(def yucky-pi 22/7)
(def keyword-example [:2 :? :hello :ThisIsAnExmaple :키워드])
(def strings ["This is a String"
              "This is also
              A String!~"])
(def characters
  [\a \A \u0042 \\])

(def collections
  {:list   '(1 2 '(\a \b \c) 4)
   :vector [1 2 :a :c]
   :map    {1       "one"
            2       "two"
            "three" 3}
   :set    #{1 2 3 "three"}})

(def function-call
  "함수호출!"
  (vector 1 2 3))

(defn make-set [x y]
  (println "making a set!")
  #{x y})

(defn make-set
  ([x] #{x})
  ([x y] #{x y}))

(defn arity2+
  "(arity2+ 1 2 3 4 5) -> [1 2 (3 4 5)]"
  [first second & more]
  (vector first second more))

(defn arity2++
  "(arity2+ 1 2 3 4 5) -> [1 2 3 4 5]"
  [first second & more]
  (flatten (vector first second more)))

(def make-list0 #(list))

(def make-list2 #(list %1 %2))

(def make-list2 #(list %1 %2 %&))

(do
  (def x 5)
  (def y 4)
  (+ x y)
  [x y])

(let [r 5
      pi 3.141592653589793
      r-squared (* r r)]
  (println "radius is " r)
  (* pi r-squared))

(defn print-down-from [x]
  (when (pos? x)
    (println x)
    (recur (dec x))))

(defn sum-down-from [sum x]
  (if (pos? x)
    (recur (+ sum x) (dec x))
    sum))

(defn sum-down-from2 [initial-x]
  (loop [sum 0
         x initial-x]
    (if (pos? x)
      (recur (+ sum x) (dec x))
      sum)))

;; https://8thlight.com/blog/patrick-gombert/2015/03/23/tail-recursion-in-clojure.html (on tail recursion)
(defn absolute-value [x]
  (if (pos? x)
    x
    (- x)))

;(fn [x] (recur x) (println x))

;; cons and conj, mnemonic
;; https://bfontaine.net/blog/2014/05/25/how-to-remember-the-difference-between-conj-and-cons-in-clojure/
(cons 1 [2 3])
(conj [2 3] 1)

(quote age)

(def age 9)
(quote age)

(quote (cons 1 [2 3]))

;; ERROR!
;; (cons 1 (2 3))

(cons 1 (quote (2 3)))
(cons 1 '(2 3))

[1 (+ 2 3)]
'(1 (+ 2 3))

`(1 2 3)

`map
`Integer
`(map even? [1 2 3])
`is-always-right

`(+ 10 (* 3 2))
`(+ 10 ~(* 3 2))

(let [x 2]
  `(1 ~x 3))

;; ERROR!
;(let [x 2]
;  `(1 ~(2 3)))

`(1 (2 3))

(let [x '(2 3)]
  `(1 ~x))

(let [x '(2 3)]
  `(1 ~@x))

`potion#

(java.util.Locale/KOREA)

(Math/sqrt 9)

(new java.awt.Point 0 1)
(new java.util.HashMap {"foo" 42 "bar" 9 "baz" "quux"})
(java.util.HashMap. {"foo" 42 "bar" 9 "baz" "quux"})

;;(js.Date.)

(.-x (java.awt.Point. 0 10))
(.divide (java.math.BigDecimal. "42") 2M)


(let [origin (java.awt.Point. 0 0)]
  (set! (.-x origin) 15)
  (str origin))


;; new java.util.Date().toString().endsWith("2013")
(.endsWith (.toString (java.util.Date.)) "2014") ;; false
(.endsWith (.toString (java.util.Date.)) "2021") ;; true

(.. (java.util.Date.) toString (endsWith "2021"))


;java.util.HashMap props = new java.util.HashMap ();
;props.put ("name", "상현")
;props.put ("language", "clojure")
;props.put ("position", "worker")

(doto (java.util.HashMap.)
  (.put "name" "상현")
  (.put "language" "clojure")
  (.put "position" "worker"))

;; https://stackoverflow.com/questions/26050540/what-is-the-difference-between-the-hash-map-and-array-map-in-clojure
;; https://clojuredocs.org/clojure.core/hash-map
(array-map 1 2 3 4)
(hash-map 1 2 3 4)
(class (hash-map 1 2 3 4))
(class (array-map 1 2 3 4))
(class (new java.util.HashMap))

;; (gen-class)

(defn testfunc []
  (throw (Exception. "thrown!")))

(defn throw-catch
  "f 는 함수"
  [f]
  [(try
     (f)
     (catch ArithmeticException e "No division by zero!")
     (catch Exception e (str "You are so bad " (.getMessage e)))
     (finally (println "returning...")))])

(s/intersection #{1 2 3} #{2 3 4})

(map capitalize ["hello" "world"])

(HashMap. {"joy of?" "Clojure!"})
(AtomicLong. 42)