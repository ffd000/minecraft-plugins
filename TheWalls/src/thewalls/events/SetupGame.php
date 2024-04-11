<?php

/*
*
*   ________       _       __      ____
*  /_  __/ /_  ___| |     / /___ _/ / /____
*   / / / __ \/ _ \ | /| / / __ `/ / / ___/
*  / / / / / /  __/ |/ |/ / /_/ / / (__  )
* /_/ /_/ /_/\___/|__/|__/\__,_/_/_/____/
*
* Handles TheWalls setup-related events
*
* @author hartleyterw
*/

namespace thewalls\events;

use pocketmine\event\Listener;
use pocketmine\event\player\PlayerInteractEvent;

use pocketmine\utils\TextFormat as T;

use pocketmine\tile\Sign;

use thewalls\TheWalls;

class SetupGame implements Listener {
    /** @var string[] */
    private $data;
    /**
     * This is here just because i like having my own default constructors
     */
    public function __construct() {}

    /**
     * Initialize data for and register a new TheWalls game
     * @param  PlayerInteractEvent $event Event the class is listening for
     * @return boolean                    Boolean is returned to make debugging
     *                                    the method easier
     */
    public function onInteract(PlayerInteractEvent $event) {
        if(!isset(TheWalls::$mode[$event->getPlayer()->getName()])) return false;
        # TODO: some very complex and smart setup code
        $player = $event->getPlayer();
        $name = $player->getName();
        $block = $event->getBlock();
        # player is in setup stage, start initializing map positions
        switch(TheWalls::$mode[$name]) {
            case 1:
                $this->data[$name]["zone1"]["x"] = $block->x;
                $this->data[$name]["zone1"]["y"] = $block->y;
                $this->data[$name]["zone1"]["z"] = $block->z;
                $player->sendMessage(TheWalls::PREFIX . T::LIGHT_PURPLE . " Tap a block to set spawnpoint for zone 2.");
            break;
            case 2:
                $this->data[$name]["zone2"]["x"] = $block->x;
                $this->data[$name]["zone2"]["y"] = $block->y;
                $this->data[$name]["zone2"]["z"] = $block->z;
                $player->sendMessage(TheWalls::PREFIX . T::LIGHT_PURPLE . " Tap a block to set spawnpoint for zone 3.");
            break;
            case 3:
                $this->data[$name]["zone3"]["x"] = $block->x;
                $this->data[$name]["zone3"]["y"] = $block->y;
                $this->data[$name]["zone3"]["z"] = $block->z;
                $player->sendMessage(TheWalls::PREFIX . T::LIGHT_PURPLE . " Tap a block to set spawnpoint for zone 4.");
            break;
            case 4:
                $this->data[$name]["zone4"]["x"] = $block->x;
                $this->data[$name]["zone4"]["y"] = $block->y;
                $this->data[$name]["zone4"]["z"] = $block->z;
                $player->sendMessage(TheWalls::PREFIX . T::LIGHT_PURPLE . " Tap a block to set center position for the map.");
            break;
            case 5:
                $this->data[$name]["center"]["x"] = $block->x;
                $this->data[$name]["center"]["y"] = $block->y;
                $this->data[$name]["center"]["z"] = $block->z;
                $player->sendMessage(TheWalls::PREFIX . T::GREEN . " Positions set!");
                $player->sendMessage(TheWalls::PREFIX . T::AQUA . " Tap a sign to register it for the arena.");
            break;
            case 6:
                if($tile = $player->getLevel()->getTile($block) instanceof Sign) {
                    $level = $player->getLevel();
                    $tile->setText(
                        TheWalls::PREFIX,
                        ":: JOIN ::",
                        "0/16",
                        $level->getFolderName()
                    );
                    $player->sendMessage(TheWalls::PREFIX . T::GREEN . " Sign has been registered.");
                    # add some other data to the level array
                    $data[$name]["gametime"] = 120;
                    $data[$name]["waittime"] = 120;
                    $data[$name]["players"] = [];
                    # save to config and return player to default level
                    $this->saveDataToConfig($level);
                    $player->sendMessage(TheWalls::PREFIX . T::BOLD . T::GREEN . " Setup complete for map " . $level->getFolderName() . "!");
                    $player->teleport(TheWalls::getInstance()->getServer()->getDefaultLevel()->getSafeSpawn());
                    return true;
                } else {
                    $player->sendMessage(TheWalls::PREFIX . T::RED . " Please tap a sign to complete setup.");
                    return;
                }
            break;
        }
        TheWalls::$mode[$name]++;
        return true;
    }

    /**
     * Save current setup data to config
     * @param  \pocketmine\level\Level $level Object to use as offset
     * @return void
     */
    private function saveDataToConfig($level) {
        $levelname = $level->getFolderName();
        $config = TheWalls::getDataFile();
        $data = $this->data[$name];
        unset($this->data[$name]);
        $config->set($levelname, $data);
        $config->save();
    }
}
