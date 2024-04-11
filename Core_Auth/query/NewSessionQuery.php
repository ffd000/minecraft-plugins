<?php

namespace castialnet\query;

use pocketmine\Server;
use pocketmine\Player;

use castialnet\session\Session;
use castialnet\session\SessionHelper;
use castialnet\Base;

class NewSessionQuery extends MysqlQuery
{
	public function onCompletion(Server $server)
	{
		$res = $this->getResult();
		if(isset($res["error"])) {
			$server->getLogger()->error($res["error"]);
		} else {
			if($this->getSuccess()) {
				$player = $server->getPlayer($this->getName());
				if(!$player) {
					$server->getLogger()->notice("Terminatting initialization for session ".$this->getName());
					return false;
				}
                if(SessionHelper::create($player, $res['sid'], $this->getName(), $this->getData()) instanceof Session) {
					$server->getLogger()->info("Successfully registered user #".$this->getPid());
                	$player->sendMessage("Successfully registered ses#".$this->getUid());
                } else {
                	$player->sendMessage("Something went wrong while initializing session");
                }
			}
		}
	}

	public function callback_query(\mysqli $mysqli)
	{
		$stmt = $mysqli->prepare($this->getSql());
		if(false === $stmt) {
			$this->setResult(["error" => "Could not initiate query: ".$mysqli->error]);
			return;
		}
		$bind = $stmt->bind_param("sss", $uid, $name, $rank);
		if(false === $bind) {
			$this->setResult(["error" => "Could not bind params: ".$stmt->error]);
			return;
		}

		$uid = $this->getUid();
	    $name = $this->getName();
	    $rank = $this->getRank();
	    $exec = $stmt->execute();
		if(false === $exec) {
			$this->setResult(["error" => "Could not execute query: ".$stmt->error]);
			return;
		}
		$this->success();
		$this->setResult(["sid" => $stmt->insert_id]);
	}
}