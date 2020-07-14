;; gorilla-repl.fileformat = 1

;; @@

(ns blackjack-saurav
  (:require [gorilla-plot.core :as plot]))


;;; Functions to get a card and a hand

(defn deal []
  (inc (rand-int 10)))

(defn new-hand [] [(deal)])


;;; To get a card use (deal). To get a hand use (new-hand).


;;; Three functions to get the first card, add card, and sum the total.

(defn up-card [hand]
  (first hand))


(defn add-card [hand card]
  (conj hand card))


(defn total [hand]
  (reduce + hand))


(up-card [1 2 3])
(add-card [1 2 3 ] 10)
(total [1 2 3])


;;; Let's create two stop-at-17 functons. One that returns a boolean and the one that returns a hand less than 17. We will call the one that returns boolean stop-at-17 and the one that returns a hand as stop-at-17_.



(defn stop-at-17 [hand opponent-up-card]
   (if (< (total hand) 17) true false)
  )



(defn stop-at-17_ [hand]
  (cond (> (total hand) 17)
        (do
          (prn hand)
          (drop-last hand)
          )

        :else (into [] (stop-at-17_ (add-card hand (deal))))
        ))


(stop-at-17 (new-hand) (up-card (new-hand)))


(stop-at-17_ (new-hand))


;;; Let's make stop at n now. We will again create two stop at n functions.


;;; stop-at-n will return a procedure (function) that in turn returns a boolean. Whereas, stop-at-n_ will return a hand with total less than n.


(defn stop-at-n [n]

 (fn [hand opponent-up-card] (if (< (total hand) n) true false))
  )


(defn stop-at-n_ [n]

  (def atom-hand (atom (new-hand)))



  (while (< (total @atom-hand) n)



      (do (swap! atom-hand (fn [x]  (add-card x (deal))))
         (prn @atom-hand))


  )

  (into [](drop-last @atom-hand))
  )


(stop-at-n 21)


((stop-at-n 21) (new-hand) (up-card(new-hand)))


(stop-at-n_ 21)


;;; Now time for strategies. We will make main two strategies : one smart strategy and one stupid strategy.

;;; In the smart strategy that I have created, if the opponent card is less than 6, it stops at 15. Otherwise, it will stop at 17.

;;; Logic for stopping at 15 if the opponent card is less than 6 : The maximum number the opponent can add at a time is 11 points. And normally in average it would be less than 7 points. So, unless the opponent picks 10 or 11, they can never catch up in one card addition only. So 15 is still a safe mark. Whereas, if the opponent has a card more than 6, then we need to take a bit more risk and go till 17.

;;; I have also created 3 other dummies strategies to be used later for another test.


;;; ### Main Strategies ###


(defn stupid-strategy [hand upcard-house]
  (> upcard-house 5))

;;; Our main strategy. Stop at 15 if the opponents up card is less than 6. Else, stop at 17.

(defn smart-strategy [hand upcard-house]
  (if (< upcard-house 6) ((stop-at-n 15) hand upcard-house)
    ((stop-at-n 17) hand upcard-house)
  ))



;;;  ### Dummy Strategies ###


(defn player-strategy-one [hand upcard-house]

  (if  (and (< upcard-house 6) (> (total hand) 17))

    true false

    )
  )


(defn player-strategy-two [hand upcard-house]

  (if  (and (< upcard-house 10) (> (total hand) 20))

    true false

    )
  )

(defn player-strategy-three [hand upcard-house]

  (if  (and (< upcard-house 8) (> (total hand) 16))

    true false

    )
  )


(prn (player-strategy-one [1 3 5 10] 10))
(prn (player-strategy-two [1 3 5 10] 10))
(prn (player-strategy-three [1 3 5 10] 10))
(prn (smart-strategy [1 3 5 10] 10))
(prn (stupid-strategy [1 3 5 10] 10))


;;; play-hand function


(defn play-hand [strategy hand opponent-up-card]
  (cond (> (total hand) 21)
        hand

        (strategy hand opponent-up-card)
        (recur strategy
               (add-card hand (deal))
               opponent-up-card)

        :else
        hand))


(play-hand smart-strategy (new-hand) (up-card (new-hand)))


;;; play-game function


(defn play-game [player-strategy house-strategy]
  (let [house-initial-hand (new-hand)
        player-hand (play-hand player-strategy
                               (new-hand)
                               (up-card house-initial-hand))]
    (if (> (total player-hand) 21)
      0 ; Player bust
      (let [house-hand (play-hand house-strategy
                                  house-initial-hand
                                  (up-card player-hand))]
        (cond (> (total house-hand) 21)
              1 ; House bust

              (> (total player-hand) (total house-hand))
              1 ; House lost

              :else
              0 ; Player lost
              )))))




(play-game stop-at-17 stop-at-17)



;;; Majority function takes a list of strategies and returns a true or false depending on the majority. In case of a tie, instead of returning a boolean, it calculates the total of the hand, and if it is less than 15, it adds one more card and returns that.

;;; For taking a majority, let us first create a function that calculates the mode.


(defn most-frequent-n [n items]
  (->> items
    frequencies
    (sort-by val)
    reverse
    (take n)
    (map first)))


; ### majority ###


(defn majority [List-of-Strategies hand up-card]

  ; It returns true or false depending upon what is the majority.
  ; In case of a tie, instead of returning a boolean, it calculates the total of the hand, and if it is less than 15, it adds one more card and returns that.

         (def top-two (sort-by val >
                         (frequencies
                           (map #(% hand up-card) List-of-Strategies)
  ))
  )


  (if (= (second (first top-two))(second (second top-two)) )

    (if (< (total hand) 15)

      (add-card hand (deal))

      hand

      )
    (first (first top-two))
     ))


(def list-of-strag [player-strategy-one player-strategy-two player-strategy-three smart-strategy stupid-strategy])


(majority list-of-strag [1 3 5 10] 5)

(defn test-strategy
  ([player-strategy house-strategy]
     (test-strategy player-strategy house-strategy 100))
  ([player-strategy house-strategy n]
        (float (/ (total (repeatedly n #(play-game player-strategy house-strategy))) n))
     ))


(test-strategy stop-at-17 stop-at-17 100000)


;; @@
;; ->
;;; [3 3 3 5 3 5]
;;; [1 8]
;;; [1 8 5]
;;; [1 8 5 5]
;;; [1 8 5 5 10]
;;; false
;;; false
;;; false
;;; false
;;; true
;;; 
;; <-
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>0.40929</span>","value":"0.40929"}
;; <=

;; @@

;; @@
