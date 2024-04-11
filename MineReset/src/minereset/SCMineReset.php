<?php

namespace minereset;

use pocketmine\plugin\PluginBase;

use pocketmine\math\Vector3;

use minereset\Mine;
use minereset\MineManager;

class SCMineReset extends PluginBase {
    /** @var MineManager $mm */
    private $mm;

    public function onEnable() {
        @mkdir($this->getDataFolder());
        $this->mm = new MineManager($this);
        $this->initTest();
    }

    private function initTest() {
        $this->createTestMine(100, 100, 100, 125, 120, 125, "mineA", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(126, 100, 126, 151, 120, 151, "mineB", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(152, 100, 152, 176, 120, 176, "mineC", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(177, 100, 177, 202, 120, 202, "mineD", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(203, 100, 203, 228, 120, 228, "mineE", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(229, 100, 229, 254, 120, 254, "mineF", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(255, 100, 255, 280, 120, 280, "mineG", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(281, 100, 281, 306, 120, 306, "mineH", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(307, 100, 307, 332, 120, 332, "mineI", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->createTestMine(333, 100, 333, 358, 120, 358, "mineJ", [1 => "20", 2 => "20", 3 => "20", 4 => "20", 5 => "20"]);
        $this->getLogger()->info("Mines registered.");
    }

    private function createTestMine($x1, $y1, $z1, $x2, $y2, $z2, $name, $data) {
        $pos1 = new Vector3($x1, $y1, $z1);
        $pos2 = new Vector3($x2, $y2, $z2);
        $level = "world";
        $mine = new Mine($this, $this->mm, $pos1, $pos2, $level, $name, $data, (60 * 5));
    }

    public function getMineManager() {
        return $this->mm;
    }

    public function notifyComplete(string $name) {
        echo "--------------\n";
        echo "Mine: $name\n";
        echo "Mine reset complete\n";
        echo "--------------\n";
    }

    public function notifyProgress($progress) {
        echo "Progress: $progress\n";
    }
}

?>
