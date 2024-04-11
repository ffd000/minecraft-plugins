<?php

namespace BuildBattle\tasks;

use pocketmine\scheduler\PluginTask;
use pocketmine\plugin\Plugin;

use pocketmine\Server;
use pocketmine\Player;

use pocketmine\level\Level;

use pocketmine\math\Vector3;

use pocketmine\utils\Config;

use BuildBattle\Main;

class LobbyTimerTask extends PluginTask {

  private $plugin;

  public function __construct(Main $plugin) {
    parent::__construct($plugin);
    $this->plugin = $plugin;
  }

  public function onRun($tick) {
    $config = new Config($this->plugin->getDataFolder() . "arenas.json", Config::JSON);
    $arenas = $config->get("arenas");
    if(!empty($config->getAll())) {
      foreach($arenas[0] as $arena => $data) {
        $lobby = $arenas[0][$arena]["waitroomworld"];
        $waitroom = $this->plugin->getServer()->getLevelByName($lobby);
        $players = $waitroom->getPlayers();
        $count = count($players);
        $waittime = $arenas[0][$arena]["waittime"];
        $status = $arenas[0][$arena]["status"];
        if($status === "waiting") {
          if($count >= 2) {
            if($waittime > 0) {
              $waittime--;
              $arenas[0][$arena]["waittime"] = $waittime;
              $config->set("arenas", $arenas);
              $config->save();
            }
          }

          /* TODO
          if(!(isset($this->theme))) {
            shuffle($this->plugin->themes);
            $this->theme = $this->plugin->themes[0];
          }*/

          foreach($players as $player) {
            if($count >= 2) {
              if($waittime > 0) {
                if($waittime % 60 == 0 && $waittimw != 60) {
                  $player->sendMessage($this->plugin->prefix . " §9Build Battle will begin in §1" . $waittime / 60 . " §9minutes.");
                } elseif($waittime == 60) {
                  $player->sendMessage($this->plugin->prefix . " §9Build Battle will begin in §1" . $waittime / 60 . " §9minute.");
                } elseif($waittime == 30 or $waittime == 15 or $waittime == 10 or ($waittime <= 5 && $waittime > 1)) {
                  $player->sendMessage($this->plugin->prefix . " §9Build Battle will begin in §1" . $waittime . " §9seconds.");
                } elseif($waittime == 1) {
                  $player->sendMessage($this->plugin->prefix . " §9Build Battle will begin in §1" . $waittime . " §9second.");
                }
              } else {
                $player->sendMessage($this->plugin->prefix . " §eGame is starting..");
                $arenas[0][$arena]["status"] = "ingame";
                $config->set("arenas", $arenas);
                $config->save();

                $name = $player->getName();

                $level = $this->plugin->getServer()->getLevelByName($arena);
                $player->teleport($level->getSafeSpawn(), 0, 0);

                $buildzone = count($level->getPlayers());

                $x = $arenas[0][$arena]["buildzone" . $buildzone]["center"]["x"];
                $y = $arenas[0][$arena]["buildzone" . $buildzone]["center"]["y"];
                $z = $arenas[0][$arena]["buildzone" . $buildzone]["center"]["z"];

                $arenas[0][$arena]["buildzone" . $buildzone]["builder"] = $name;
                $config->set("arenas", $arenas);
                $config->save();

                $player->teleport(new Vector3($x, $y + 1, $z));

                $player->sendMessage($this->plugin->prefix . " §7Theme: §f" . $this->theme);
                unset($this->theme);
              }
            }
          }
        }
      }
    }
  }
}
