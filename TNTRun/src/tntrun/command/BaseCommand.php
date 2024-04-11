<?php

namespace tntrun\command;

use tntrun\Main;

use pocketmine\plugin\PluginBase;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\command\CommandExecutor;

class BaseCommand extends PluginBase implements CommandExecutor {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    public function getPlugin() {
        return $this->plugin;
    }

    public function sendUsage() {
        $messages = [
            "TNTRun Command Usage",
            "/tr join",
            "/tr leave",
            "/tr help"
        ];

        foreach($messages as $message) {
            return $message;
        }
    }

    public function onCommand(CommandSender $sender, Command $command, $label, array $args) {
        if($command->getName() === "tr") {
            if(isset($args[0])) {
                switch($args[0]) {
                    case "join":
                        $this->getPlugin()->getGameSender()->sendToGame($sender);
                    break;
                    case "leave":

                    break;
                    case "help":
                        $sender->sendMessage($this->sendUsage());
                    break;
                    case "debug":
                        var_dump($this->getPlugin()->getGames());
                    break;
                }
            } else {
                $sender->sendMessage($this->sendUsage());
                return false;
            }
        }
        return false;
    }
}
