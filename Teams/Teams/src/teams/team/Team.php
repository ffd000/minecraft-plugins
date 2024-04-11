<?php

namespace teams\team;

use pocketmine\Player;
use pocketmine\entity\Effect;
use pocketmine\entity\EffectInstance;
use pocketmine\nbt\tag\IntTag;

class Team
{
    const TEAM_RED = 0;
    const TEAM_YELLOW = 1;

    const MAX_PLAYERS_COUNT = 4;

    /** @var int */
    private $type;
    /** @var Effect[] */
    private $effects = [];
    /** @var string */
    private $color;
    private $name;
    /**
     * A list of the names of all members in this team.
     * @var string[]
     */
    private $members = [];

    public function __construct(int $type, array $effects, string $color, string $name)
    {
        $this->type = $type;
        $this->effects = $effects;
        $this->color = $color;
        $this->name = $name;
    }

    public function getType() :int { return $this->type; }
    public function getEffects() :array { return $this->effects; }
    public function getColor() :string { return $this->color; }
    public function getName() :string { return $this->name; }

    public function addMember(Player $player) :bool
    {
        if (count($this->members) === self::MAX_PLAYERS_COUNT)
            return false;

        // This is done so we can use isset() instead of in_array() to check if the player
        // is in the list as it is faster. Later on if team data per player has to be stored
        // such as, say, kills, this can be extended.
        $this->members[$player->getName()] = 0;

        $player->removeAllEffects();

        $player->namedtag->setInt("timesSwitchedTeams", (int)$player->namedtag->hasTag("timesSwitchedTeams", IntTag::class));
        $player->namedtag->setInt("teamId", $this->type);
        $player->setNameTag($this->color . $player->getName());
        $player->setDisplayName($this->color . $player->getName());

        $this->applyEffects($player);

        return true;
    }

    public function applyEffects(Player $player)
    {
        foreach ($this->effects as $effect) {
            $player->addEffect((new EffectInstance(Effect::getEffect($effect)))->setDuration(INT32_MAX)->setAmplifier(0)->setVisible(false));
        }
    }

    public function removeMember(Player $player)
    {
        unset($this->members[$player->getName()]);
    }

    public function hasMember(Player $player) :bool
    {
        return isset($this->members[$player->getName()]);
    }

    public function removeAll()
    {
        $this->members = [];
    }
}
