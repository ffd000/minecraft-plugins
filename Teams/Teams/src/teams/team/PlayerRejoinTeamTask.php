<?php

namespace teams\team;

use pocketmine\Player;
use pocketmine\entity\Effect;
use pocketmine\entity\EffectInstance;
use pocketmine\scheduler\Task;

class PlayerReapplyEffectsTask extends Task
{
    private $player;
    /** @var Team */
    private $team;

    public function __construct(Player $player, Team $team)
    {
        $this->player = $player;
        $this->team = $team;
    }

    public function onRun($currentTick)
    {
        $this->team->applyEffects($this->player);
    }
}
