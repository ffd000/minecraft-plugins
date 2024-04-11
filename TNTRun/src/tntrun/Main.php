<?php

namespace tntrun;

use tntrun\game\GameBase;
use tntrun\command\BaseCommand;
use tntrun\task\WaitTask;
use tntrun\task\GameTask;
use tntrun\game\GameSender;

use pocketmine\plugin\PluginBase;

class Main extends PluginBase {

    private $games = [];
    private $gameSender;

    public function onEnable() {
        $this->getLogger()->info("Stormcade TNTRun Enabled!");
        $this->initEvent();
        $this->initCommand();
        $this->initTask();
        $this->initGames();
        $this->gameSender = new GameSender($this);
    }

    public function initEvent() {
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);
    }

    public function initCommand() {
        $this->getCommand("tr")->setExecutor(new BaseCommand($this), $this);
    }

    public function initTask() {
        $this->getServer()->getScheduler()->scheduleRepeatingTask(new WaitTask($this), 20);
        $this->getServer()->getScheduler()->scheduleRepeatingTask(new GameTask($this), 20);
    }

    public function initGames() {
        $level = $this->getServer()->getDefaultLevel();
        $this->games = [
            new GameBase($this, "WAITING", 12, 5, 10, $level, $level, [128, 70, 128], [148, 70, 148]),
            new GameBase($this, "WAITING", 12, 30, 300, $level, $level, [128, 70, 128], [148, 70, 148]),
            new GameBase($this, "WAITING", 12, 30, 300, $level, $level, [128, 70, 128], [148, 70, 148])
        ];
    }

    public function getGames() {
        return $this->games;
    }

    public function getGameSender() {
        return $this->gameSender;
    }
}
