package com.nauxi;

import io.lumine.mythic.lib.api.util.SmartGive;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private final UpdateItemsPlugin plugin;

    public PlayerJoinListener(UpdateItemsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        List<ItemUpdateRequest> updates = plugin.getUpdateQueue().get(uuid);
        if (updates == null || updates.isEmpty()) return;

        int total = 0;
        for (ItemUpdateRequest req : updates) {
            ItemStack updated = MMOItems.plugin.getItem(MMOItems.plugin.getTypes().get(req.type), req.id);
            if (updated != null) {
                new SmartGive(player).give(updated);
                player.sendMessage(ChatColor.YELLOW + "[UpdateItems] Vật phẩm " + req.type + ":" + req.id + " đã được cập nhật khi bạn đăng nhập.");
                total++;
            }
        }

        plugin.getUpdateQueue().remove(uuid);
        plugin.getUpdateQueue().save();

        if (total > 0) {
            player.sendMessage(ChatColor.GREEN + "Tổng cộng " + total + " vật phẩm được cập nhật.");
        }
    }
}
