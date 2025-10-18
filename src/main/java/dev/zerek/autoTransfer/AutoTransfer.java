package dev.zerek.autoTransfer;

import dev.zerek.autoTransfer.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoTransfer extends JavaPlugin {

    private String joinMessage; // x
    private String preTransferTitle; // y
    private String transferCommandTemplate;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        // Cache config values at load time
        this.joinMessage = getConfig().getString("join-message");
        this.preTransferTitle = getConfig().getString("pre-transfer-message");
        this.transferCommandTemplate = getConfig().getString("transfer-command");

        // Register event listeners in separate class
        this.getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Getters for cached values
    public String getJoinMessage() { return joinMessage; }
    public String getPreTransferTitle() { return preTransferTitle; }
    public String getTransferCommandTemplate() { return transferCommandTemplate; }
}
