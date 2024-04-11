<?php namespace sg\arena;

/* SurvivalGames classes */
use sg\Main;

/* PocketMine classes */
use pocketmine\level\Level;
use pocketmine\math\Vector3;
use pocketmine\Player;

class ArenaManager {

    private $plugin;
    private $data = [];

    public function __construct(Main $plugin, array $data) {
        $this->plugin = $plugin;
        $this->data = $data;
    }

    public function getId() : int {
        return $this->data["id"];
    }
    
    public function getState() : string {
        return $this->data["state"];
    }

    public function getMinSlots() {
        return $this->data["min_slots"]
    }

    public function getMaxSlots() : int {
        return $this->data["max_slots"];
    }

    public function getWaitLevel() : Level {
        return $this->data["wait_level"];
    }

    public function getArenaLevel() : Level {
        return $this->data["arena_level"];
    }

    public function getWaitSpawn() : array {
        return $this->data["wait_spawn"];
    }

    public function getArenaSpawn() : array {
        return $this->data["arena_spawn"]["spawn_" . count($this->data["players"])];
    }

    public function getDeathmatchSpawn() : array {
        return $this->data["deathmatch_spawn"];
    }

    public function getWaitTime() : int {
        return $this->data["wait_time"];
    }
    
    public function setWaitTime(int $time) {
        $this->data["wait_time"] = $time;
    }

    public function getArenaTime() : int {
        return $this->data["arena_time"];
    }
    
    public function setArenaTime(int $time) {
        $this->data["arena_time"] = $time;
    }

    public function getPlayers() : Player {
        return $this->data["players"];
    }

    public function isInArena(Player $player) : bool {
        return isset($player, $this->data["players"]) ? true : false;
    }

    public function addPlayer(Player $player) {
        array_push($this->data["players"], $player);
    }

    public function removePlayer(Player $player) {
        unset($this->data["players"][$player]);
    }
}
