package net.fricktastic.chestprotector;

import org.bukkit.block.Chest;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChestRegenerationTask extends BukkitRunnable
{
    private final ChestProtector plugin;

    static List<Chest> chests = new ArrayList<>();

    ChestRegenerationTask(ChestProtector plugin)
    {
        this.plugin = plugin;
    }

    public void run()
    {
        if (chests.isEmpty()) return;

        for (Iterator<Chest> it = chests.iterator(); it.hasNext();) {
            Chest chest = it.next();

            float health = chest.getMetadata("health").get(0).asFloat();

            if (health < chest.getMetadata("maxHealth").get(0).asFloat()) {
                chest.setMetadata("health", new FixedMetadataValue(plugin, ++health));
            } else {
                it.remove();
            }
        }
    }

    static void tryAddChest(Chest chest)
    {
        if (!chests.contains(chest)) {
            chests.add(chest);
        }
    }
}
