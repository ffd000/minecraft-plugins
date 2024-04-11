<?php

namespace castialnet\query;

use pocketmine\Server;
use pocketmine\Player;

use castialnet\session\Session;
use castialnet\session\SessionHelper;
use castialnet\Base;

class FetchSessionQuery extends MysqlQuery
{
	public function onCompletion(Server $server)
	{
		$res = $this->getResult();
		if(is_array($res) && isset($res["error"])) {
			$server->getLogger()->error($res["error"]);
		} else {
			if($this->getSuccess()) {
				$player = $server->getPlayer($this->getName());
				if(!$player) {
					$server->getLogger()->notice("Terminatting initialization for session ".$this->getName());
					return false;
				}
	            if(SessionHelper::create($player, $res->results['id'], $this->getName(), $this->getData()) instanceof Session) {
					printf("Database records found. Fetched data for session %s with SID %d\n", $this->getName(), $res->results['id']);
	            } else {
		            printf("Something went wrong");
	            }
	        } else {
				printf("Unable to fetch data for %d. Creating new...\n", $this->getName());
                new NewSessionQuery(Base::getInstance(), "INSERT INTO users (uid, name, rank) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE uid = values(uid), name = values(name), rank = values(rank);", $this->getName(), $this->getData());

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
		$bind = $stmt->bind_param("s", $name);
		if(false === $bind) {
			$this->setResult(["error" => "Could not bind params: ".$stmt->error]);
			return;
		}
		$name = $this->getName();
	    $exec = $stmt->execute();
		$stmt->store_result();
		if(false === $exec) {
			$this->setResult(["error" => "Could not execute query: ".$stmt->error]);
			return;
		}
		$objs = null;
	    if($row = $this->fetchAssoc($stmt)) {
	    	$result = new SessionQueryResult();
		    foreach ($row as $key => $val) {
		        $result->$key = $val;
		    }
	    	$objs = $result;
	    }
	    if($objs !== null) {
		    $this->success();
			$this->setResult($objs);
	    }
	}

	private function fetchAssoc($stmt)
	{
	    if($stmt->num_rows>0) {
	        $result = [];
	        $md = $stmt->result_metadata();
	        $params = [];
	        while ($field = $md->fetch_field()) {
	            $params[] = &$result[$field->name];
	        }
	        call_user_func_array([$stmt, 'bind_result'], $params);
	        if($stmt->fetch())
	            return $result;
	    }
	    return null;
	}
}