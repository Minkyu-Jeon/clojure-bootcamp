(ns aoc2018-2
  (:require [clojure.string :as str])
  (:require [clojure.core]))

;; 파트 1
;; 주어진 각각의 문자열에서, 같은 문자가 두번 혹은 세번씩 나타난다면 각각을 한번씩 센다.
;; 두번 나타난 문자가 있는 문자열의 수 * 세번 나타난 문자가 있는 문자열의 수를 반환하시오.
;; 예)
;; abcdef 어떤 문자도 두번 혹은 세번 나타나지 않음 -> (두번 나오는 문자열 수: 0, 세번 나오는 문자열 수: 0)
;; bababc 2개의 a, 3개의 b -> (두번 나오는 문자열 수: 1, 세번 나오는 문자열 수: 1)
;; abbcde 2개의 b -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 1)
;; abcccd 3개의 c -> (두번 나오는 문자열 수: 2, 세번 나오는 문자열 수: 2)
;; aabcdd 2개의 a, 2개의 d 이지만, 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 3, 세번 나오는 문자열 수: 2)
;; abcdee 2개의 e -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 2)
;; ababab 3개의 a, 3개의 b 지만 한 문자열에서 같은 갯수는 한번만 카운트함 -> (두번 나오는 문자열 수: 4, 세번 나오는 문자열 수: 3)
;; 답 : 4 * 3 = 12

(def vect (->> "input/day2_input.txt"
               (slurp)
               (str/split-lines)))


(defn two-three-vector [line]
  (let [frequencySet (->> line
                          (char-array)
                          (seq)
                          (frequencies)
                          (vals)
                          (into #{}))]
    (->> [2 3]
         (map (fn [item] (if (frequencySet item)
                           1
                           0))))))

(defn solve [vect]
  (loop [vect vect m [0 0]]
    (let [line (first vect)]
      (if (nil? line)
        (* (nth m 0) (nth m 1))
        (recur (rest vect) (map + m (two-three-vector line)))))))

(solve vect)



;; 파트 2
;; 여러개의 문자열 중, 같은 위치에 정확히 하나의 문자가 다른 문자열 쌍에서 같은 부분만을 리턴하시오.
;; 예)
;; abcde
;; fghij
;; klmno
;; pqrst
;; fguij
;; axcye
;; wvxyz

;; 주어진 예시에서 fguij와 fghij는 같은 위치 (2번째 인덱스)에 정확히 한 문자 (u와 h)가 다름. 따라서 같은 부분인 fgij를 리턴하면 됨.

(defn to-char-seq [str] (->> str (char-array) (seq)))

(defn intersect-chars
  [str1 str2]
  (->>
   (map vector (to-char-seq str1) (to-char-seq str2))
   (map (fn [item] (if (= (nth item 0) (nth item 1))
                     (nth item 0)
                     nil)))
   (keep clojure.core/identity)
   (str/join)))

(defn make-string-pair [vect]
  (loop [total-vect []
         vect vect
         vect2 (rest vect)]
    (if (empty? (rest vect))
      total-vect
      (if (empty? (rest vect2))
        (recur total-vect (rest vect) (rest (rest vect)))
        (recur (conj total-vect [(first vect) (first vect2)]) vect (rest vect2))))))

(->> vect
     (make-string-pair)
     (map (fn [[str1 str2]] (let
                             [intersection (intersect-chars str1 str2)]
                              (if (= (- (count str1) 1) (->> intersection (count)))
                                intersection
                                nil))))
     (keep clojure.core/identity))


;; bvhaknyooqsudzrpggslectkj

