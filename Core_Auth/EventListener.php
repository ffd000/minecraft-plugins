<?php

namespace castialnet;

use pocketmine\event\Listener;

use pocketmine\item\Item;

use pocketmine\Player;

use castialnet\query\FetchSessionQuery;
use castialnet\session\Session;
use castialnet\session\SessionHelper;
use castialnet\rank\Rank;
use castialnet\Base;

use pocketmine\utils\{Color, TextFormat as T};
use pocketmine\nbt\tag\{CompoundTag, StringTag};
use pocketmine\network\mcpe\protocol\ClientboundMapItemDataPacket;

class EventListener implements Listener
{
	private $base;

	public function __construct(Base $base)
	{
		$this->base = $base;
	}

    public function onJoin(\pocketmine\event\player\PlayerJoinEvent $event)
    {
        $player = $event->getPlayer();
        $folder = $this->base->getDataFolder();
        @mkdir($folder);
        for ($i=0; $i < 3; $i++) {
            $colors = [];
            $image = @imagecreatefrompng($folder."image-".$i.".png");
            if($image === false) {
                $player->sendMessage(T::RED."Error with image!");
                return;
            }
            $r = 0;
            $g = 0;
            $b = 0;
            $ancho = 128;
            $altura = 128;
            $image = imagescale($image, $ancho, $altura, IMG_NEAREST_NEIGHBOUR);
            imagepng($image, $folder."image-".$i.".png");
            for($y = 0; $y < $altura; ++$y) {
                for($x = 0; $x < $ancho; ++$x) {
                    $rgb = imagecolorat($image, $x, $y);
                    $color = imagecolorsforindex($image, $rgb);
                    $r = $color["red"];
                    $g = $color["green"];
                    $b = $color["blue"];
                    $colors[$y][$x] = new Color($r, $g, $b, 0xff);
                }
            }
            $map = new Item(Item::FILLED_MAP, 0, 1);
            $key = 18293883+$i;
            $tag = new CompoundTag("", []);
            $tag->map_uuid = new StringTag("map_uuid", $key);
            $map->setCompoundTag($tag);
            $player->getInventory()->addItem($map);
            $pk = new ClientboundMapItemDataPacket();
            $pk->mapId = $key;
            $pk->type = ClientboundMapItemDataPacket::BITFLAG_TEXTURE_UPDATE;
            $pk->height = 128;
            $pk->width = 128;
            $pk->scale = 1;
            $pk->colors = $colors;
            $player->dataPacket($pk);
            $player->sendMessage(T::GREEN."Image completed!");
        }
        return true;
        $session = SessionHelper::get($event->getPlayer());
        if($session instanceof Session) {
            // session already iniitialiazed
            $session->getPlayer()->sendMessage("Session data found. Welcome back, %s. SID = %d\n", $session->getDisplayName(), $session->getPid());
            return true;
        } else {
            // try to fetch from db
            $player = $event->getPlayer();
            new FetchSessionQuery($this->getBase(), "SELECT * FROM users WHERE name=? LIMIT 1", $player->getName(), $this->getDefaultData($player));
        }
    }

    public function onPreLogin(\pocketmine\event\player\PlayerPreLoginEvent $event)
    {

    }

    public function onChat(\pocketmine\event\player\PlayerChatEvent $event)
    {
        $session = SessionHelper::get($event->getPlayer());
        $event->setFormat(ChatFormatter::getChatFormat($session, $event->getMessage()));
    }

	private function verifySession(?Session $ses)
	{
		//
	}

	private function getBase() :Base
	{
		return $this->base;
	}

    private function getDefaultData($player) :array
    {
        return [
            "uid" => $player->getUniqueId()->toString(),
            "pid" => $player->getId(),
            "rank" => Rank::RANK_STAFF_OPERATOR
        ];
    }
}