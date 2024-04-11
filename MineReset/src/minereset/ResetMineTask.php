<?php
namespace minereset;

use pocketmine\level\format\Chunk;
use pocketmine\level\Level;

use pocketmine\math\Vector3;

use pocketmine\scheduler\AsyncTask;

use pocketmine\Server;

use minereset\SCMineReset;

class ResetMineTask extends AsyncTask {
    /** @var string $name */
    private $name;
    /** @var string $chunks */
    private $chunks;
    /** @var Vector3 $pos1 */
    private $pos1;
    /** @var Vector3 $pos2 */
    private $pos2;
    /** @var string $ratioData */
    private $ratioData;
    /** @var int $levelId */
    private $levelId;
    /** @var Chunk $chunkClass */
    private $chunkClass;

    private $time;

    public function __construct(string $name, array $chunks, Vector3 $pos1, Vector3 $pos2, array $data, $levelId, $chunkClass){
        $this->name = $name;
        $this->chunks = serialize($chunks);
        $this->pos1 = $pos1;
        $this->pos2 = $pos2;
        $this->ratioData = serialize($data);
        $this->levelId = $levelId;
        $this->chunkClass = $chunkClass;
    }

    public function onRun() {
        $this->time = microtime(true);
        $chunkClass = $this->chunkClass;
        /** @var Chunk[] $chunks */
        $chunks = unserialize($this->chunks);
        foreach($chunks as $hash => $binary)
            $chunks[$hash] = $chunkClass::fastDeserialize($binary);
        $sum = [];
        $id = array_keys(unserialize($this->ratioData));
        for($i = 0; $i < count($id); $i++){
            $blockId = explode(":", $id[$i]);
            if(!isset($blockId[1])){
                $blockId[1] = 0;
            }
            $id[$i] = $blockId;
        }
        $m = array_values(unserialize($this->ratioData));
        $sum[0] = $m[0];
        for($l = 1; $l < count($m); $l++)
            $sum[$l] = $sum[$l - 1] + $m[$l];

        $totalBlocks = ($this->pos2->x - $this->pos1->x + 1) *
                       ($this->pos2->y - $this->pos1->y + 1) *
                       ($this->pos2->z - $this->pos1->z + 1);
        $interval = $totalBlocks / 8;
        $lastUpdate = 0;
        $currentBlocks = 0;
        for($x = $this->pos1->getX(); $x <= $this->pos2->getX(); $x++)
            for($y = $this->pos1->getY(); $y <= $this->pos2->getY(); $y++)
                for($z = $this->pos1->getZ(); $z <= $this->pos2->getZ(); $z++) {
                    $a = rand(0, end($sum));
                    for($l = 0; $l < count($sum); $l++) {
                        if($a <= $sum[$l]) {
                            $hash = Level::chunkHash($x >> 4, $z >> 4);
                            if(isset($chunks[$hash])) {
                                $chunks[$hash]->setBlock($x & 0x0f, $y & 0x7f, $z & 0x0f, $id[$l][0] & 0xff, $id[$l][1] & 0xff);
                                $currentBlocks++;
                                if($lastUpdate + $interval <= $currentBlocks){
                                    if(method_exists($this, 'publishProgress')) {
                                        $this->publishProgress(round(($currentBlocks / $totalBlocks) * 100) . "%");
                                    }
                                    $lastUpdate = $currentBlocks;
                                }
                            }
                            $l = count($sum);
                        }
                    }
                }
        $this->setResult($chunks);
    }

    public function onCompletion(Server $server) {
        $chunks = $this->getResult();
        $plugin = $server->getPluginManager()->getPlugin("SCMineReset");
        if($plugin instanceof SCMineReset and $plugin->isEnabled()) {
            $level = $server->getLevel($this->levelId);
            if($level instanceof Level) {
                foreach($chunks as $hash => $chunk) {
                    Level::getXZ($hash, $x, $z);
                    $level->setChunk($x, $z, $chunk, true);
                }
            }
            $plugin->notifyComplete($this->name);
            echo "Took: " . round((microtime(true) - $this->time), 2) . "s\n";
            //$plugin->clearMine($this->name);
        }
        //echo "onCompletion() exited\n";
    }

    public function onProgressUpdate(Server $server, $progress){
        $plugin = $server->getPluginManager()->getPlugin("SCMineReset");
        if($plugin instanceof SCMineReset and $plugin->isEnabled())
            $plugin->notifyProgress($progress);
    }
}

?>
