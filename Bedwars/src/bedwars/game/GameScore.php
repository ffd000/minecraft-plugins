<?php namespace bedwars\game;

class GameScore {
    
    private $game;
    private $score = 0;
    
    public function __construct(GameManager $game) {
        $this->game = $game;
    }
    
    public function getGame() {
        return $this->game;
    }
    
    public function getScore() {
        return $this->score;
    }
    
    public function setScore($name, $score) {
        foreach($this->getGame()->getPlayers() as $player) {
            $name = $player->getName();
            $this->score[$name] = $score;
        }
    }
}
