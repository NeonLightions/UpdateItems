package com.nauxi;

import io.lumine.mythic.lib.api.item.ItemTag;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.inventory.ItemStack;

public class UpdateItemsPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("UpdateItemsPlugin enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("update") || args.length != 2) {
            sender.sendMessage("§cDùng: /update <type> <id>");
            return true;
        }

        String typeName = args[0].toUpperCase();
        String itemId = args[1];

        Type type = Type.get(typeName);
        if (type == null) {
            sender.sendMessage("§cLoại item không hợp lệ: " + typeName);
            return true;
        }

        ItemStack newItem = MMOItems.plugin.getMMOItem(type, itemId).newBuilder().build();

        // Online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            updateAllInventories(player, typeName, itemId, newItem);
        }

        // Offline players (chỉ có thể cập nhật khi họ vào lại)
        for (OfflinePlayer offline : Bukkit.getOfflinePlayers()) {
            if (!offline.isOnline()) continue;
            updateAllInventories(offline.getPlayer(), typeName, itemId, newItem);
        }

        sender.sendMessage("§aĐã cập nhật item cho tất cả người chơi.");
        return true;
    }

    private void updateAllInventories(Player player, String typeName, String itemId, ItemStack newItem) {
        // Update inventory chính (hotbar + 27 slot)
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            contents[i] = maybeReplace(contents[i], typeName, itemId, newItem);
        }
        player.getInventory().setContents(contents);

        // Update armor slots
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (int i = 0; i < armor.length; i++) {
            armor[i] = maybeReplace(armor[i], typeName, itemId, newItem);
        }
        player.getInventory().setArmorContents(armor);

        // Update offhand (tay phụ)
        ItemStack offhand = player.getInventory().getItemInOffHand();
        player.getInventory().setItemInOffHand(maybeReplace(offhand, typeName, itemId, newItem));

        // Update ender chest
        ItemStack[] ender = player.getEnderChest().getContents();
        for (int i = 0; i < ender.length; i++) {
            ender[i] = maybeReplace(ender[i], typeName, itemId, newItem);
        }
        player.getEnderChest().setContents(ender);

        // Apply changes
        player.updateInventory();
    }

    private ItemStack maybeReplace(ItemStack item, String typeName, String itemId, ItemStack newItem) {
        if (item == null || item.getType().isAir()) return item;

        NBTItem nbt = NBTItem.get(item);
        if (!nbt.hasTag("MMOITEMS_ITEM_ID") || !nbt.hasTag("MMOITEMS_ITEM_TYPE")) return item;

        String currentType = nbt.getString("MMOITEMS_ITEM_TYPE");
        String currentId = nbt.getString("MMOITEMS_ITEM_ID");

        if (currentType.equalsIgnoreCase(typeName) && currentId.equalsIgnoreCase(itemId)) {
            return newItem.clone();
        }

        return item;
    }

}
