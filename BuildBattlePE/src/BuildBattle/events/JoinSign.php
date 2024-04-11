<?php

namespace BuildBattle\events;

use pocketmine\scheduler\PluginTask;

use pocketmine\event\Listener;

use pocketmine\utils\Config;

use pocketmine\level\Level;
use pocketmine\level\Position;

use pocketmine\math\Vector3;

use pocketmine\event\player\PlayerInteractEvent;
use pocketmine\event\block\BlockPlaceEvent;

use pocketmine\block\Block;
use pocketmine\tile\Sign;

use BuildBattle\Tasks\LobbyTimerTask;

use BuildBattle\Main;

class JoinSign implements Listener {

  private $players = [];
  private $buildzone = "";

  private $plugin;

  public function __construct(Main $plugin) {
    $this->plugin = $plugin;
  }

  public function onInteract(PlayerInteractEvent $event) {
    $config = new Config($this->plugin->getDataFolder() . "arenas.json", Config::JSON);
    $player = $event->getPlayer();
    $name = $player->getName();
    $block = $event->getBlock();
    $tile = $player->getLevel()->getTile($block);
    if($this->plugin->mode == 28) {
      if($tile instanceof Sign) {
        $text = $tile->getText();
        $tile->setText($this->plugin->prefix, "§l§2|| §aJoin §2||", "§b0§8/§b16", $this->plugin->currentArena);
        $this->plugin->mode = 0;
        $player->sendMessage($this->plugin->prefix . " §bSign registered. Setup complete.");
        unset($this->plugin->currentArena);
        unset($this->plugin->currentLobby);
      } else {
        $player->sendMessage($this->plugin->prefix . " §cPlease tap a sign.");
        return;
      }
    } else {
      if($tile instanceof Sign) {
        $text = $tile->getText();
        if($text[0] === $this->plugin->prefix) {
          if($text[1] === "§l§2|| §aJoin §2||") {
            $arenas = $config->get("arenas");
            $arenas[0][$text[3]]["status"] = "ingame"; //testing
            $lobby = $arenas[0][$text[3]]["waitroomworld"];
            $this->plugin->getServer()->loadLevel($lobby);
            $waitroom = $this->plugin->getServer()->getLevelByName($lobby);
            $player->sendMessage($this->plugin->prefix . " §6Joining BuildBattle game on map §e" . $text[3] . "§6...");
            if(count($waitroom->getPlayers()) < 1) {
              $arenas[0][$text[3]]["waittime"] = 61;
              $arenas[0][$text[3]]["gametime"] = 121;
              $arenas[0][$text[3]]["status"] = "waiting";
              $config->set("arenas", $arenas);
              $config->save();
            } else {
              $this->plugin->getServer()->getScheduler()->scheduleRepeatingTask(new LobbyTimerTask($this->plugin), 20);
            }
            $player->teleport(new Position($arenas[0][$text[3]]["waitroompos"]["x"], $arenas[0][$text[3]]["waitroompos"]["y"], $arenas[0][$text[3]]["waitroompos"]["z"], $waitroom));
            $levelplayers = $waitroom->getPlayers();
            $count = count($levelplayers);
            foreach($levelplayers as $pl) {
              $pl->sendMessage($this->plugin->prefix . " §c" . $name . " §ejoined the game §8[§b" . $count . "§7/§b16§8]");
              $names = $pl->getName();
              array_push($this->players, $names);
            }
            $arenas[0][$text[3]]["players"] = [];
            array_push($arenas[0][$text[3]]["players"], $this->players);
            $config->set("arenas", $arenas);
            $config->save();
          }
        }
      }
    }
  }

  public function onPlace(BlockPlaceEvent $event) {
    if($this->plugin->mode === 0) {
      $config = new Config($this->plugin->getDataFolder() . "config.json", Config::JSON);
      $arenas = $config->get("arenas");
      $player = $event->getPlayer();
      $block = $event->getBlock();
      $level = $player->getLevel()->getFolderName();
      $bzone = $this->getBuildZone($player->getName(), $level);
      $minx = $arenas[0][$level][$bzone]["0"]["x1"];
      $miny = $arenas[0][$level][$bzone]["0"]["y1"];
      $minz = $arenas[0][$level][$bzone]["0"]["z1"];
      $maxx = $arenas[0][$level][$bzone]["1"]["x2"];
      $maxy = $arenas[0][$level][$bzone]["1"]["y2"];
      $maxz = $arenas[0][$level][$bzone]["1"]["z2"];

      $x = $block->getX();
      $y = $block->getY();
      $z = $block->getZ();

      if($x <= $minx && $x >= $maxx && $y >= $miny && $y <= $maxy && $z <= $minz && $z >= $maxz) {
        $event->setCancelled(false);
      } else {
        $event->setCancelled(true);
        $player->sendMessage("§cYou cannot build outside your building area!");
      }
    }
  }

  public function getBuildZone(string $name, string $arena) {
    $config = new Config($this->plugin->getDataFolder() . "config.json", Config::JSON);
    $arenas = $config->get("arenas");

    foreach($arenas[0][$arena] as $buildzone => $data) {
      if($buildzone != "status" && $buildzone != "waitlobby" && $buildzone != "waitroom") {
        if($arenas[0][$arena][$buildzone]["builder"] == $name) {
          return $buildzone;
        }
      }
    }
  }
}
