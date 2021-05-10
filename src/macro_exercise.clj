(ns macro_exercise)


;; Macro Axioms
; 1. Macro 는 한 코드를 다른 코드로 변환시키는 함수이다.
; 2. Macro 의 입력은 '코드' 이다.
; 3. Macroexpansion 은 하나의 표현만 리턴할 수 있다.

;; Macro 기본 지식
; ' quote
; ~ unquote
; ` syntax quote
; ~@ unquote splicing
; # auto gen-sym



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
;(assert (= 1 2))
(assert (= 1 1))

(macroexpand '(assert (= 1 2)))
(def a 4)
'(1 2 3 a 5)
(list 1 2 3 a 5)

`(1 2 3 '~a 5)
`(1 2 3 (quote (clojure.core/unquote a)) 5)

(def other-numbers '(4 5 6 7 8))
`(1 2 3 other-numbers 9 10)
`(1 2 3 ~other-numbers 9 10)
(concat `(1 2 3) other-numbers `(9 10))
`(1 2 3 ~@other-numbers 9 10)

(defmacro squares1 [xs] (list 'map '#(* % %) xs))
(squares1 (range 10))

;(ns foo (:refer-clojure :exclude [map]))
(def map {:a 1 :b 2})
(squares1 (range 10))
(squares1 :a)
(first (macroexpand `(squares1 (range 10))))
({:a 1 :b 2} :nonexistent-key :default-value)


(defmacro squares2 [xs] (list 'map (fn [x] (* x x)) ~xs))
;(squares2 (range 10))

`(* ~`x ~`x)
(defmacro squares3 [xs] `(map (fn [~'x] (* ~'x ~'x)) ~xs))
(squares3 (range 10))

;; let's look into 'and'
(and)

(defmacro info-about-caller []
  (clojure.pprint/pprint {:form &form :env &env})
  `(println "macro was called!"))

(let [foo "bar" hello "world"] (info-about-caller))

(defmacro inspect-caller-locals1 []
  (->> (keys &env)
       (map (fn [k] [`'~k k]))
       (into {})))

(let [foo "bar" hello "world"] (inspect-caller-locals1))

(defmacro inspect-caller-locals2 []
  (->> (keys &env)
       (map (fn [k] [(list 'quote k) k]))
       (into {})))

(let [grant "crapp" Tayla "damir"] (inspect-caller-locals2))

;; chater 3 - use your powers wisely
(defn square [x] (* x x))
(map square (range 10))

(defmacro square-macro [x] `(* ~x ~x))
; (map square-macro (range 10)) error. why?
(macroexpand '(square-macro 10))
(map (fn [n] (square n)) (range 10))


(fn? @#'square-macro)
(@#'square-macro 9)
(@#'square-macro nil nil 9)


(defmacro do-multiplication [expression]
  (cons `* (rest expression)))
(do-multiplication '(+ 3 4))
(do-multiplication (+ 3 4))

; (map (fn [x] (do-multiplication x)) ['(+ 3 4) '(- 10 2)]) - 실패!, how to fix?

; Macros can be contagious
(defmacro log [& args]
  `(println (str "[INFO] " (clojure.string/join " : " ~(vec args)))))
(log "that went well!")
(log "Item #1" "created by me!")

(defn send-email [user messages]
  (Thread/sleep 1000))

(defn notify-everyone [messages]
  (apply log messages)
  (send-email "hello" "world")
  (send-email "hello2" "world2"))
; Can't take value of a macro: #'macro_exercise/log

(defn notify-everyone [messages]
  `(do
      (send-email "hello" ~messages)
      (send-email "hello2" ~messages)
      (log ~@messages)))

; far better when log is NOT a macro!
(defn log-non-macro [& args]
  (println (str "[INFO] " (clojure.string/join " : " args))))

(log-non-macro "hi" "there!")
(apply log-non-macro ["hi" "there!"])


;macros can be tricky
(defmacro our-and
  ([] true)
  ([x] x)
  ([x & next]
   `(if ~x (our-and ~@next) ~x)))

(our-and true true)
(our-and true true false)
(our-and true true (= 1 1))
(our-and 1 2 3 false)

(our-and (do (println "hi there!")) (= 1 2) (= 3 4))
; why evaluated twice?
(macroexpand-1 '(our-and (do (println "hi there!")) (= 1 2) (= 3 4)))


(defmacro our-and-fixed
  ([] true)
  ([x] x)
  ([x & next]
   `(let [arg# ~x]
      (if arg# (our-and-fixed ~@next) arg#))))

(our-and-fixed (do (println "hi there!")) (= 1 2) (= 3 4))
