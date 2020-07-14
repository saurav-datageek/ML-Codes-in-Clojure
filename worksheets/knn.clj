(ns knn
  (:require

            [clojure.data.fressian :as fress]
            [clojure.java.io :as io]

            [clojure.set :as set]
            [clojure.string :as str]))



(defn dot [v1 v2]
(reduce + (map * v1 v2)))

(defn magnitude [v]
(Math/sqrt (reduce + (map * v v))))


(defn cosine [v1 v2]
(/ (dot v1 v2) (* (magnitude v1) (magnitude v2))))


 (defn line->stype

  [line]
  (let [[a b c d e] (str/split line #"[,]")]
    {:sepal-length a
     :sepal-width b
     :petal-length c
     :petal-width d
     :iris e
     }))


(defn load-file

  []
  (if (.exists (io/file "resources/Iris.csv"))
    (->> (io/resource "Iris.csv")
         (io/reader)
         (line-seq)
         (map #(line->stype %)))
    nil))


;# Data Preparation #;

(def iris-data (drop 1 (load-file)))


#;(def hawa (reduce (fn [v m] (conj v {:name (:iris m) :value [(:sepal-length m)(:sepal-width m) (:sepal-width m) (:petal-length m)]}))
         #;[] iris-data ))

#;(cosine (mapv read-string (:value (first iris-data)))(mapv read-string (:value (second iris-data))))

;#Good one.
(def exp-iris-data (reduce (fn [v m] (conj v {:name (:iris m) :value  (mapv read-string [(:sepal-length m)(:sepal-width m) (:sepal-width m)
(:petal-length m)])}))
         [] iris-data ))

(def only-value (reduce (fn [v m] (conj v (:value m))) [] exp-iris-data  ))

(def cosine-values (mapv #(cosine [1 2 3 4] %) only-value))

(def max-values-with-index  (sort-by second > (map-indexed vector cosine-values)))

(def index-of-max-values (reduce (fn [v l] (conj v (first l))) [] (take 10 max-values-with-index))) ;#Getting the top values indices


(mapv exp-iris-data index-of-max-values) ;# To get the values from vector of maps using index

;;; Turn it into a classifier now.

(reduce (fn [v m] (conj v (:name m))) [] top-5)

(first (first (sort-by val >(frequencies final-5))))

;;; Depending on the value of n.





;;;
