<?php

namespace tntrun\task;

use tntrun\Main;

use pocketmine\scheduler\PluginTask;
use pocketmine\scheduler\RepeatingTask;

class WaitTask extends PluginTask {

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
            $waitTime = $game->getWaitTime();
            $players = $game->getPlayers();
            foreach($players as $player) {
                if($game->getMode() == "WAITING") {
                    if($waitTime > 0) {
                        $player->sendTip("Game starting in: {$waitTime}");
                        $waitTime--;
                        $game->setWaitTime($waitTime);
                    } else {
                        $player->addTitle("Game started!");
                        $game->setMode("INGAME");
                        $this->getPlugin()->getServer()->getScheduler()->cancelTask($this->getTaskId());
                    }
                }
            }
        }
    }
}
