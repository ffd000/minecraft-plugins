<?php namespace bedwars\command;

/* Bedwars classes */
use bedwars\Main;

/* PocketMine classes */
use pocketmine\plugin\PluginBase;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\command\CommandExecutor;

class BedwarsCommand extends PluginBase implements CommandExecutor {

    private $plugin;

    public function __construct(Main $plugin) {
        $this->plugin = $plugin;
    }

    public function onCommand(CommandSender $sender, Command $command, $label, array $args) {
        if($command->getName() == "bw" or $command->getName() == "bedwars") {
            if(isset($args[0])) {
                switch($args[0]) {
                    case "join":
                        // TODO : send to open arena
                    break;
                    case "leave":
                        // TODO : remove player from queue before match starts
                    break;
                    case "help":
                        // TODO : send command usage
                    break;
                }
            } else {
                // TODO : send command usage
            }
        }
    }
}
