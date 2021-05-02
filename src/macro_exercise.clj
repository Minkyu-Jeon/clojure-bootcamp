(ns macro_exercise)

(read-string "(+ 1 2 3 4 5)")
(class (read-string "(+ 1 2 3 4 5)"))

(eval (read-string "(+ 1 2 3 4 5)"))
(class (eval (read-string "(+ 1 2 3 4 5)")))

(+ 1 2 3 4 5)

;; replacing addition with ease
(let [expression (read-string "(+ 1 2 3 4 5)")]
  (cons (read-string "*")
        (rest expression)))
; (eval *1)

; wrong!
;(let [expression (+ 1 2 3 4 5)]
;  (cons (read-string "*")
;        (rest expression)))

(let [expression (quote (+ 2 3 4 5 6))]
  (cons (read-string "*")
        (rest expression)))
; (eval *1)

(defn print-with-asterisk [printable-argument]
  (print "******")
  (print printable-argument)
  (print "******"))

(print-with-asterisk "hello!")

;; 표현(expression)이 항상 함수의 바디보다 먼저 평가된다
(print-with-asterisk (do (print "in argument expression") "hi"))

(quote 1)
(quote "hello")
(quote :kthx)
(quote kthx)

'(+ 1 2 3 4 5)

(let [expression '(+ 1 2 3 4 5)]
  (cons '* (rest expression)))


(defmacro when-macro
  "Evaluates test. If logical true, evaluates body in an implicit do."
  {:added "1.0"}
  [test & body]
  (list 'if test (cons 'do body)))


(when-macro (= 2 (+ 1 1))
  (print "you got")
  (print " the touch!")
  (println))

(list 'if
      '(= 2 (+ 1 1))
      (cons 'do
            '((print "you got")
              (print " the touch!")
              (println))))

(eval (list 'if
            '(= 2 (+ 1 1))
            (cons 'do
                  '((print "you got")
                    (print " the touch!")
                    (println)))))

(macroexpand-1 '(when (= 2 (+ 1 1)) (do (print "hello world!"))))
(macroexpand '(when (= 2 (+ 1 1)) (do (print "hello world!"))))


;; chapter 2
(assert (= 1 2))
(assert (= 1 1))

(macroexpand '(assert (= 1 2)))
(def a 4)
'(1 2 3 a 5)
(list 1 2 3 a 5)

`(1 2 3 '~a 5)
`(1 2 3 (quote (clojure.core/unquote a)) 5)

(def other-numbers '(4 5 6 7 8))
`(1 2 3 ~@other-numbers 9 10)
`(1 2 3 other-numbers 9 10)
`(1 2 3 ~other-numbers 9 10)
(concat `(1 2 3) other-numbers `(9 10))

(defmacro squares [xs] (list 'map '#(* % %) xs))
(squares (range 10))

;(ns foo (:refer-clojure :exclude [map]))
(def map {:a 1 :b 2})
(macro_exercise/squares (range 10))
(macro_exercise/squares :a)
(first (macroexpand `(macro_exercise/squares (range 10))))
({:a 1 :b 2} :nonexistent-key :default-value)


(defmacro squares [xs] (list 'map (fn [x] (* x x)) ~xs))
(squares (range 10))

`(* ~`x ~`x)
(defmacro squares [xs] `(map (fn [~'x] (* ~'x ~'x)) ~xs))
(squares (range 10))

;; let's look into 'and'
(and)


(defmacro inspect-caller-locals []
  (->> (keys &env)
       (map (fn [k] [`'~k k]))
       (into {})))

(let [foo "bar" hello "world"] (inspect-caller-locals))

(defmacro inspect-caller-locals []
  (->> (keys &env)
       (map (fn [k] [(list 'quote k) k]))
       (into {})))

(let [grant "crapp" Tayla "damir"] (inspect-caller-locals))