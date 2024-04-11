<?php

namespace castialnet\command;

use pocketmine\plugin\Plugin;

use pocketmine\command\{Command, CommandSender, PluginIdentifiableCommand};

use castialnet\Base;

abstract class BaseCommand extends Command implements PluginIdentifiableCommand
{
	private $plugin;

	public function __construct(Plugin $plugin, string $cmdName, string $usage = "", array $aliases = [])
	{
		$this->plugin = $plugin;
        parent::__construct($cmdName, "", $usage, $aliases);
	}

	abstract public function execute(CommandSender $sender, string $label, array $args);

	public function getPlugin() :Plugin
	{
		return $this->plugin;
	}
}