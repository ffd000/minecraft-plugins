<?php

namespace castialnet;

use pocketmine\utils\TextFormat as T;

interface Error
{
	const ERROR = T::BOLD.T::DARK_RED."Error! ".T::RESET.T::RED;

	const CMD_NO_PERMS = self::ERROR."You do not have the required permission to execute this command.\n - %s\nIf you think this was a mistake on our part, please notify the administrators.";
	
	const CMD_INVALID_PLAYER = self::ERROR."Player '%s' does not exist.";
	const CMD_INVALID_RANK = self::ERROR."Rank '%s' does not exist.";

	const UNREGISTERED_SESSION = self::ERROR."Session '%s' is unregistered.";
}