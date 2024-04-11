<?php

namespace castialnet\query;

class SessionQueryResult
{
	public $results = [];
 
	public function __construct()
		{
		}

	public function __set($var, $val)
	{
		$this->results[$var] = $val;
	}

	public function __get($var)
	{  
		if(isset($this->results[$var])) {
			return $this->results[$var];
		} else {
			return null;
		}
	}
}