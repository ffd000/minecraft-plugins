<?php namespace bedwars\game;

/* Bedwars classes */
use bedwars\Main;

class GameManager {

    private $plugin;
    private $data;

    public function __construct(Main $plugin, array $data) {
        $this->plugin = $plugin;
        $this->data = $data;
    }

    public function getId() {
        return $this->data["id"];
    }

    public function getState() {
        return $this->data["state"];
    }

    public function setState() {
        $this->data["state"] = $state;
    }

    public function getSlots() {
        return $this->data["slots"];
    }

    public function getTeamSlots() {
        return $this->data["team_slots"];
    }

    public function getPlayers() {
        return $this->data["players"];
    }

    public function checkPlayer($player) {
        return (in_array($player, $this->data["players"])) ? true : false;
    }

    public function addPlayer($player) {
        array_push($this->data["players"], $player);
    }

    public function removePlayer($player) {
        unset($this->data["players"][$player]);
    }
}
