<?php

/*
*
*   ________       _       __      ____
*  /_  __/ /_  ___| |     / /___ _/ / /____
*   / / / __ \/ _ \ | /| / / __ `/ / / ___/
*  / / / / / /  __/ |/ |/ / /_/ / / (__  )
* /_/ /_/ /_/\___/|__/|__/\__,_/_/_/____/
*
* Lets players use game signs
*
* @author hartleyterw
*/

namespace thewalls\events;

use pocketmine\event\Listener;
use pocketmine\event\player\PlayerInteractEvent;

use pocketmine\utils\TextFormat as T;

use pocketmine\tiles\Sign;

use pocketmine\level\Position;

use thewalls\TheWalls;

class JoinGame implements Listener {
    /**
     * This is here just because i like having my own default constructors
     */
    public function __construct() {}

    /**
     * Send player to a game upon clicking a sign
     * @param  PlayerInteractEvent $event Event the class is listening for
     * @return boolean                    Boolean is returned to make debugging
     *                                    the method easier
     */
    public function onInteract(PlayerInteractEvent $event) {
        $player = $event->getPlayer();
        $block = $event->getBlock();
        if($tile = $player->getLevel()->getTile($block) instanceof Sign) {
            $text = $tile->getText();
            if($text[0] !== TheWalls::PREFIX) return false;
            # handle all cases
            if(strpos(strtolower($text[1]), "full")) {
                $sender->sendMessage(TheWalls::PREFIX . T::RED . " This game is currently full.");
                return false;
            } elseif(strpos(strtolower($text[1]), "ingame")) {
                $sender->sendMessage(TheWalls::PREFIX . T::RED . " This game has already started.");
                return false;
            } elseif(strpos(strtolower($text[1]), "offline")) {
                $sender->sendMessage(TheWalls::PREFIX . T::RED . " This game is currently offline.");
                return false;
            } elseif(strpos(strtolower($text[1]), "join")) {
                $sender->sendMessage(TheWalls::PREFIX . T::AQUA . " Sending to game on map {$text[3]}...");
                $leveldata = TheWalls::getDataFile()->get($text[3]);
                $level = TheWalls::getInstance()->getServer()->getLevelByName($text[3]);
                if($zone = TheWalls::findEmptyZone($text[3]) !== false) {
                    $zone = "zone" . $zone;
                    $player->teleport(new Position($zone["x"], $zone["y"], $zone["z"], $level));
                    return true;
                } else {
                    $sender->sendMessage(TheWalls::PREFIX . T::RED . " Something went wrong.");
                    return false;
                }
                return false;
            }
            $sender->sendMessage(TheWalls::PREFIX . T::RED . " This sign is invalid.");
        }
        return false;
    }
}
