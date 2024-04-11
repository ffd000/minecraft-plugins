<?php

namespace castialnet\session;

use pocketmine\Player;

use castialnet\rank\Rank;
use castialnet\Base;

class SessionHelper
{
    private static $sessions = [];

    public function __construct()
        {
        }

    public static function getAll() :array
    {
        return self::$sessions;
    }

    public static function get(Player $player)
    {
        return isset(self::$sessions[$player->getName()]) ? self::$sessions[$player->getName()] : null;
    }

    public static function unset(?Session $session)
    {
        if(!$session) return false;
        unset(self::$sessions[$session->getName()]);
    }

    public static function exists(string $name) :bool
    {
        return isset(self::$sessions[$name]);
    }

    public static function create(Player $player, int $sid, string $name, array $data = [])
    {
        try {
            $session = new Session($player, $sid, $name, $data);
            self::$sessions[$name] = $session;
            printf("Registered session ".$name.": ".$session->getUid()."\n");
            return $session;
        } catch(\Exception $e) {
            Base::getInstance()->getServer()->getLogger()->error("An error occured: ".$e->getMessage()." on line ".$e->getLine());
            return null;
        }
    }
}