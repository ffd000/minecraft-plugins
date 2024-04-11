<?php

namespace castialnet\command\operator;

use pocketmine\command\CommandSender;

use castialnet\session\SessionHelper;

use castialnet\Error;

class WarnCommand extends AdminCommand
{
    public function execute(CommandSender $sender, string $label, array $args)
    {
        $session = SessionHelper::get($sender);
    	if($this->verify($session)) {
    		if(count($args) > 0) {
            	$sender->sendMessage("Executed!");
	        } else {
	            $sender->sendMessage($this->getUsage());
	        }
    	} else {
    		$sender->sendMessage(sprintf(Error::CMD_NO_PERMS, $this->getCommandPerm()));
    	}
    }
}
