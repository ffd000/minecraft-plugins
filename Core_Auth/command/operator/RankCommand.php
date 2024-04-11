<?php

namespace castialnet\command\operator;

use pocketmine\command\CommandSender;

use castialnet\rank\Rank;
use castialnet\session\SessionHelper;
use castialnet\Error;

class RankCommand extends AdminCommand
{
    public function execute(CommandSender $sender, string $label, array $args)
    {
        $session = SessionHelper::get($sender);
    	if($this->verify($session)) {
    		if(count($args) > 0) {
                switch ($args[0]) {
                    case "set":
                        //new SetRank($session, $args);
                        $this->set($session, $args);
                        break;
                    default:
                        $sender->sendMessage($this->getUsage());
                        break;
                }
                return true;
	        }
            $sender->sendMessage($this->getUsage());
    	} else {
    		$sender->sendMessage(sprintf(Error::CMD_NO_PERMS, $this->getCommandPerm()));
    	}
    }

    private function set(&$session, array $args)
    {
        if($recepient = $this->getPlugin()->getServer()->getPlayer($args[1])) {
            $rank = Rank::getRankByName($args[2]);
            if($rank !== false) {
                $recepSession = SessionHelper::get($recepient);
                if(!$recepSession) {
                    $session->getPlayer()->sendMessage(sprintf(Error::UNREGISTERED_SESSION, $args[1]));
                    return false;
                }
                $recepSession->setRank($rank);
                $session->getPlayer()->sendMessage(sprintf("Successfully updated rank of '%s' to '%s'.", $recepSession->getName(), $args[2]));
            } else {
                $session->getPlayer()->sendMessage(sprintf(Error::CMD_INVALID_RANK, $args[2]));
            }
        } else {
            $session->getPlayer()->sendMessage(sprintf(Error::CMD_INVALID_PLAYER, $args[1]));
        }
    }
}
