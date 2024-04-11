<?php

/*
*
*   ________       _       __      ____
*  /_  __/ /_  ___| |     / /___ _/ / /____
*   / / / __ \/ _ \ | /| / / __ `/ / / ___/
*  / / / / / /  __/ |/ |/ / /_/ / / (__  )
* /_/ /_/ /_/\___/|__/|__/\__,_/_/_/____/
*
* Handles TheWalls commands and subcommands
*
* @author hartleyterw
*/

namespace thewalls\commands;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\command\CommandExecutor;

use pocketmine\utils\TextFormat as T;

use pocketmine\level\Level;

use thewalls\TheWalls;

class WallsCommand implements CommandExecutor {
    /**
     * This is here just because i like having my own default constructors
     */
    public function __construct() {}

    /**
     * Perform actions when commands are executed
     * @param  CommandSender $sender  Command executor
     * @param  Command       $command Command being executed
     * @param  string        $label   Command label
     * @param  string[]      $args    Command arguments
     * @return boolean                Boolean is returned to make debugging
     *                                the method easier
     */
    public function onCommand(CommandSender $sender, Command $command, $label, array $args) {
        if($command->getName() === "walls") {
            if(isset($args[0])) {
                switch($args[0]) {
                    case "setup":
                        if(isset(TheWalls::$mode[$sender->getName()])) {
                            $sender->sendMessage(TheWalls::PREFIX . T::RED . " You are already in setup mode.");
                            return true;
                        }
                        if(isset($args[1])) {
                            if($level = TheWalls::getInstance()->getServer()->getLevelByName($args[1]) instanceof Level) {
                                # enter setup stage
                                TheWalls::$mode[$sender->getName()] = 1;
                                TheWalls::getInstance()->getServer()->loadLevel($args[1]);
                                $sender->teleport($level->getSafeSpawn());
                                $sender->sendMessage(TheWalls::PREFIX . T::AQUA . " You have entered TheWalls setup stage.");
                                $sender->sendMessage(TheWalls::PREFIX . T::AQUA . " Type /walls exitSetup to exit.");
                                $sender->sendMessage(TheWalls::PREFIX . T::LIGHT_PURPLE . " Tap a block to set spawnpoint for zone 1 for map {$args[1]}.");
                                return true;
                            } else {
                                $sender->sendMessage(TheWalls::PREFIX . T::RED . " The level you entered is invalid.");
                            }
                        }
                    break;
                    case "exitSetup":
                        unset(TheWalls::$mode[$sender->getName()]);
                        $sender->sendMessage(TheWalls::PREFIX . T::GREEN . " You have exited TheWalls setup stage.");
                        return true;
                    break;
                    case "debug":
                        var_dump(TheWalls::getDataFile());
                        $sender->sendMessage(TheWalls::PREFIX . T::GRAY . " Config debugged.");
                        return true;
                    break;
                    case "resetConfig":
                        TheWalls::getDataFile()->setAll("");
                        $sender->sendMessage(TheWalls::PREFIX . T::GREEN . " Config reset.");
                        return true;
                    break;
                    case "help":
                        $sender->sendMessage(T::BOLD . T::DARK_RED . "-----[" . T::RED . "TheWalls Admin Commands" . T::DARK_RED . "]-----");
                        $sender->sendMessage(T::GOLD . "-" . T::YELLOW . "/walls setup " . T::WHITE . ": Register positions for a new Walls game world.");
                        $sender->sendMessage(T::GOLD . "-" . T::YELLOW . "/walls debug " . T::WHITE . ": Debug the configuration.");
                        $sender->sendMessage(T::GOLD . "-" . T::YELLOW . "/walls resetConfig " . T::WHITE . ": Reset the configuration.");
                        $sender->sendMessage(T::GOLD . "-" . T::YELLOW . "/walls help " . T::WHITE . ": Display this page.");
                        $sender->sendMessage(T::BOLD . T::DARK_RED . "-----[" . T::RED . "TheWalls Admin Commands" . T::DARK_RED . "]-----");
                        return true;
                    break;
                }
            }
            $sender->sendMessage(TheWalls::PREFIX . T::RED . " Invalid arguments.");
            return true;
        }
    }
}
