<?php

namespace teams;

use teams\team\{TeamManager, TeamCommand};
use pocketmine\utils\TextFormat;
use pocketmine\plugin\PluginBase;

class Main extends PluginBase
{
    public function onLoad()
    {
        TeamManager::initTeams();
    }

	public function onEnable()
	{
		$this->getServer()->getPluginManager()->registerEvents(new EventHandler($this), $this);

        $this->getServer()->getCommandMap()->registerAll('team', [new TeamCommand($this)]);

        $this->getLogger()->info(TextFormat::GREEN . "Enabled successfully.");

        foreach (TeamManager::$baseWorldNames as $worldname) {
            $this->getServer()->loadLevel($worldname);
        }
	}
}
