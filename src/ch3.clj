(ns ch3)


;; 3.1
(if true :truthy :falsey)                                   ;=> truthy
(if [] :truthy :falsey)                                     ;=> truthy
(if nil :truthy :falsey)                                    ;=> truthy
(if false :truthy :falsey)                                  ;=> truthy

(def evil-false (Boolean. "false"))

(= false evil-false)
(if evil-false :truthy :falsey)

(if (Boolean/valueOf "false") :truthy :falsey)

(when (nil? nil) "Actually nil, not false")


;; 3.2
(seq [1 2 3])
(seq [])

(defn print-seq [s]
  (when (seq s)
    (prn (first s))
    (recur (rest s))))

(defn print-seq2 [s]
  (when s
    (prn (first s))
    (recur (next s))))

(print-seq [1 2])

;; 3.3
(def guys-whole-name ["Tlonist" "Harry" "Kim"])

(str (nth guys-whole-name 2) ", "
     (nth guys-whole-name 1) " "
     (nth guys-whole-name 0))

(let [[f-name m-name l-name] guys-whole-name]
  (str l-name ", " f-name " " m-name))

(def date-regex #"(\d{1,2})\/(\d{1,2})\/(\d{4})")
(let [rem (re-matcher date-regex "12/02/2021")]
  (when (.find rem)
    (let [[_ m d] rem]
      {:month m :day d})))

(let [[a b c & more] (range 10)]
  (println "a b c are:" a b c)
  (println "more is:" more))

(let [range-vec (vec (range 10))
      [a b c & more :as all] range-vec]
  (println "a b c are:" a b c)
  (println "more is:" more)
  (println "all is :" all))

(def guys-name-map
  {:f-name "Harry" :m-name "Tlonist" :l-name "Kim"})

(let [{f-name :f-name
       m-name :m-name
       l-name :l-name} guys-name-map]
  (str l-name ", " f-name " " m-name))

(let [{:keys [f-name m-name l-name]} guys-name-map]
  (str l-name ", " f-name " " m-name))

(let [{f-name :f-name :as whole-name} guys-name-map]
  (println "First name is " f-name)
  (println "Whole name is below:")
  whole-name)

(let [{:keys [title f-name m-name l-name]
       :or {title "Mr."}} guys-name-map]
  (println title f-name m-name l-name))

(defn whole-name [& args]
  (let [{:keys [f-name m-name l-name]} args]
    (str l-name ", " f-name " " m-name)))

(whole-name :f-name "Guy" :m-name "Lewis" :l-name "Steele")

(let [{first-thing 0, last-thing 3} [1 2 3 4]]
  [first-thing last-thing])

;; 3.3.4
(defn print-last-name [{:keys [l-name]}]
  (println l-name))

(print-last-name guys-name-map)

;; 3.4
(range 5)
(for [x (range 3)
      y (range 3)]
  [x y])

; (xor 1 2) ;=> error!
; (find-doc "xor")
(bit-xor 1 2)

(for [x (range 2)
      y (range 2)]
  [x y (bit-xor x y)])

(defn xors [max-x max-y]
  (for [x (range max-x)
        y (range max-y)]
    [x y (bit-xor x y)]))


;; 3.4.2 graphics
;(def frame (java.awt.Frame.))
  ;
;(for [meth (.getMethods java.awt.Frame)
;      :let [name (.getName meth)]
;      :when (re-find #"Vis" name)]
;  name)
;
;(.isVisible frame)
;(.setVisible frame true)
;(.setSize frame (java.awt.Dimension. 200 200))
;
;(def gfx (.getGraphics frame))
;(.fillRect gfx 100 100 50 75)
;(.setColor gfx (java.awt.Color. 255 128 0))
;(.fillRect gfx 100 150 75 50)
;
;(doseq [[x y xor] (xors 200 200)]
;  (.setColor gfx (java.awt.Color. xor xor xor))
;  (.fillRect gfx x y 1 1))
;
;;(doseq [[x y xor] (xors 500 500)]
;;  (.setColor gfx (java.awt.Color. xor xor xor))
;;  (.fillRect gfx x y 1 1))
;
;;; new xors
;(defn xors [xs ys]
;  (for [x (range xs)
;        y (range ys)]
;    [x y (rem (bit-xor x y) 256)]))
;
;(defn clear [g] (.clearRect g 0 0 200 200))
;
;(defn f-values [f xs ys]
;  (for [x (range xs)
;        y (range ys)]
;    [x y (rem (f x y ) 256)]))
;
;(defn draw-values [f xs ys]
;  (clear gfx)
;  (.setSize frame (java.awt.Dimension. xs ys))
;  (doseq [[x y v] (f-values f xs ys)]
;    (.setColor gfx (java.awt.Color. v v v))
;    (.fillRect gfx x y 1 1)))