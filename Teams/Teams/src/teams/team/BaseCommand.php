<?php

namespace teams\team;

use teams\Main;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\nbt\tag\IntTag;

class BaseCommand extends Command
{
    /** @var Main */
    protected $plugin;

    public function __construct(Main $plugin)
    {
        $this->plugin = $plugin;
        parent::__construct('base', '', '', []);
    }

    final public function execute(CommandSender $sender, string $commandLabel, array $args)
    {
        if ($sender->namedtag->hasTag("teamId", IntTag::class)) {
            $player->teleport($this->plugin->getServer()->getLevelByName(TeamManager::$baseWorldNames[$sender->namedtag->getInt("teamId")])->getSafeSpawn());
        } else {
            $sender->sendMessage("You are not in a team.");
        }
    }

    final public function getPlugin() :Main
    {
        return $this->plugin;
    }
}