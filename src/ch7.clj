(ns ch7)

;; 7.1.1
(def fifth (comp first rest rest rest rest))
(fifth [1 2 3 4 5])

(defn fnth [n]
  (apply comp (cons first (take (dec n) (repeat rest)))))

((fnth 5) '[a b c d e])

(map (comp
       keyword
       #(.toLowerCase %)
       name)
     '(a B C))

((partial + 5) 100 200)

(let [truthiness (fn [v] v)]
  [(complement truthiness) true
   (complement truthiness) 42
   (complement truthiness) false
   (complement truthiness) nil])

((complement even?) 2)

; 함수를 데이터로 사용하기
(defn join
  {:test (fn [] (assert (= (join "," [1 2 3]) "1,3,3")))}
  [sep s]
  (apply str (interpose sep s)))


(use '[clojure.test :as t])
(t/run-tests)

;; 7.1.2 고차함수
(sort [1 5 7 0 -42 13])
(sort ["z" "x" "a" "aa"])
(sort [(java.util.Date.) (java.util.Date. 100)])
(sort [[1 2 3] [-1 0 9] [3 2 1]])

(sort > [7 1 4])
(sort ["z" "x" "a" "aa" 1 5 8])
(sort [{:age 99} {:age 1} {:age 23}])

(sort [[:a 17] [:d 10] [:c 23]])
(sort second [[:a 17] [:d 10] [:c 23]])
(sort-by second [[:a 17] [:d 10] [:c 23]])

(sort-by str ["z" "x" "a" "aa" 1 5 8])
(sort-by str [{:age 99} {:age 1} {:age 23}])


(def info [{:name "Dan" :height 198.1 :weight 91}
           {:name "Victor" :height 183.2 :weight 71}
           {:name "Gao" :height 173.8 :weight 68}
           {:name "Winston" :height 177.3 :weight 74}
           {:name "Wei" :height 177.3 :weight 64}
           {:name "Albert" :height 168.1 :weight 77}])

(def sort-by-bmi (partial sort-by #(/ (:weight %) (* (:height %) (:height %)))))
(sort-by-bmi info)

;; 리턴값으로의 함수
(defn columns [column-names]
  (fn [row] (vec (map row column-names))))
(sort-by (columns [:name :height :weight]) info)

(vec (map (info 0) [:name :height :weight]))

(sort-by ["Dan" 198.1 91] info)
;;7.1.3 순수함수

;참조 투명성
(defn keys-apply [f ks m]
  (let [only (select-keys m ks)]
    (zipmap (keys only)
            (map f (vals only)))))

(keys-apply #(.toUpperCase %) #{:name} (info 0))
(defn manip-map [f ks m]
  (merge m (keys-apply f ks m)))

(manip-map #(int (/ % 2)) #{:height :weight} (info 0))
(defn mega-weight!
  "info 라는 전역변수에 의존적. 항상 같은 결과를 낸다는 보장이 없음"
  [ks]
  (map (partial manip-map #(int (* % 1000)) ks) info))

(mega-weight! [:weight])

;;7.1.4 인자 이름 지정
(defn slope
  [& {:keys [p1 p2] :or {p1 [0 0] p2 [1 1]}}]
  (float (/ (- (p2 1) (p1 1)) (- (p2 0) (p1 0)))))

(slope :p1 [4 15] :p2 [3 21])

;;7.1.5 선행과 후행으로 함수 제약하기
(defn slope-new [p1 p2]
  {:pre  [(not= p1 p2) (vector? p1) (vector? p2)]
   :post [(float? %)]}
  (/ (- (p2 1) (p1 1))
     (- (p2 0) (p1 0))))

(slope-new [19 19] [19 19])
(slope-new [19 19] '(11 12))
(slope-new [10 1] [1 20])
(slope-new [10 20] [5 11.1])

;; 함수로부터 선언 분리하기
(defn put-things [m]
  (into m {:meat "beer" :veggie "broccoli"}))

(put-things {})
(defn vegan-constraint [f m]
  {:pre  [(:veggie m)]
   :post [(:veggie %) (nil? (:meat %))]}
  (f m))

(vegan-constraint put-things {:veggie "carrot"})
(defn balanced-diet [f m]
  {:post [(:meat %)
          (:veggie %)]}
  (f m))

(balanced-diet put-things {})

(defn finicky [f m]
  {:post [(= (:meat %) (:meat m))]}
  (f m))

(finicky put-things {:meat "chicken"})

;; closure
; 생성된 위치의 컨텍스트에서 로컬에 접근할 수 있는 함수
(def times-two
  (let [x 2]
    (fn [y] (* y x))))

(times-two 6)

(def add-and-get
  (let [ai (java.util.concurrent.atomic.AtomicInteger.)]
    (fn [y] (.addAndGet ai y))))

(add-and-get 8)
(add-and-get 1)
(add-and-get 100)

;; 7.2.1 클로저를 리턴하는 함수
(defn times-n [n]
  (let [x n]
    (fn [y] (* y x))))

(times-n 4)
(def times-four (times-n 4))
(times-four 10)

;; 7.2.2 인자 감싸기
(defn times-n [n]
  (fn [y] (* y n)))

(defn divisible [denom]
  (fn [num]
    (zero? (rem num denom))))

((divisible 3) 6)
((divisible 3) 7)

;;7.2.3 클로저를 함수로 전달하기
(filter even? (range 10))
(filter (divisible 4) (range 10))
(defn filter-divisible [denom s]
  (filter (fn [num] (zero? (rem num denom))) s))

(filter-divisible 4 (range 10))

(defn filter-divisible-shortened [denom s]
  (filter #(zero? (rem % denom)) s))

(filter-divisible-shortened 7 (range 30))

;; 7.2.4 클로저 콘텍스트 공유
(def bearings [{:x 0, :y 1} {:x 1, :y 0} {:x 0, :y -1} {:x -1, :y 0}])
(defn forward [x y bearing-num]
  [(+ x (:x (bearings bearing-num)))
   (+ y (:y (bearings bearing-num)))])
(forward 5 5 0)
(forward 5 5 2)
(defn bot [x y bearing-num]
  {:coords  [x y]
   :bearing ([:north :east :south :west] bearing-num)
   :forward (fn [] (bot (+ x (:x (bearings bearing-num)))
                        (+ y (:y (bearings bearing-num))) bearing-num))})

(:coords (bot 5 5 0))
(:bearing (bot 5 5 0))
(:coords ((:forward (bot 5 5 0))))

(defn bot [x y bearing-num]
  {:coords     [x y]
   :bearing    ([:north :east :south :west] bearing-num)
   :forward    (fn [] (bot (+ x (:x (bearings bearing-num)))
                           (+ y (:y (bearings bearing-num)))
                           bearing-num))
   :turn-right (fn [] (bot x y (mod (+ 1 bearing-num) 4)))
   :turn-left  (fn [] (bot x y (mod (- 1 bearing-num) 4)))})

(:bearing ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))
(:coords ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))

;; 뒤의 예제는 생략;; 하겠습니다.

;; 7.3 재귀적으로 생각하기
(defn pow [base exp]
  (if (zero? exp)
    1
    (* base (pow base (dec exp)))))

(pow 3 20)
(pow 1.0001 999)
(pow 2 10000)

(defn pow [base exp]
  (letfn [(kapow [base exp acc]
            (if (zero? exp)
              acc
              (recur base (dec exp) (* base acc))))]
    (kapow base exp 1)))

(pow 2N 10000)

