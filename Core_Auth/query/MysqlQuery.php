<?php

namespace castialnet\query;

use pocketmine\scheduler\AsyncTask;

use castialnet\Base;

abstract class MysqlQuery extends AsyncTask
{
	protected $sql = "";

	protected $sid = 0;
	protected $uid = "";
	protected $pid = 0;

	protected $name = "";
	protected $rank = 0;

	protected $success = false;

	public function __construct(Base $main, string $sql, string $name, array $data = [])
	{
		$this->sql = $sql;
		$this->name = $name;
		if(!empty($data)) {
			foreach ($data as $key => $value) {
				$this->$key = $value; 	
			}
		}
		$main->getServer()->getScheduler()->scheduleAsyncTask($this);
	}

	public function onRun()
	{
		try {
			$provider = new MysqlProvider();
			$provider->query([$this, 'callback_query']);
		} catch(\Exception $e) {
			$this->setResult(["error" => $e->getMessage()]);
			return;
		}
	}

	public abstract function callback_query(\mysqli $mysqli);

	public function getSuccess() :bool
	{
		return $this->success;
	}

	protected function success()
	{
		return $this->success = true;
	}

	protected function getData() :array
	{
		return [
            "uid" => $this->getUid(),
            "pid" => $this->getPid(),
            "rank" => $this->getRank()
        ];
	}

	protected function getSid() :int
	{
		return $this->sid;
	}

	protected function getPid() :int
	{
		return $this->pid;
	}

	protected function getUid() :string
	{
		return $this->uid;
	}

	protected function getSql() :string
	{
		return $this->sql;
	}

	protected function getName() :string
	{
		return $this->name;
	}

	protected function getRank() :string
	{
		return $this->rank;
	}
}