(ns snakesandladders.core
  (:gen-class))

(def sandl-map 
"Map of Snakes/Chutes and Ladders on the board"
  {4  14
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
  98  78})
  
(def game
  {:turn 0 :players [] :spaces 100 :board {}})
 
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

(defn print-interface
"Output the current game state"
  [game]
  (println (str "\nTurn: " (:turn game)))
  (doseq [p (:players game)] (println (str "Player " (:name p) " is on space " (:space p))))  
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
		 (cond
		   (< ((:board game) space space) space) (println (str "You landed on a snake, back to " ((:board game) space)))
			(> ((:board game) space space) space) (println (str "You landed on a ladder, ascend to " ((:board game) space))))
		 (assoc-in game [:players player-id :space] ((:board game) space space))))

(defn check-overshoot
"Count back from 100"
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
   (println (str "You rolled a " roll "!"))
	(update-in game [:players player-id :space] + roll)))
		  
(defn new-game
"Create a new game with x players"
  [x]
  (-> game
    (assoc :board sandl-map)
    (assoc :players (into [] (map #( assoc-in player [:name] % ) (take x (iterate inc 1)))))))

(defn player-turn
"Do a player turn and then do another one if the game hasn't finished"
  [game]
  (print-interface game)
  (let [user-input (prompt (#(str "\nPlayer " % " hit a key to roll, or type 'exit' to quit.") (:name ((:players game) (current-player game)))))]
    (if (= user-input "exit")
      :exit
      (-> game
          move-player
          check-overshoot
          check-map
          (update-in [:turn] inc)
          check-win))))

			 
;; game-loop :players 2
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
