<?php

namespace BuildBattle;

use pocketmine\plugin\PluginBase;

use pocketmine\utils\Config;

use BuildBattle\commands\BBCommand;

use BuildBattle\events\CreateBuildZones;
use BuildBattle\events\JoinSign;

use BuildBattle\tasks\LobbyTimerTask;
use BuildBattle\tasks\GameTimerTask;

class Main extends PluginBase {

    public $prefix = "§8[ §bBuild§3Battle §8]";
    public $mode = 0;
    public $arenas = [];

    public $currentArena;
    public $currentLobby;

    // TODO public $themes = ["house", "rainbow", "redstone", "village", "lake"];

    public function onEnable() {
        $this->getLogger()->info("§b[BuildBattle] Loading...");
        $this->initializeConfig();
        $this->loadArenas();
        $this->registerEvents();
        $this->registerCommands();
        $this->getLogger()->info("§a[BuildBattle] Everything loaded.");
    }

    private function initializeConfig() {
        @mkdir($this->getDataFolder());
            if(!file_exists($this->getDataFolder() . "config.yml")) {
                $config = new Config($this->getDataFolder() . "config.yml", Config::YAML, [
                    "gameTime" => 120,
                    "waitTime" => 60,
                    "loadArenasOnServerStartup" => true,
                    "playersPerGame" => 16
                ]);
            }
        if(!file_exists($this->getDataFolder() . "temp_match_data.json"))
            $temp = new Config($this->getDataFolder() . "temp_match_data.json", Config::JSON);
    }

    private function loadArenas() {
        $config = new Config($this->getDataFolder() . "arenas.json", Config::JSON);
        $arenas = $config->get("arenas");
        if($arenas !== null && !empty($arenas)) {
            foreach($arenas[0] as $arena => $data) {
                $this->getServer()->loadLevel($arena);
                $lobby = $arenas[0][$arena]["waitroomworld"];
                $this->getServer()->loadLevel($lobby);
                $this->getLogger()->info("Worlds loaded.");
            }
        }
    }

    private function registerEvents() {
        $this->getServer()->getPluginManager()->registerEvents(new CreateBuildZones($this), $this);
        $this->getServer()->getPluginManager()->registerEvents(new JoinSign($this), $this);
    }

    private function registerCommands() {
        $this->getCommand("bb")->setExecutor(new BBCommand($this), $this);
    }
}
