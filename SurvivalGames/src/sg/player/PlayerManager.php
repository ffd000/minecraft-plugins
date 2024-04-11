<?php namespace sg\player;

/* SurvivalGames classes */
use sg\Main;

class PlayerManager {

    private $plugin;
    private $data = [];

    public function __construct(Main $plugin, array $data) {
        $this->plugin = $plugin;
        $this->data = $data;
    }

    public function getKills() : int {
        return $this->data["kills"];
    }

    public function getDeaths() : int {
        return $this->data["deaths"];
    }

    public function getKit(string $kit) {
        /*
            TODO : Implement kits that
            players can select to use in
            the arena!
        */
    }
}
