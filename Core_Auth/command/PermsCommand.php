<?php

namespace castialnet\command;

use pocketmine\command\CommandSender;

use castialnet\session\SessionHelper;

class PermsCommand extends BaseCommand
{
    public function execute(CommandSender $sender, string $label, array $args)
    {
    	$session = SessionHelper::get($sender);
		$sender->sendMessage("Your permissions:");
		foreach ($session->getAllPerms() as $perm) {
			$sender->sendMessage(" - ".$perm);
        }
    }
}
