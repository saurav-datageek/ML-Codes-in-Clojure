
(list 'a 'b 'c)
-> (a b c)

(list (list 'george))
-> ((george))

(rest '((x1 x2) (y1 y2)))
-> ((y1 y2))

(list? (first '(a short list)))
-> false

(memq 'red '((red shoes) (blue socks)))
-> false


(memq 'red '(red shoes blue socks))
-> (red shoes blue socks)



(defn equal [list1 list2]

(cond
    (and (empty? list1) (empty? list2)) true
    (not= (first list1) (first list2)) false
    (= (first list1) (first list2)) (equal (rest list1) (rest list2))
)
)