<?php namespace sg;

/* SurvivalGames classes */
use sg\Main;

/* PocketMine classes */
use pocketmine\event\Listener;

class EventListener implements Listener {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    public function getPlugin() : Main {
        return $this->plugin;
    }
}
