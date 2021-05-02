(ns specter-exercise
  (:use [com.rpl.specter]))

(def data {:a [{:aa 1 :bb 2}
               {:cc 3}]
           :b [{:dd 4}]})

;; Manual Clojure, value 에만 함수를 적용시키고 다시 맵 구조로 모아주는 함수
(defn map-vals [m afn]
  (->> m
       (map (fn [[k v]] [k (afn v)]))
       (into (empty m))))

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

;; Navigator
(setval [ALL nil?] NONE [1 2 nil 3 nil])
(select [MAP-KEYS] {:a 3 :b 4})
(select [MAP-VALS MAP-VALS] {:a {:b :c}, :d {:e :f}})
(setval [MAP-VALS even?] NONE {:a 1 :b 2 :c 3 :d 4})

;; walker : 데이터 구조의 엘리먼트를 순회하기 좋음
(transform [(walker keyword?)] name {:a {:b {:d [1 2 3 4 5 :6]} :e '(10 "hello")}})
(transform [(walker int?)] str {:a {:b {:d [1 2 3 4 5 :6]} :e '(10 "hello")}})

;; EXAMPLES
;; depth 1인 값들을 1씩 증가
(transform [MAP-VALS MAP-VALS] inc {:a {:aa 1} :b {:ba -1 :bb 2}})

;; :a의 값들 중 짝수인 값들을 1씩 증가
(transform [ALL :a even?] inc [{:a 1} {:a 2} {:a 4} {:a 3}])

;; 시퀀스의 시퀀스에서 3으로 나눠 떨어지는 숫자들을 select 하기
(select [ALL ALL #(= 0 (mod % 3))] [[1 2 3 4] [] [5 3 2 18] [2 4 6] [12]])

;; 주어진 시퀀스의 가장 마지막 홀수를 증가시키기
(transform [(filterer odd?) LAST] inc [2 1 3 6 9 4 8])

;; 중첩 시퀀스에서 nil 제거하기
(setval [MAP-VALS ALL nil?] NONE {:a [1 2 nil 3 nil]})

;; 특정 k-v 쌍 삭제하기
(setval [:a :b :c] NONE {:a {:b {:c 1}}})

;; 특정 k-v 쌍을 삭제하면서 맵 자체도 없애기
(setval [:a (compact :b :c)] NONE {:a {:b {:c 1}}})
(setval [:a :b (compact :c)] NONE {:a {:b {:c 1}}})

;; 인덱스 1이상 4미만의 홀수를 증가시키기
(transform [(srange 1 4) ALL odd?] inc [0 1 2 3 4 5 6 7])

;; 인덱스 2부터 4까지의 값을 [:a :b :c :d]로 치환하기
(setval (srange 2 4) [:a :b :c :d :e] [0 1 2 3 4 5 6 7 8 9])

;; 중첩된 구조에서 값 꺼내기 (get-in과 비슷)
(select ["a" "b"] {"a" {"b" 10}})
(get-in {"a" {"b" 10}} ["a" "b"])

;; 하위 시퀀스 중 최소한 2개의 짝수를 가지는 시퀀스의 마지막에 [:c :d] 이어 붙이기
(setval [ALL
         (selected? (filterer even?) (view count) (pred>= 2))
         END]
        [:c :d]
        [[1 2 3 4 5 6] [7 0 -1] [8 8] []])

;; 주어진 시퀀스에서 인덱스 4이상 11미만의 수 중 짝수만 자리를 바꾸기 [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15], filterer 사용
(transform [(srange 4 13) (filterer even?)] reverse [0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15])

;; 데이터 구조안의 모든 숫자를 꺼내기 {2 [1 2 [6 7]] :a 4 :c {:a 1 :d [2 nil]}}, walker 사용
(select [(walker int?)] {2 [1 2 [6 7]] :a 4 :c {:a 1 :d [2 nil]}})

;; 모든 leaf 노드의 nil 없애기 {1 {:a nil :b "hello" :c {:d nil :e 23}} 2 nil}, walker 사용
(setval [(walker nil?)] NONE {1 {:a nil :b "hello" :c {:d nil :e 23}} 2 nil})


