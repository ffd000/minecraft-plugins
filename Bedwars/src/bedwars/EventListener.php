<?php namespace bedwars;

/* Bedwars classes */
use bedwars\Main;

/* PocketMine classes */
use pocketmine\event\Listener;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\event\player\PlayerQuitEvent;

class EventListener implements Listener {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    public function onJoin(PlayerJoinEvent $event) {
        $event->setJoinMessage(null);
        $player = $event->getPlayer();
        $player->addTitle("Welcome to Stormcade Bedwars!");
    }
}
