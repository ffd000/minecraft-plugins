<?php

namespace teams;

use pocketmine\Player;
use pocketmine\utils\TextFormat;
use pocketmine\event\Listener;
use pocketmine\event\player\PlayerQuitEvent;
use pocketmine\event\player\PlayerJoinEvent;
use pocketmine\event\player\PlayerRespawnEvent;
use pocketmine\event\entity\EntityDamageEvent;
use pocketmine\event\entity\EntityDamageByEntityEvent;
use pocketmine\nbt\tag\IntTag;
use teams\team\PlayerReapplyEffectsTask;
use teams\team\TeamManager;
use teams\team\Team;

class EventHandler implements Listener
{
    /** @var Main */
    private $plugin;

    public function __construct(Main $plugin)
    {
        $this->plugin = $plugin;
    }

    public function onEntityDamage(EntityDamageEvent $event)
    {
        if ($event->getCause() instanceof EntityDamageByEntityEvent) {
            if (($player = $event->getEntity()) instanceof Player &&
                ($attacker = $event->getDamager()) instanceof Player) {
                if (($playerTeam = TeamManager::getTeamForPlayer($player)) !== null &&
                    ($attackerTeam = TeamManager::getTeamForPlayer($attacker)) !== null) {
                    if ($playerTeam->getType() === $attackerTeam->getType()) {
                        $attacker->sendMessage(TextFormat::RED . "You can't attack fellow team members!");

                        $event->setCancelled();
                    }
                }
            }
        }
    }

    /**
     * @param  PlayerRespawnEvent $event
     *
     * @priority HIGHEST
     */
    public function onPlayerRespawn(PlayerRespawnEvent $event)
    {
        $player = $event->getPlayer();
        if (($team = TeamManager::getTeamForPlayer($player)) !== null) {
            // It won't work if it's done in the same function...
            $this->plugin->getScheduler()->scheduleDelayedTask(new PlayerReapplyEffectsTask($player, $team), 0);
        }
    }

    public function onPlayerJoin(PlayerJoinEvent $event)
    {
        $player = $event->getPlayer();
        if ($player->namedtag->hasTag("teamId", IntTag::class)) {
            if (($team = TeamManager::joinTeam($player->namedtag->getInt("teamId"), $player, true)) !== false) {
                $player->teleport($this->plugin->getServer()->getLevelByName(TeamManager::$baseWorldNames[$team->getType()])->getSafeSpawn());

                $player->sendMessage("Rejoined " . $team->getColor() . $team->getName());
            } else {
                $player->sendMessage("Could not rejoin your team as it is full.");
                // Remove the tag here?? they won't be able to rejoin the team anymore.
                // $player->namedtag->removeTag("teamId");
            }
        }
    }

    public function onPlayerQuit(PlayerQuitEvent $event)
    {
        $player = $event->getPlayer();
        if (($team = TeamManager::getTeamForPlayer($player)) !== null) {
            // Player is removed from their team, but the team ID is still saved to their data, so they can rejoin.
            $team->removeMember($player);
        }
    }
}
