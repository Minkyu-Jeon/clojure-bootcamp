(ns ch8)

;; 8.1
(eval 42)
(eval '(list 1 2))
; (eval (list 1 2)) ERROR! (1 2)와 같음


(eval (list (symbol "+") 1 2))
(eval '(list (symbol "+") 1 2))

;; 8.1.1

(defn contextual-eval [ctx expr]
  (eval
    `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
       ~expr)))

(contextual-eval '{a 1 b 2} '(+ a b))
(contextual-eval '{a 1 b 2} '(let [b 1000] (+ a b)))

;; 8.2 제어 구조 정의
;; 8.2.1

(defmacro do-until [& clauses]
  (when clauses
    (list 'clojure.core/when (first clauses)
          (if (next clauses)
            (second clauses)
            (throw (IllegalArgumentException.
                     "do-until requires an even number of forms")))
          (cons 'do-until (nnext clauses)))))

(do-until
  (even? 2) (println "Even")
  (odd? 3) (println "Odd")
  (zero? 1) (println "You never see me!")
  :hello (println "world"))

(macroexpand-1 '(do-until true (prn 1) false (prn 2)))
(require '[clojure.walk :as walk])
(walk/macroexpand-all '(do-until true (prn 1) false (prn 2)))

;; 8.2.2
(defmacro unless
  [condition & body]
  `(if (not ~condition)
     (do ~@body)))


(unless (even? 3) "Now we see it!!")
(unless (even? 2) "You don't see it...")

;; 8.3 구문 결합 매크로
;; add-watch 함수 출
;; 1. var 정의
;; 2. 관찰자가 될 함수 정의
;; 3. 값을 입력하여 add-watch 호

(defmacro def-watched [name & value]
  `(do
     (def ~name ~@value)
     (add-watch (var ~name)
                :re-bind
                (fn [~'key ~'r old# new#]
                  (println old# " -> " new#)))))

(def-watched x (* 12 13))
(def x 1)
(def x 99)

;; 8.4 매크로를 사용하여 구문 변경하기
;{:tag <node form>
; :attrs {}
; :content [<nodes>]}

(defmacro domain [name & body]
  `{:tag     :domain
    :attrs   {:name (str '~name)}
    :content [~@body]})

(declare handle-things)
(defmacro grouping [name & body]
  `{:tag     :grouping
    :attrs   {:name (str '~name)}
    :content [~@(handle-things body)]})

(declare grok-attrs grok-props)

(defn handle-things [things]
  (for [t things]
    {:tag     :thing
     :attrs   (grok-attrs (take-while (comp not vector?) t))
     :content (if-let [c (grok-props (drop-while (comp not vector?) t))]
                [c]
                [])}))

(defn grok-attrs [attrs]
  (into {:name (str (first attrs))}
        (for [a (rest attrs)]
          (cond
            (list? a) [:isa (str (second a))]
            (string? a) [:comment a]))))

(defn grok-props [props]
  (when props
    {:tag     :properties
     :attrs   nil
     :content (apply vector (for [p props]
                              {:tag     :property
                               :attrs   {:name (str (first p))}
                               :content nil}))}))

(def d
  (domain ios-vs-android
          (grouping ios
                    (IOS "A loyal follower of apple")
                    (Man (isa IOS)
                         "A man, baby"
                         [name]
                         [has-beard?]))
          (grouping android
                    (android "One who loves all"
                             [spends-money?]))))

(:tag d)
(:tag (first (:content d)))

(use '[clojure.xml :as xml])
(xml/emit d)


;; 8.5 매크로로 심벌릭 레졸루션 타임 제어하기
(defmacro resolution [] `x)
(macroexpand '(resolution))                                 ;ch8/x

(def z 9)
(let [z 100] (resolution))

;; 8.5.1 anaphora와 ~'의 사용
(defmacro awhen [expr & body]
  `(let [~'it ~expr]
     (if ~'it
       (do ~@body))))

(awhen [1 2 3 4] (it 3))
(awhen nil (println "hello, world!"))

;; ~'은 symbol이 특정 namespace에 바인딩되는것을 막아준다.

;; 8.5.2 선택정 네임 캡쳐
;; 8.6 매크로로 리소스 관리하기
(defmacro with-resource [binding close-fn & body]
  `(let ~binding
     (try
       (do ~@body)
       (finally
         (~close-fn ~(binding 0))))))

;(let [stream (joc-www)]
;  (with-stream [page stream]
;               #(.close %)
;               (.readLine page)))

;; 8.7 종합하기: 함수를 리턴하는 매크로

; HOW?
;(contract doubler
;          [x]
;          (:require (pos? x))
;          (:ensure (= (* 2 x) %)))

(declare build-contract)

(defn collect-bodies [forms]
  (for [form (partition 3 forms)]
    (build-contract form)))

(defmacro contract [name & forms]
  (list* `fn name (collect-bodies forms)))

(defn build-contract [c]
  (let [args (first c)]
    (list
      (into '[f] args)
      (apply merge
             (for [con (rest c)]
               (cond (= (first con) 'require)
                     (assoc {} :pre (vec (rest con)))
                     (= (first con) 'ensure)
                     (assoc {} :post (vec (rest con)))
                     :else (throw (Exception.
                                    (str "Unknown tag "
                                         (first con)))))))
      (list * 'f args))))

(def doubler-contract
  (contract doubler
            [x]
     (require (pos? x))
     (ensure (= (* 2 x) %))))

(def times2 (partial doubler-contract #(* 2 %)))
(times2 10)

(def times3 (partial doubler-contract #(* 3 %)))
(times3 9)