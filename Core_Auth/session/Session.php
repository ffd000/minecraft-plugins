<?php

namespace castialnet\session;

use pocketmine\Player;

use castialnet\rank\Rank;

class Session
{
    /**
     * Session ID (database)
     * @var int
     */
    private $sid = 0;
    /**
     * ID of associated Player
     * @var int
     */
    private $pid = 0;
    /**
     * UUID of associated Player
     * @var string
     */
    private $uid = "";
    
    private $name = "";
    
    private $rank = 0;

    private $permissions = [];

    private $mutedTime = false;
    private $bannedTime = false;

    private $global_pts = 0;
    private $warn_pts = 0;
    /** @var Player */
    private $player = null;

    public function __construct(Player $player, int $sid, string $name, array $data = [])
    {
        $this->sid = $sid;
        $this->name = $name;
        foreach ($data as $key => $value) {
            $this->$key = $value;
        }
        $this->player = $player;
        $this->recalcPermissions();
    }
    
    public function getSid()
    {
        return $this->sid;
    }

    public function getUid()
    {
        return $this->uid;
    }

    public function getPid()
    {
        return $this->sid;
    }

    public function getState()
    {
        return $this->state;
    }

    public function getName()
    {
        return $this->name;
    }

    public function getRank()
    {
        return $this->rank;
    }

    public function setRank(int $rank)
    {
        $this->rank = $rank;
        $this->recalcPerms();
    }

    public function isStaff()
    {
        return ($this->rank & 0xf0) === RankManager::RANK_STAFF;
    }

    public function isVip()
    {
        return ($this->rank & 0xf0) === RankManager::RANK_PAID;
    }

    public function isMuted()
    {
        return $this->mutedTime !== false;
    }

    public function isBanned()
    {
        return $this->bannedTime !== false;
    }

    public function getAllPerms()
    {
        return $this->permissions;
    }

    public function hasPermission(string $permission) :bool
    {
        return count(array_diff($this->permissions, [$permission])) !== count($this->permissions);
    }

    public function addPermission(string $permission)
    {
        if(!$this->hasPermission($permission)) {
            $this->permissions[] = $permission;
        }
    }

    public function getPlayer()
    {
        return $this->player;
    }

    public function getDisplayName()
    {
        return $this->player->getDisplayName();
    }

    private function recalcPermissions()
    {
        $this->permissions = Rank::getAllPerms($this->rank);
    }
}