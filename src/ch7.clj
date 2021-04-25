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

;;ing
