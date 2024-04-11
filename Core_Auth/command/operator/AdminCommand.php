<?php

namespace castialnet\command\operator;

use pocketmine\command\CommandSender;

use castialnet\session\Session;
use castialnet\command\BaseCommand;
use castialnet\Base;

abstract class AdminCommand extends BaseCommand
{
	private $commandPerm = "";

	public function __construct(Base $base, string $commandPerm, string $cmdName, string $usage = "", array $aliases = [])
	{
		$this->commandPerm = $commandPerm;
		parent::__construct($base, $cmdName, $usage, $aliases);
	}

	protected function verify(Session $session) :bool
	{
		return $session->hasPermission($this->getCommandPerm());
	}

	protected function getCommandPerm()
	{
		return $this->commandPerm;
	}
}