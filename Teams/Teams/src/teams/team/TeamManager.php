<?php

namespace teams\team;

use pocketmine\Player;
use pocketmine\utils\TextFormat;
use pocketmine\entity\Effect;
use pocketmine\nbt\tag\IntTag;

abstract class TeamManager
{
    /** @var Team[] */
    private static $activeTeams = [];

    public static $baseWorldNames = [
        Team::TEAM_RED => "Red Base",
        Team::TEAM_YELLOW => "Yellow Base",
    ];

    public static function initTeams()
    {
        self::$activeTeams = [
            new Team(Team::TEAM_RED, [Effect::STRENGTH, Effect::NIGHT_VISION], TextFormat::RED, "Red Crew"),
            new Team(Team::TEAM_YELLOW, [Effect::SPEED, Effect::REGENERATION], TextFormat::YELLOW, "Yellow Crew"),
        ];
    }

    public static function checkCanSwitchTeam(Player $player) :bool
    {
        $nbt = $player->namedtag;
        if ($nbt->hasTag("timesSwitchedTeams", IntTag::class)) {
            return $nbt->getInt("timesSwitchedTeams") === 0;
        }
        return true;
    }

    /**
     * Adds a player to the specified team.
     * @param  int      $teamId The team to join
     * @param  Player   $player
     * @param  bool     $returnTeam Whether to return the team instance on success
     * @return bool|Team
     */
    public static function joinTeam(int $teamId, Player $player, bool $returnTeam = false)
    {
        // if (self::getTeamForPlayer($player) !== null)
        //     return false;

        if (self::$activeTeams[$teamId]->addMember($player) === false)
            return false;

        return $returnTeam ? self::$activeTeams[$teamId] : true;
    }

    /**
     * Adds a player to the first open team, if any.
     * @param  Player $player
     * @param  bool   $returnTeam Whether to return the team instance on success
     * @return bool|Team
     */
    // public static function automaticJoin(Player $player, bool $returnTeam)
    // {
    //     if (self::getTeamForPlayer($player) !== null)
    //         return false;
    //
    //     $c = count(self::$activeTeams); // Can be replaced with a constant as well, but this way it doesn't have to be updated as new teams are added.
    //     for ($i=0; $i < $c; $i++) {
    //         if (self::$activeTeams[$i]->addMember($player) === false)
    //             continue;
    //
    //         return $returnTeam ? self::$activeTeams[$i] : true;
    //     }
    //     return false;
    // }

    /**
     * If the player is a member of a team, returns the team.
     * @param  Player $player
     * @return Team|null
     */
    public static function getTeamForPlayer(Player $player) :?Team
    {
        foreach (self::$activeTeams as $team)
        {
            if ($team->hasMember($player))
                return $team;
        }
        return null;
    }

    /**
     * If the player is a member of a team, removes them from the team.
     * @param  Player $player
     * @return bool
     */
    public static function exit(Player $player) :bool
    {
        if (($team = self::getTeamForPlayer($player)) !== null) {
            $team->removeMember($player);
            $player->removeAllEffects();

            return true;
        }
        return false;
    }

    /**
     * Exits all players currently in a team.
     */
    public static function exitAll()
    {
        foreach (self::$activeTeams as $team) {
            $team->removeAll();
        }
    }
}
