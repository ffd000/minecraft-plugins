<?php

namespace minereset;

use pocketmine\utils\Config;

use pocketmine\math\Vector3;

use minereset\SCMineReset;
use minereset\Mine;

class MineManager implements \ArrayAccess, \IteratorAggregate, \Countable {
    /** @var SCMineReset */
    private $main;

    private $test = 5;


    /** @var Mine[] */
    private $mines;

    public function __construct(SCMineReset $main) {
        $this->main = $main;
        $this->mines = $this->loadMines();
    }

    public function mineToData(Mine $mine) : array {
        return [
            $mine->getPos1()->getX(),
            $mine->getPos2()->getX(),
            $mine->getPos1()->getY(),
            $mine->getPos2()->getY(),
            $mine->getPos1()->getZ(),
            $mine->getPos2()->getZ(),
            $mine->getLevelName(),
            $mine->getResetInterval(),
            $mine->getData()
        ];
    }

    public function dataToMine(string $name, array $data) : Mine {
        return new Mine($this->main,
            $this,
            new Vector3($data[0], $data[1], $data[2]),
            new Vector3($data[3], $data[4], $data[5]),
            $data[6],
            $name,
            (is_array($data[8]) ? $data[8] : [])
        );
    }

    public function getMineByName(string $offset) : Mine {
        $data = $this->getMineDataByName($offset);
        return $this->dataToMine($offset, $data);
    }

    public function getMinesConfig() : Config {
        return new Config($this->main->getDataFolder() . "mines.yml", Config::YAML);
    }

    public function loadMines() {
        $mines = [];
        foreach($this->getMinesConfig()->getAll() as $name => $data)
            $mines[$name] = $this->dataToMine($name, $data);
    }

    // \ArrayAccess Methods //

    public function offsetGet($offset) {
        return $this->mines[$offset];
    }

    public function offsetSet($offset, $value) {
        if($value instanceof Mine && $value->getName() === $offset) {
            if(isset($this->mines[$offset]))
                unset($this->mines[$offset]);
            $this->mines[$offset] = $value;
            $config = $this->getMinesConfig();
            $config->set($offset, $this->mineToData($value));
            $config->save();
            echo "data added to config\n";
        } else {
            throw new \RuntimeException("Invalid offset or mine data.");
        }
    }

    public function offsetExists($offset) {
        return isset($this->mines[$offset]);
    }

    public function offsetUnset($offset) {
        unset($this->mines[$offset]);
    }

    // \IteratorAggregate Methods //

    public function getIterator() {
        return new \ArrayIterator($this->mines);
    }

    // \Countable Methods //

    public function count() {
        return count($this->mines);
    }
}

?>
