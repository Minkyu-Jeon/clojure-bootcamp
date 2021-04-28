(ns concurrency)
(def the-answer (promise))

;; start working on the promise in a new thread
(doto (Thread. (fn []
                 ;;; do a lot of work
                 ;;; ...
                 ;;; then deliver on your promise
                 (deliver the-answer 42)))
  .start)

;; in the original thread, do your own work
;; ...
;; then get the answer
;; (this will block if it's not there)
(println (deref the-answer))