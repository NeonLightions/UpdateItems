package com.nauxi;

import io.lumine.mythic.lib.api.item.NBTItem;
import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class UpdateCommandExecutor implements CommandExecutor {
    private final UpdateItemsPlugin plugin;

    public UpdateCommandExecutor(UpdateItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Dùng: /update <type> <id>");
            return true;
        }

        String type = args[0];
        String id = args[1];
        int updated = 0;

        for (Player target : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < target.getInventory().getSize(); i++) {
                ItemStack item = target.getInventory().getItem(i);
                if (item == null) continue;

                NBTItem nbt = NBTItem.get(item);
                if (!nbt.hasTag("MMOITEMS_ITEM_ID") || !nbt.hasTag("MMOITEMS_ITEM_TYPE")) continue;

                String currentId = nbt.getString("MMOITEMS_ITEM_ID");
                String currentType = nbt.getString("MMOITEMS_ITEM_TYPE");
                if (currentId.equalsIgnoreCase(id) && currentType.equalsIgnoreCase(type)) {
                    ItemStack updatedItem = MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(type), id);
                    if (updatedItem != null) {
                        target.getInventory().setItem(i, updatedItem);
                        updated++;
                        target.sendMessage(ChatColor.YELLOW + "[UpdateItems] Vật phẩm " + type + ":" + id + " đã được cập nhật.");
                    }
                }
            }
        }

        for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (!offline.isOnline()) {
                UUID uuid = offline.getUniqueId();
                plugin.getUpdateQueue().add(uuid, type, id);
            }
        }

        plugin.getUpdateQueue().save();
        sender.sendMessage(ChatColor.GREEN + "Đã cập nhật " + updated + " vật phẩm cho người chơi online.");
        return true;
    }
}
