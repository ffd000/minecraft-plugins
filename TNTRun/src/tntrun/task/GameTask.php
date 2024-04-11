<?php

namespace tntrun\task;

use tntrun\Main;

use pocketmine\scheduler\PluginTask;
use pocketmine\scheduler\RepeatingTask;

class GameTask extends PluginTask {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
        parent::__construct($plugin);
    }

    public function getPlugin() {
        return $this->plugin;
    }

    public function onRun($tick) {
        for($i = 0; $i < 3; $i++) {
            $game = $this->getPlugin()->getGames()[$i];
            $gameTime = $game->getGameTime();
            $players = $game->getPlayers();
            foreach($players as $player) {
                if($game->getMode() == "INGAME") {
                    if($gameTime > 0) {
                        $player->sendTip("Game ending in: {$gameTime}");
                        $gameTime--;
                        $game->setGameTime($gameTime);
                    } else {
                        $player->addTitle("Game has ended!");
                        $game->setMode("WAITING");
                        $this->getPlugin()->getServer()->getScheduler()->cancelTask($this->getTaskId());
                    }
                }
            }
        }
    }
}
