<?php namespace sg;

/* SurvivalGames classes */
use sg\arena\ArenaManager;

/* PocketMine classes */
use pocketmine\plugin\PluginBase;
use pocketmine\math\Vector3;

class Main extends PluginBase {

    private $arenas = [];
    
    public function onEnable() {
        $this->getLogger()->info("Stormcade SurvivalGames Enabled!");

        $this->initEvent();
    }

    public function initEvent() {
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);
    }
    
    public function initArenas() {
        $this->arenas = [
            new ArenaManager($this, [
                "id" => (count($this->arenas) + 1),
                "state" => "WAITING",
                "min_slots" => 2,
                "max_slots" => 12,
                "wait_level" => $this->getServer()->getLevelByName("Wait_" . (count($this->arenas) + 1)),
                "arena_level" => $this->getServer()->getLevelByName("Arena_" . (count($this->arenas) + 1)),
                "wait_spawn" => [0, 0, 0],
                "arena_spawn" => [
                    "spawn_1" => [0, 0, 0],
                    "spawn_2" => [0, 0, 0],
                    "spawn_3" => [0, 0, 0],
                    "spawn_4" => [0, 0, 0],
                    "spawn_5" => [0, 0, 0],
                    "spawn_6" => [0, 0, 0],
                    "spawn_7" => [0, 0, 0],
                    "spawn_8" => [0, 0, 0],
                    "spawn_9" => [0, 0, 0],
                    "spawn_10" => [0, 0, 0],
                    "spawn_11" => [0, 0, 0],
                    "spawn_12" => [0, 0, 0]
                ],
                "deathmatch_spawn" => [0, 0, 0],
                "wait_time" => 30,
                "arena_time" => 300,
            ])
        ];
    }
}
