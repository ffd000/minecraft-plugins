<?php

namespace castialnet\command;

use pocketmine\command\CommandSender;

use castialnet\session\SessionHelper;

class AddPermCommand extends BaseCommand
{
    public function execute(CommandSender $sender, string $label, array $args)
    {
        $session = SessionHelper::get($sender);
    	//if($this->verify($session)) {
            $session->addPermission($args[0]);
            $sender->sendMessage(sprintf("Successfully added permission '%s' to session '%s'", $args[0], $session->getName()));
    	/*} else {
    		$sender->sendMessage(sprintf(Error::CMD_NO_PERMS, $this->getCommandPerm()));
    	}*/
    }
}
