<?php

namespace castialnet\command\operator\rank;

use pocketmine\rank\Rank;

use castialnet\command\SubCommand;
use castialnet\session\Session;
use castialnet\Error;

class SetRank extends SubCommand
{
    protected function execute(Session &$session, array $args)
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
                $session->getPlayer()->sendMessage(sprintf("Successfully updated rank of '%s' to '%s'."), $recepSession->getName(), $args[2]);
                $session->getPlayer()->sendMessage($recepSession->getRankName());
            } else {
                $session->getPlayer()->sendMessage(sprintf(Error::CMD_INVALID_RANK, $args[2]));
            }
        } else {
            $session->getPlayer()->sendMessage(sprintf(Error::CMD_INVALID_PLAYER, $args[1]));
        }
    }
}
