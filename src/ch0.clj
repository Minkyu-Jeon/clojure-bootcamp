(ns ch0)

;; def => 상수
(def x 1)
(inc x) ;; 2
(prn x) ;; 2?

;; defn => 함수 선언 (매크로)
(defn plus_5 [x] (+ x 5))
(plus_5 10)
(defn sum_four_numbers [x1 x2 x3 x4] (+ x1 x2 x3 x4))
(sum_four_numbers 10 20 30 40)
(defn sum_indefinite [& nums]
  (eval `(+ ~@nums)))
(sum_indefinite 1 2 3 4 5)


;; 기본 데이터 타입
(def number 127)
(def number-hex 0x7F)
(def number-oct 0177)
(def number-32 32r3V)

(def yucky-pi 22/7)
(def keyword-example [:2 :? :hello :ThisIsAnExmaple :키워드])
(def strings ["This is a String"
              "This is also
              A String!~"])
(def characters
  [\a \A \u0042 \\])


;; 자료구조
(def collections
  {:list   '(1 2 '(\a \b \c) 4)
   :vector [1 2 :a :c]
   :map    {1       "one"
            2       "two"
            "three" 3}
   :set    #{1 2 3 "three"}})

;; list
;; 앞에서 추가할 때 효율이 좋음
'(1 2 3 4 5)
'(- + \a "안녕!" [0 0] 122)
(cons 0 '(1 2 3))
(conj '(1 2 3) 4)

;; 왜 '(quote)가 필요할까?
;; clojure는 lisp의 방언. lisp은 list processing의 줄임말
;; list에서 첫번째 argument는 함수 혹은 연산자. quote로 평가를 막지 않으면 리스트가 평가됨
;; (1 2 3 4 5)를 repl에 실행하면 어떻게 될까?


;; vector
[99 100 101]
(def vector-example [:a "b" \c (def d "d") (defn e [] "e") [["f"]]])
(def vector-example2 [[1 2 3]
                      [4 5 6]
                      [7 8 9]])

(nth vector-example 1)                                      ;; "b"

(get-in vector-example2 [1 1])                              ;; 5
(get-in vector-example [5 0 0])                             ;; "f"


;; map
(def test-map {:a      1
               :b      "b"
               [1 0 1] {}
               #{0 99} (defn test-inc [x] (inc x))})
(test-map :a)
(:b test-map)
(test-map [1 0 1])
(test-map #{0 99})                                          ;test 함수를 실행하려면?

;;set
#{1 2 3}
;; #{1 2 3 3} ERROR!
;; 중복이 없다는 것을 이용해 contains?의 용도로 사용 가능
(some #{1} [2 3])
(->> [1 1 1 2 2 2 2 3 3 3 3 3 3 3 3 4 5]
     (into #{}))                                            ;; 순서가 보장 안됨

(->> [1 1 1 2 2 2 2 3 3 3 3 3 3 3 3 4 5]
     (into (sorted-set)))                                   ;; 순서 보장


;; code is data, data is code?
(+ 1 2)
;; 1과 2를 더해라! -> 함수의 평가
;; + 와 1과 2를 갖는 리스트 -> 리스트 데이터
;; 코드의 표현식과 데이터의 표현식이 일치한다! -> 매크로가 매우 강력함.

(read-string "(+ 1 2 3 4 5)")
(eval (read-string "(+ 1 2 3 4 5)"))

;; 코드를 on-the-fly로 변형시켜서 다른 일을 시킬 수도 있다.
(let [original (read-string "(+ 1 2 3 4 5)")]
  (-> (cons '* (rest original))
      (eval)))

;; 없는 문법을 만들어 낼 수도 있다.
(for [i (range 0 10 2)]
  i)

;; clojure의 for는 sequence -> sequence로 바꾸는 monad.
;; Java/JavaScript는 한 loop에서의 연산이 그 전 혹은 다음 연산의 값과 concat 되지 않음
;; 하지만 매크로를 사용해서 다른 언어와 비슷한 문법을 만들어 낼 수 있음

(defmacro for-loop [[sym init check change :as params] & steps]
  `(loop [~sym ~init value# nil]
     (if ~check
       (let [new-value# (do ~@steps)]
         (recur ~change new-value#))
       value#)))

;; Usage:
(for-loop [i 0 (< i 10) (inc i)]
          i)
;; 하지만 매크로는 어렵습니다!!



;; 고차 함수
;; map, filter, reduce ... etc

;; (map f coll1 coll2 ..)
(map inc [1 2 3 4])
(map vector [1 2 3 4])
(map + [1 2 3] [10 20 30])


;; (filter pred coll)
;; predicate은 true/false로 평가되는 식
;; pred가 true인 것만 가져옴
(filter even? (range 10))
(filter #(and (even? %) (> % 5)) (range 10))

;; (reduce f val coll)

(reduce + (range 10))
(reduce (fn [acc v] (if (= 0 (mod v 3))
                      (+ acc v)
                      acc))
        0 (range 100))

(reduce (fn [acc [k v]] (if (= (class v) java.lang.String)
                          (str acc v)
                          acc))
        "hello"
        {:a \C
         :b 23
         :c " ,world!"})
;; reduce는 사실상 all-mighty한 고차함수라 지양하는 것이 좋습니다!


;; loop recur
;; clojure의 for != 절차 지향 언어의 for
;; 예를 들어 이중 loop 은?

;; for(int i=0; i<10; i++)
;; for(int j=0; j<10; j++)
;; k++

(def k 0)
(for [i (range 10)
      j (range 10)]
  (inc k)) ;; 안된다.

;; atom, ref, var 등을 써야함
(def k-atom (atom 1))
(for [i (range 10)
      j (range 10)]
  (swap! k-atom inc))

(loop [i 0] ;; for i=0
  (if (< i 100) ;; i<100
    (recur (inc i)) ;; i++
    (prn "i->" i)))

(loop [x 10]
  (when (> x 1)
    (println x)
    (recur (- x 2))))
