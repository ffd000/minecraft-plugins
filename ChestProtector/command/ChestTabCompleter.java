package net.fricktastic.chestprotector.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ChestTabCompleter implements TabCompleter
{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> variants = new ArrayList<>();

        if (!(sender instanceof Player)) return variants;

        switch (args.length) {
            case 1:
                variants.add("inspect");
                variants.add("add");
                variants.add("remove");
                variants.add("setowner");
                variants.add("setflag");
                variants.add("setprivate");
                variants.add("setpublic");
                variants.add("addgroup");
                variants.add("removegroup");

                if (sender.isOp() || sender.hasPermission("chestprotector.admin")) {
                    variants.add("reload");
                    variants.add("delete");
                    variants.add("adminhelp");
                    variants.add("notify");
                }

                break;
            case 2:
                switch (args[0].toLowerCase()) {
                    case "setowner":
                    case "remove":
                    case "add": variants = null; break;
                    case "setflag": variants.add("redstone"); break;
                    case "notify":
                        variants.add("on");
                        variants.add("off");
                        break;
                }
                break;
            case 3:
                switch (args[1].toLowerCase()) {
                    case "redstone":
                        variants.add("true");
                        variants.add("false");

                        break;
                }
                break;
        }
        if (variants != null) variants = adopt(args[args.length-1], variants);

        return variants;
    }

    static List<String> adopt(String last, List<String> variants)
    {
        List<String> variantsList = new ArrayList<>(variants);
        for (String variant : variantsList) if (!variant.startsWith(last)) variants.remove(variant);

        return variants;
    }
}
