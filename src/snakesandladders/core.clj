(ns snakesandladders.core
  (:gen-class))
  
(def game
"Default game map including spaces on the board and map of snakes and ladders"
  {:turn 0 
   :players [] 
	:spaces 100 
	:sandl {4  14
          9  31
          16  6
          21  42
          20  38
          28  84
          36  44
          47  26
          49  11
          51  67
          56  53
          62  19
          64  60
          71  91
          80  100
          87  24
          93  73
          95  75
          98  78}})
 
(def player
  {:name "" :space 0})

(defn prompt [msg]
  (println msg)
  (read-line))

;;(defn dice-roll [] (+ (rand-int 6) 1))

(defn current-player
"Returns the current player based on the turn and # players"
  [game]
  (rem (:turn game) (count (:players game))))

(defn snakeorladder?
"Check the map for the head of a snake :snake or the bottom of a ladder :ladder or returns :none"
  [sandl-map space]
  (cond
    (> (sandl-map space space) space) :ladder
    (< (sandl-map space space) space) :snake
	 :else :none))

(defn print-interface
"Output the current game state"
  [game]
  (doseq [p (:players game)] (println (str (:name p) " is on space " (:space p)))) 
  game
)

(defn check-win
"Check for a winner"
  [game]
  (let [winner (first (filter #(= (:spaces game) (:space %)) (:players game)))]
    (if winner
	   (do
		  (println (str "Player " (:name winner) " Wins!"))
		  :exit)
		game)))

(defn check-map
"Check for snakes and ladders"
  [game]
  (let [player-id (current-player game)
       space (get-in game [:players player-id :space])]
		 (println (str "Moving to space " space))
		 (cond
		   (= (snakeorladder? (:sandl game) space) :snake) (println (str "You landed on a snake, go back to " ((:sandl game) space) " :("))
			(= (snakeorladder? (:sandl game) space) :ladder) (println (str "You landed on a ladder, climb up to " ((:sandl game) space) " :)")))
		 (assoc-in game [:players player-id :space] ((:sandl game) space space))))

(defn check-overshoot
"Count back from end space"
  [game]
  (let [space (get-in game [:players (current-player game) :space])]
    (if (> space (:spaces game))
	    (do 
		   (println (str "You overshot the target by " (- space (:spaces game)) " spaces."))
		   (update-in game [:players (current-player game) :space] + (-> (:spaces game) (- space) (* 2))))
		 game)))
  
(defn move-player
"Function to roll & move"
  [game]
  (let [roll (+ (rand-int 6) 1)
       player-id (current-player game)]
   (println (str (get-in game [:players player-id :name]) " rolled a " roll "!"))
	(update-in game [:players player-id :space] + roll)))
		  
(defn new-game
"Create a new game with [x] 1-6 players."
;; TODO get player names
  [x]
  {:pre [(integer? x)(>= x 1)(<= x 6)]}
  (assoc game :players (into [] (map #( assoc-in player [:name] % ) (map #(inc %) (range 3))))))

(defn player-prompt
"Prompt for user input"
  [game]
  (let [plyr (get-in game [:players (current-player game)])]
    (str 
      "\n" (:name plyr) "'s turn"
      "\n----------"
		"\nYou are on space " (:space plyr)
		"\nComing up in the next 6 spaces: "
      (apply str 
        (map #(
          let [sorl (snakeorladder? (:sandl game) %)]
          (cond 
            (= sorl :snake) (str % " Snake ")
            (= sorl :ladder) (str % " Ladder ")
				:else (str % " x ")))
          (range (inc (:space plyr)) (min (+ (:space plyr) 7) 100))))
		"\nHit enter to roll or type 'exit' to quit.")))
 
(defn player-turn
"Prompt player to roll the dice and move their piece"
  [game]
  (let [user-input (prompt (player-prompt game))]
    (if (= user-input "exit")
      :exit
      (-> game
          move-player
          check-overshoot
          check-map
          check-win
			 print-interface
          (update-in [:turn] inc)))))
			 
;; game-loop default :players 2
(defn game-loop
"Start a new game with default 2 players"
  [& {:keys [players]
      :or {players 2}}]
  (loop [game (new-game players)]
    (cond (= game :exit) (println "Thanks for playing, Goodbye!")
	       :else         (recur (player-turn game)))))
  
(defn -main
  [& args]
  (apply game-loop (map read-string args)))
