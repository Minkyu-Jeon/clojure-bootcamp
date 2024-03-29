(ns aoc2018-1
  (:require [clojure.string]))


;; 파트 1
;; 주어진 입력의 모든 숫자를 더하시오.
;; 예) +10 -2 -5 +1 이 입력일 경우 4를 출력
(def input (->> (slurp "input/day1_input.txt")
                (clojure.string/split-lines)
                (map #(Integer/parseInt %))))

(comment (reduce + input))


;; 파트 2
;; 주어진 입력의 숫자를 더할 때 마다 나오는 숫자 중, 처음으로 두번 나오는 숫자를 리턴하시오.
;; 예) +3, +3, +4, -2, -4 는 10이 처음으로 두번 나오는 숫자임.
;; 0 -> 3 (+3) -> 6 (+3) -> 10(+4) -> 8(-2) -> 4(-4) -> 7(+3) -> 10(+3) -> ...


(defn find-first-twice-number [initialVect]
  (loop
   [vect initialVect
    acc 0
    resultSet #{}]
    (if (resultSet (+ acc (first vect)))
      (+ acc (first vect))
      (recur (rest vect)
             (+ acc (first vect))
             (conj resultSet (+ acc (first vect)))))))

(find-first-twice-number (cycle input))
