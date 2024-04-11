<?php

namespace castialnet;

use pocketmine\utils\TextFormat as T;

use castialnet\rank\Rank;

abstract class ChatFormatter
{
	const FORMAT_LOBBY_CHAT = "&7{rankformat} {name} » &8{message}";
	const FORMAT_RANK = "&d&ko&r&d[ {rank} &d]&ko&r&7";

    private const SYMBOLS = [
    	"&0",
		"&1",
		"&2",
		"&3",
		"&4",
		"&5",
		"&6",
		"&7",
		"&8",
		"&9",
		"&a",
		"&b",
		"&c",
		"&d",
		"&e",
		"&f",
		"&k",
		"&l",
		"&m",
		"&n",
		"&i",
		"&r"
    ];

	private const COLORS = [
		T::BLACK,
		T::DARK_BLUE,
		T::DARK_GREEN,
		T::DARK_AQUA,
		T::DARK_RED,
		T::DARK_PURPLE,
		T::GOLD,
		T::GRAY,
		T::DARK_GRAY,
		T::BLUE,
		T::GREEN,
		T::AQUA,
		T::RED,
		T::LIGHT_PURPLE,
		T::YELLOW,
		T::WHITE,
		T::OBFUSCATED,
		T::BOLD,
		T::STRIKETHROUGH,
		T::UNDERLINE,
		T::ITALIC,
		T::RESET
	];

	public static function formatColors(string $format) :string
	{
		$c = count(self::COLORS);
        for($i=0; $i < $c; $i++) {
            $format = str_replace(self::SYMBOLS[$i], self::COLORS[$i], $format);
        }
        return $format;
    }

    public static function getChatFormat($session, string $message) :string
    {
    	$format = self::FORMAT_LOBBY_CHAT;
    	$rankformat = "";
        if($session->getName() === "spopovabgs") {
            $rank = $session->getRank();
	        $rankformat = self::FORMAT_RANK;
	        $rankformat = str_replace("{rank}", Rank::RANK_FORMATS[$rank], $rankformat);
        }
        $format = str_replace("{rankformat}", $rankformat, $format);
        $format = str_replace("{name}", $session->getDisplayName(), $format);
        $format = str_replace("{message}", $message, $format);
        return self::formatColors($format);
    }
}