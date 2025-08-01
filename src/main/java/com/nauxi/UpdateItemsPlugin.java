package com.nauxi;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class UpdateItemsPlugin extends JavaPlugin {
    private UpdateQueue updateQueue;

    @Override
    public void onEnable() {
        this.updateQueue = new UpdateQueue(new File(getDataFolder(), "queue.yml"));
        this.updateQueue.load();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getCommand("update").setExecutor(new UpdateCommandExecutor(this));
        getCommand("ui").setExecutor(new UpdateCommandExecutor(this));
    }

    public UpdateQueue getUpdateQueue() {
        return updateQueue;
    }
}
