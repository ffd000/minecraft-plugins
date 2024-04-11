<?php

/*
*
*   ________       _       __      ____
*  /_  __/ /_  ___| |     / /___ _/ / /____
*   / / / __ \/ _ \ | /| / / __ `/ / / ___/
*  / / / / / /  __/ |/ |/ / /_/ / / (__  )
* /_/ /_/ /_/\___/|__/|__/\__,_/_/_/____/
*
* Class calls important functions on plugin load and
* contains key TheWalls methods
*
* @author hartleyterw
*/

namespace thewalls;

use pocketmine\plugin\PluginBase;

use pocketmine\utils\TextFormat as T;
use pocketmine\utils\Config;

use thewalls\events\SetupGame;
use thewalls\events\JoinGame;

use thewalls\commands\WallsCommand;

use thewalls\tasks\RefreshSign;

class TheWalls extends PluginBase {
    /** @var string */
    const PREFIX = T::BOLD . T::DARK_GRAY . "[" . T::GOLD . "The" . T::YELLOW . "Walls" . T::DARK_GRAY . "]" . T::RESET . T::WHITE;
    /** @var array */
    public static $mode;
    /** @var TheWalls */
    private static $instance;

    /**
     * Do important things when plugin loads
     * @return void
     */
    public function onEnable() {
        self::$instance = $this;
        $this->initConfiguration();
        $this->registerCommands();
        $this->registerEvents();
        $this->getLogger()->info(T::GREEN . "Everything loaded.");
    }

    /**
     * Register plugin command executor files
     * @return void
     */
    private function registerCommands() {
        $this->getCommand("walls")->setExecutor(new WallsCommand());
    }

    /**
     * Register plugin event files
     * @return void
     */
    private function registerEvents() {
        $this->getServer()->getPluginManager()->registerEvents(new SetupGame(), $this);
        $this->getServer()->getPluginManager()->registerEvents(new JoinGame(), $this);
    }

    /**
     * Register tasks
     * @return void
     */
    private function registerTasks() {
        $this->getServer()->getScheduler()->scheduleRepeatingTask(new SignRefresh($this), 1);
    }

    /**
     * Initialize configurations and plugin folders
     * @return void
     */
    private function initConfiguration() {
        @mkdir($this->getDataFolder());
        @mkdir($this->getDataFolder() . "players");
        if(!file_exists($this->getDataFolder() . "matchdata.json"))
            $data = new Config($this->getDataFolder() . "matchdata.json", Config::JSON);
    }

    /// \/ TODO: make another class for these methods \/ ///

    /**
     * If any, finds an empty zone for the given map
     * @param  string $map      Config offset
     * @return mixed
     */
    public static function findEmptyZone(string $map) {
        $config = self::getDataFile();
        $data = $config->get($map);
        $full = [];
        for($i=1; $i <= 4; $i++) {
            $c = 1;
            for($v=0; $v < self::MAX_PLAYERS_PER_ZONE; $v++) {
                if(isset($data["zone" . $i]["players"][$v])) $c++;
            }
            if($c === self::MAX_PLAYERS_PER_ZONE) $full[$v] = true;
        }
        foreach($full as $key => $data) {
            if($data === false) return $key;
        }
        return false;
    }

    /**
     * Return the file where plugin data is stored in
     * @return Config       Plugin data file
     */
    public static function getDataFile() : Config {
        return new Config(self::getInstance()->getDataFolder() . "matchdata.json", Config::JSON);
    }

    /**
     * Return an instance of the main (this) class
     * @return TheWalls     Instance of this class
     */
    public static function getInstance() : TheWalls {
        return self::$instance;
    }
}
