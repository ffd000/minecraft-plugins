<?php

namespace tntrun\game;

use tntrun\Main;

use pocketmine\level\Level;
use pocketmine\level\Position;
use pocketmine\math\Vector3;

class GameBase {

    private $plugin, $id;
    private $players = [];
    private $mode;
    private $slots;
    private $waitLevel = [], $gameLevel = [];
    private $waitTime, $gameTime;

    public function __construct(Main $plugin, $mode, $slots, $waitTime, $gameTime, Level $waitLevel, Level $gameLevel, array $waitSpawn, array $gameSpawn) {
        $this->plugin = $plugin;
        $this->id = count($this->getPlugin()->getGames()) + 1;
        $this->mode = $mode;
        $this->slots = $slots;
        $this->waitTime = $waitTime;
        $this->gameTime = $gameTime;
        $this->waitLevel["level"] = $waitLevel->getFolderName();
        $this->waitLevel["spawn"] = $waitSpawn;
        $this->gameLevel["level"] = $gameLevel->getFolderName();
        $this->gameLevel["spawn"] = $gameSpawn;
    }

    public function getPlugin() {
        return $this->plugin;
    }

    public function getId() {
        return $this->id;
    }

    public function getPlayers() {
        return $this->players;
    }

    public function countPlayers() {
        return count($this->players);
    }

    public function isInGame($player) {
        return in_array($player, $this->players);
    }

    public function addPlayer($player) {
        array_push($this->players, $player);
    }

    public function removePlayer($player) {
        unset($this->players[$player]);
    }

    public function getMode() {
        return $this->mode;
    }

    public function setMode($mode) {
        $this->mode = $mode;
    }

    public function getSlots() {
        return $this->slots;
    }

    public function getWaitTime() {
        return $this->waitTime;
    }

    public function setWaitTime($waitTime) {
        $this->waitTime = $waitTime;
    }

    public function getGameTime() {
        return $this->gameTime;
    }

    public function setGameTime($gameTime) {
        $this->gameTime = $gameTime;
    }

    public function getWaitLevel() {
        return $this->waitLevel["level"];
    }

    public function getWaitSpawn() {
        return $this->waitLevel["spawn"];
    }

    public function getGameLevel() {
        return $this->gameLevel["level"];
    }

    public function getGameSpawn() {
        return $this->gameLevel["spawn"];
    }
}
