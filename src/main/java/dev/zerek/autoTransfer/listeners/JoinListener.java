package dev.zerek.autoTransfer.listeners;

import dev.zerek.autoTransfer.AutoTransfer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class JoinListener implements Listener {
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final AutoTransfer plugin;
    private final Map<UUID, BukkitTask> transferTasks = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitTask> preTransferTasks = new ConcurrentHashMap<>();

    public JoinListener(AutoTransfer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Send informational TITLE 5 seconds after join
        BukkitTask preTask1 = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            player.showTitle(Title.title(Component.empty(), MINI_MESSAGE.deserialize(plugin.getJoinMessage())));
        }, 20L * 5L);
        preTransferTasks.put(player.getUniqueId(), preTask1);

        // Send pre-transfer TITLE 2 seconds before transfer
        BukkitTask preTask2 = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            player.showTitle(Title.title(Component.empty(), MINI_MESSAGE.deserialize(plugin.getPreTransferTitle())));
        }, 20L * 116L);
        preTransferTasks.put(player.getUniqueId(), preTask2);

        // Schedule transfer for this specific player
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            transferTasks.remove(player.getUniqueId());
            preTransferTasks.remove(player.getUniqueId());
            if (player.isOnline()) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.getTransferCommandTemplate().replace("<player>", player.getName()));
        }, 20L * 120L);
        transferTasks.put(player.getUniqueId(), task);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        BukkitTask task = transferTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
        BukkitTask pre = preTransferTasks.remove(uuid);
        if (pre != null) {
            pre.cancel();
        }
    }
}
