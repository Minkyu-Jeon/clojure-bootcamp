(ns macro_exercise2)

(defmacro inspect-caller-locals []
  (let [temp (->> (keys &env)
                  (mapv (fn [k] `'~k)))]
     (clojure.pprint/pprint &env)))

(let [foo "bar" baz "quux"]
  (inspect-caller-locals))

(def temp1 100)
(->> (keys {"foo" "bar" "hello" "world"})
     (map (fn [k] [`'~k k]))
     (into {}))

(let [foo "bar"]
  (list 'quote "bar"))
