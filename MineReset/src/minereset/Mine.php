<?php

namespace minereset;

use pocketmine\scheduler\PluginTask;

use pocketmine\math\Vector3;

use pocketmine\utils\Config;

use pocketmine\level\Level;
use pocketmine\level\Position;
use pocketmine\level\format\Chunk;

use minereset\MineManager;
use minereset\ResetMineTask;

class Mine extends PluginTask {
    private $pos1;
    private $pos2;
    private $level;
    private $data;
    private $name;
    private $resetInterval;

    /** @var MineManager $mm */
    private $mm;

    /** @var SCMineReset $mm */
    private $main;

    private $isResetting;

    public function __construct(SCMineReset $main, MineManager $mm, Vector3 $pos1, Vector3 $pos2, $level, string $name, $data = [], $resetInterval = 0) {
        parent::__construct($main);
        $this->pos1 = $pos1;
        $this->pos2 = $pos2;
        $this->level = $level;
        $this->data = $data;
        $this->name = $name;
        $this->resetInterval = $resetInterval;
        $this->mm = $mm;
        $this->main = $main;
        $this->isResetting = false;
        $this->register();
    }

    public function onRun($tick) {
        $this->reset();
    }

    private function register() {
        if($this->getHandler() === null && $this->resetInterval > 0) {
            $this->main->getServer()->getScheduler()->scheduleRepeatingTask($this, 20 * $this->resetInterval);
            $this->setData($this->data);
            echo "Task registered\n";
            if($this->getLevel() === null)
                $this->main->getServer()->loadLevel($this->level);
        }
    }

    public function unregister() {
        if($this->getHandler() !== null)
            $this->main->getServer()->getScheduler()->cancelTask($this->getTaskId());
    }


    public function reset() : bool {
        if(!$this->isResetting() && $this->getLevel() !== null) {
            $this->isResetting = true;
            $chunks = [];
            $chunkClass = Chunk::class;
            for($x = $this->getPos1()->getX(); ($x - 16) <= $this->getPos2()->getX(); $x += 16)
                for($z = $this->getPos1()->getZ(); ($z - 16) <= $this->getPos2()->getZ(); $z += 16) {
                    $chunk = $this->getLevel()->getChunk($x >> 4, $z >> 4, true);
                    $chunkClass = get_class($chunk);
                    $chunks[Level::chunkHash($x >> 4, $z >> 4)] = $chunk->fastSerialize();
                }
            $resetTask = new ResetMineTask($this->getName(), $chunks, $this->getPos1(), $this->getPos2(), $this->data, $this->getLevel()->getId(), $chunkClass);
            $this->main->getServer()->getScheduler()->scheduleAsyncTask($resetTask);
            echo "Async task registered\n";
            return true;
        }
        echo "reset() executed\n";
        echo "isResetting: ";
        var_dump($this->isResetting());
        return false;
    }

    public function getPos1() : Vector3 {
        return $this->pos1;
    }

    public function getPos2() : Vector3 {
        return $this->pos2;
    }

    public function getLevel() {
        return $this->main->getServer()->getLevelByName($this->level);
    }

    public function getLevelName() : string {
        return $this->level;
    }

    public function getData() : array {
        return $this->data;
    }

    public function setData(array $data) {
        $this->data = $data;
        $this->mm->offsetSet($this->getName(), $this);
    }

    public function getName() : string {
        return $this->name;
    }

    public function isResetting() : bool {
        return $this->isResetting;
    }

    public function doneReset() {
        $this->isResetting = false;
    }

    public function getResetInterval() : int {
        return $this->resetInterval;
    }

    public function setResetInterval(int $resetInterval) {
        $this->resetInterval = $resetInterval;
        $this->unregister();
        $this->register();
    }
}
?>
