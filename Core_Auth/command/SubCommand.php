<?php

namespace castialnet\command;

use castialnet\session\Session;

abstract class SubCommand
{
    public function __construct(Session &$session, array $args)
    {
        $this->execute($session, $args);
    }

    protected abstract function execute(Session &$session, array $args);

    public function getPlugin()
	{
		return $this->plugin;
	}
}
