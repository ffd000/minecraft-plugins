<?php

namespace castialnet;

use pocketmine\event\Listener;
use pocketmine\plugin\PluginBase;

use pocketmine\command\{Command, CommandSender, CommandExecutor};

use pocketmine\Server;
use pocketmine\Player;

use castialnet\command\CommandFactory;
use castialnet\rank\RankManager;
use castialnet\query\NewSessionQuery;
use castialnet\query\MysqlProvider;
use castialnet\session\SessionHelper;
use castialnet\session\Session;

class Base extends PluginBase implements Listener
{
    /** @var Base */
    private static $instance = null;
    private static $name = "";
    /** @var SessionHelper */
    private $sessionHelper = null;

    private $sessions = [];
    private static $gametypes = [];
    
    private $status;
    private $players;

    public function onEnable()
    {
        $time = microtime(true);
        self::$instance = $this;
        self::$name = $this->getName();
        if(!is_dir($this->getDataFolder())) {
            mkdir($this->getDataFolder());
        }

        $provider = new MysqlProvider();
        $provider->db_connect(); 
        $this->sessionHelper = new SessionHelper();

        $bb = $this->getServer()->getPluginManager()->getPlugin("BuildBattle") ?: null;
        self::$gametypes[] = $bb;
        
        CommandFactory::registerAll($this);

        $this->getServer()->getPluginManager()->registerEvents($this, $this);
        $this->getServer()->getPluginManager()->registerEvents(new EventListener($this), $this);
        $this->getLogger()->info("Base loaded (" . round((microtime(true) - $time), 2) . "s)");
    }

    /**
     * Gets list of loaded game plugins
     * @return array
     */
    public static function getGameTypes()
    {
        return self::$gametypes;
    }

    /**
     * Gets the SessionHelper instance
     * @return SessionHelper|null
     */
    public function getSessionHelper()
    {
        return $this->sessionHlper;
    }

    /**
     * Gets the Base instance
     * @return Base
     */
    public static function getInstance() :Base
    {
        return self::$instance;
    }
}
