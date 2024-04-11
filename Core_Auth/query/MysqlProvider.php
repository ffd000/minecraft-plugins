<?php

namespace castialnet\query;
 
class MysqlProvider
{	
	public function __construct()
		{
		}

	public final function db_connect() {
	    static $connection;

	    if(!isset($connection)) {
	        $connection = new \mysqli(DBConnect::DB_HOST, DBConnect::DB_USER, DBConnect::DB_PASSWORD, DBConnect::DB_NAME, DBConnect::DB_PORT);
	    }

	    if(!$connection) {
	        return $connecion->connect_error; 
	    }
	    return $connection;
	}

	public final function query(callable $cb_query)
	{
		$conn = $this->db_connect();
		return $cb_query($conn);
	}
}