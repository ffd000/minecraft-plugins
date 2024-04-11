<?php namespace bedwars;

/* Bedwars classes */
use bedwars\command\BedwarsCommand;
use bedwars\task\WaitTask;
use bedwars\task\GameTask;

/* PocketMine classes */
use pocketmine\plugin\PluginBase;

class Main extends PluginBase {

    private $games;

    public function onEnable() {
        $this->getLogger()->info("Stormcade Bedwars Enabled!");

        /* init events */
        $this->initEvent();
        /* init commands */
        $this->initCommand();
        /* init tasks */
        $this->initTask();
    }

    public function initEvent() {
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);
    }

    public function initCommand() {
        $this->getCommand("bw")->setExecutor(new BedwarsCommand($this), $this);
        $this->getCommand("bedwars")->setExecutor(new BedwarsCommand($this), $this);
    }

    public function initTask() {
        $this->getServer()->getScheduler()->scheduleRepeatingTask(new WaitTask($this), 20);
        $this->getServer()->getScheduler()->scheduleRepeatingTask(new GameTask($this), 20);
    }

    public function initGames() {
        $this->games = [
            /* small arena */
            new GameManager($this, [
                "id" => count($this->games) + 1,
                "state" => "STATE_WAITING",
                "slots" => 8,
                "team_slots" => 2,
                "players" => [],
                "red_team" => [],
                "green_team" => [],
                "blue_team" => [],
                "yellow_team" => [],
                "red_spawn" => [0, 0, 0],
                "green_spawn" => [0, 0, 0],
                "blue_spawn" => [0, 0, 0],
                "yellow_spawn" => [0, 0, 0],
                "wait_level" => "Wait-" . count($this->games) + 1,
                "arena_level" => "Arena-" . count($this->games) + 1,
                "wait_time" => 5,
                "game_time" => 20
            ]),
            /* medium arena */
            new GameManager($this, [
                "id" => count($this->games) + 1,
                "state" => "STATE_WAITING",
                "slots" => 12,
                "team_slots" => 3,
                "players" => [],
                "red_team" => [],
                "green_team" => [],
                "blue_team" => [],
                "yellow_team" => [],
                "red_spawn" => [0, 0, 0],
                "green_spawn" => [0, 0, 0],
                "blue_spawn" => [0, 0, 0],
                "yellow_spawn" => [0, 0, 0],
                "wait_level" => "Wait-" . count($this->games) + 1,
                "arena_level" => "Arena-" . count($this->games) + 1,
                "wait_time" => 30,
                "game_time" => 300
            ]),
            /* large arena */
            new GameManager($this, [
                "id" => count($this->games) + 1,
                "state" => "STATE_WAITING",
                "slots" => 16,
                "team_slots" => 4,
                "players" => [],
                "red_team" => [],
                "green_team" => [],
                "blue_team" => [],
                "yellow_team" => [],
                "red_spawn" => [0, 0, 0],
                "green_spawn" => [0, 0, 0],
                "blue_spawn" => [0, 0, 0],
                "yellow_spawn" => [0, 0, 0],
                "wait_level" => "Wait-" . count($this->games) + 1,
                "arena_level" => "Arena-" . count($this->games) + 1,
                "wait_time" => 30,
                "game_time" => 300
            ]),
            /* super arena */
            new GameManager($this, [
                "id" => count($this->games) + 1,
                "state" => "STATE_WAITING",
                "slots" => 20,
                "team_slots" => 5,
                "players" => [],
                "red_team" => [],
                "green_team" => [],
                "blue_team" => [],
                "yellow_team" => [],
                "red_spawn" => [0, 0, 0],
                "green_spawn" => [0, 0, 0],
                "blue_spawn" => [0, 0, 0],
                "yellow_spawn" => [0, 0, 0],
                "wait_level" => "Wait-" . count($this->games) + 1,
                "arena_level" => "Arena-" . count($this->games) + 1,
                "wait_time" => 30,
                "game_time" => 300
            ])
        ];
    }

    public function getGames() {
        return $this->games;
    }
}
