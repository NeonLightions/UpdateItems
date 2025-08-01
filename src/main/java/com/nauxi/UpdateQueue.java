package com.nauxi;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UpdateQueue {
    private final File file;
    private final Map<UUID, List<ItemUpdateRequest>> queue = new HashMap<>();

    public UpdateQueue(File file) {
        this.file = file;
    }

    public void load() {
        if (!file.exists()) return;
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        for (String key : config.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            List<String> list = config.getStringList(key);
            List<ItemUpdateRequest> requests = new ArrayList<>();
            for (String line : list) {
                String[] split = line.split(":");
                if (split.length == 2) {
                    requests.add(new ItemUpdateRequest(split[0], split[1]));
                }
            }
            queue.put(uuid, requests);
        }
    }

    public void save() {
        YamlConfiguration config = new YamlConfiguration();
        for (Map.Entry<UUID, List<ItemUpdateRequest>> entry : queue.entrySet()) {
            List<String> list = new ArrayList<>();
            for (ItemUpdateRequest req : entry.getValue()) {
                list.add(req.type + ":" + req.id);
            }
            config.set(entry.getKey().toString(), list);
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(UUID uuid, String type, String id) {
        queue.computeIfAbsent(uuid, k -> new ArrayList<>()).add(new ItemUpdateRequest(type, id));
    }

    public List<ItemUpdateRequest> get(UUID uuid) {
        return queue.get(uuid);
    }

    public void remove(UUID uuid) {
        queue.remove(uuid);
    }
}
