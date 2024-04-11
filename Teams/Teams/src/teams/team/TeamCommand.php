<?php

namespace teams\team;

use teams\Main;
use pocketmine\Player;
use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\nbt\tag\IntTag;

class TeamCommand extends Command
{
    /** @var Main */
    protected $plugin;

    private $teams = [
        "red" => Team::TEAM_RED,
        "yellow" => Team::TEAM_YELLOW,
    ];

    public function __construct(Main $plugin)
	{
		$this->plugin = $plugin;
		parent::__construct('team', '', '/team <red|yellow|quit>', []);
	}

    final public function execute(CommandSender $sender, string $commandLabel, array $args)
    {
        if (empty($args)) {
            $sender->sendMessage("Invalid arguments.");
            $sender->sendMessage($this->getUsage());

            return;
        }

        switch ($args[0]) {
            case 'quit':
                if (TeamManager::exit($sender) !== false) {
                    $sender->sendMessage("You left your team.");

                    return;
                }
                $sender->sendMessage("You are not a member of any team.");

                return;
            case 'debug':
                $sender->namedtag->removeTag('teamId');
                $sender->namedtag->removeTag('timesSwitchedTeams');

                return;
        }

        if (isset($this->teams[strtolower($args[0])])) {
            if ($sender->namedtag->hasTag("teamId", IntTag::class) && ($sender->namedtag->getInt("teamId") === $this->teams[strtolower($args[0])])) {
                $sender->sendMessage("You are already in this team.");

                return;
            }

            $teamId = $this->teams[strtolower($args[0])];
            if (TeamManager::checkCanSwitchTeam($sender)) {
                if (($team = TeamManager::joinTeam($teamId, $sender, true)) !== false) {
                    $sender->teleport($this->plugin->getServer()->getLevelByName(TeamManager::$baseWorldNames[$team->getType()])->getSafeSpawn());
                    $sender->sendMessage($team->getColor() . "Joined " . $team->getName());

                    return;
                }
                $sender->sendMessage("The " . strtolower($args[0]) . " team is full.");
            } else {
                $sender->sendMessage("You can only switch teams once.");
            }
        } else {
            $sender->sendMessage("Invalid arguments.");
            $sender->sendMessage($this->getUsage());
        }
    }

    final public function getPlugin() :Main
	{
		return $this->plugin;
	}
}
