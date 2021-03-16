(ns ch1.ch1
  (:require [java-time :as t]))


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
(take 10 (cycle [1 2 3])) ;(1 2 3 1 2 3 1 2 3 1)
(partition 9 (cycle (range 10))) ;No!
(take 99 (partition 9 (cycle (range 10))))