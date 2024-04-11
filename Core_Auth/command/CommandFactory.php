<?php

namespace castialnet\command;

use castialnet\command\operator\WarnCommand;
use castialnet\command\operator\RankCommand;
use castialnet\Base;

abstract class CommandFactory
{
	public static function registerAll(Base $base)
	{
		$map = $base->getServer()->getCommandMap();
		// core commands
		$map->registerAll("c", [
			new PermsCommand($base, "perms", "/perms", ["perm", "permissions"]),
			new WarnCommand($base, "command.admin", "admin"),
			new RankCommand($base, "command.rank", "rank", "/rank <set, rem, check> <user>", ["r"]),
			new AddPermCommand($base, "addperm")
		]);
		// game-specific
		foreach (Base::getGametypes() as $main) {
			foreach ($main->getCommands() as $command) {
				$map->register("b", $command);
			}
		}
	}
}