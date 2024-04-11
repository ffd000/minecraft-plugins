<?php

namespace BuildBattle\tasks;

use pocketmine\scheduler\PluginTask;
use pocketmine\plugin\Plugin;

use pocketmine\level\Level;

use pocketmine\level\Level;

use pocketmine\math\Vector3;

use pocketmine\utils\Config;

use BuildBattle\Main;

class GameTimerTask extends PluginTask {

  private $buildzone;

  public function __construct(Main $plugin) {
    parent::__construct($plugin);
    $this->plugin = $plugin;
  }

  public function onRun($tick) {
    $config = new Config($this->plugin->getDataFolder() . "arenas.json", Config::JSON);
    $arenas = $config->get("arenas");
    if(!empty($config->getAll())) { //if arenas are set
      foreach($arenas[0] as $arena => $data) {
        $gamearena = $this->plugin->getServer()->getLevelByName($arena);
        $players = $gamearena->getPlayers();
        $count = count($players);
        $waittime = $data["waittime"];
        $gametime = $data["gametime"];
        //$votetime = $data["votetime"]; TODO
        $status = $data["status"];
        if($status === "ingame") {
          if($count >= 2) {
            if($gametime > 0) {
              $gametime--;
              $arenas[0][$arena]["gametime"] = $gametime;
              $config->set("arenas", $arenas);
              $config->save();
            }
          } else {
            foreach($players as $player) {
              $player->teleport($this->plugin->getServer()->getDefaultLevel()->getSafeSpawn());
              $player->sendMessage($this->plugin->prefix . " The game has ended!"); // TODO: make a proper game end message
              $arenas[0][$arena]["gametime"] = 61;
              $arenas[0][$arena]["waittime"] = 31;
              $config->set("arenas", $arenas);
              $config->save();
            }
          }

          foreach($players as $player) {
            if($count >= 2) {
              if($gametime > 0) {
                if($gametime % 60 == 0 && $gametime != 60) {
                  $player->sendMessage($this->plugin->prefix . " §eBuilding ends in §c" . $gametime / 60 . " §eminutes.");
                } elseif($gametime == 60) {
                  $player->sendMessage($this->plugin->prefix . " §eBuilding ends in §c" . $gametime / 60 . " §eminute.");
                } elseif($gametime == 30 or $gametime == 15 or $gametime == 10 or ($gametime > 1 && $gametime <= 5)) {
                  $player->sendTip("§l§eBuilding ends in §c" . $gametime . " §eseconds.");
                } elseif($gametime == 1) {
                  $player->sendTip("§l§eBuilding ends in §c" . $gametime . " §esecond.");
                }
              } else {
                $player->sendMessage($this->plugin->prefix . " §aVoting has started!");
                $arenas[0][$arena]["status"] = "voting";
                $config->set("arenas", $arenas);
                $config->save();

                if($status == "voting") {
                  if(!(isset($this->buildzone))) {
                    $this->buildzone = 1;
                  }
                  $buildzone = $arenas[0][$arena]["buildzone" . $this->buildzone];
                  $x = $buildzone["center"]["x"];
                  $y = $buildzone["center"]["y"];
                  $z = $buildzone["center"]["z"];
                  if($votetime == 5) {
                    $player->teleport(new Vector3($x, $y, $z));
                    if($this->buildzone <= $count) {
                      $this->buildzone++;
                    } else {
                      unset($this->buildzone);
                    }
                  }

                  if($votetime > 0) {
                    $votetime--;
                    $arenas[0][$arena]["votetimer"] = $votetime;
                    $config->set("arenas", $arenas);
                    $config->save();
                  } elseif($votetime == 0) {
                    $player->sendMessage($this->plugin->prefix . " §8DEBUG");
                    $arenas[0][$arena]["votetime"] = 5;
                    $config->set("arenas", $arenas);
                    $config->save();
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
