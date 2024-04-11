<?php

namespace tntrun\game;

use tntrun\Main;

class GameSender {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    public function getPlugin() {
        return $this->plugin;
    }

    public function getGame($id) {
        return $this->getPlugin()->getGames()[$id];
    }

    public function sendToGame($player) {
        for($i = 0; $i <= 3; $i++) {
            $game = $this->getGame($i);
            if($game->getMode() == "WAITING" && $game->countPlayers() < $game->getSlots()) {
                if(!$game->isInGame($player)) {
                    $game->addPlayer($player);
                    $player->sendMessage("Teleporting to waiting lobby {$game->getId()}!");
                    $level = $this->getPlugin()->getServer()->getLevelByName($game->getWaitLevel()["level"]);
                    $player->teleport(new Position($game->getWaitLevel()["spawn"][0], $game->getWaitLevel()["spawn"][1], $game->getWaitLevel()["spawn"][2], $level));
                } else {
                    $player->sendMessage("You are already in a game!");
                }
            } else {
                $player->sendMessage("No open games currently!");
            }
        }
    }
}
