package com.badbones69.crazyenvoys;

import com.badbones69.crazyenvoys.commands.EnvoyCommand;
import com.badbones69.crazyenvoys.api.CrazyManager;
import com.badbones69.crazyenvoys.api.FileManager;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent;
import com.badbones69.crazyenvoys.api.events.EnvoyEndEvent.EnvoyEndReason;
import com.badbones69.crazyenvoys.api.objects.CoolDownSettings;
import com.badbones69.crazyenvoys.api.objects.EditorSettings;
import com.badbones69.crazyenvoys.api.objects.FlareSettings;
import com.badbones69.crazyenvoys.api.objects.LocationSettings;
import com.badbones69.crazyenvoys.commands.EnvoyTab;
import com.badbones69.crazyenvoys.listeners.EnvoyEditListener;
import com.badbones69.crazyenvoys.listeners.EnvoyClickListener;
import com.badbones69.crazyenvoys.listeners.FireworkDamageListener;
import com.badbones69.crazyenvoys.listeners.FlareClickListener;
import com.badbones69.crazyenvoys.support.placeholders.PlaceholderAPISupport;
import com.ryderbelserion.vital.paper.VitalPaper;
import com.ryderbelserion.vital.paper.enums.Support;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazyenvoys.api.plugin.CrazyHandler;
import us.crazycrew.crazyenvoys.common.config.types.ConfigKeys;
import com.badbones69.crazyenvoys.support.MetricsWrapper;

public class CrazyEnvoys extends JavaPlugin {

    @NotNull
    public static CrazyEnvoys get() {
        return JavaPlugin.getPlugin(CrazyEnvoys.class);
    }

    private Methods methods;

    // Envoy Required Classes.
    private EditorSettings editorSettings;
    private FlareSettings flareSettings;
    private CoolDownSettings coolDownSettings;
    private LocationSettings locationSettings;

    private CrazyManager crazyManager;

    private CrazyHandler crazyHandler;

    @Override
    public void onEnable() {
        new VitalPaper(this).setLogging(false);

        new MetricsWrapper(this, 4514);

        this.crazyHandler = new CrazyHandler(getDataFolder());
        this.crazyHandler.install();

        this.methods = new Methods();

        this.locationSettings = new LocationSettings();
        this.editorSettings = new EditorSettings();
        this.coolDownSettings = new CoolDownSettings();
        this.flareSettings = new FlareSettings();

        this.crazyManager = new CrazyManager();

        this.crazyManager.load();

        enable();
    }

    @Override
    public void onDisable() {
        this.crazyHandler.uninstall();

        for (Player player : getServer().getOnlinePlayers()) {
            if (this.editorSettings.isEditor(player)) {
                this.editorSettings.removeEditor(player);
                this.editorSettings.removeFakeBlocks();
            }
        }

        if (this.crazyManager.isEnvoyActive()) {
            EnvoyEndEvent event = new EnvoyEndEvent(EnvoyEndReason.SHUTDOWN);

            getServer().getPluginManager().callEvent(event);

            this.crazyManager.endEnvoyEvent();
        }

        this.crazyManager.reload(true);
    }

    public @NotNull CrazyHandler getCrazyHandler() {
        return this.crazyHandler;
    }

    public boolean isLogging() {
        return this.crazyHandler.getConfigManager().getConfig().getProperty(ConfigKeys.verbose_logging);
    }

    private void enable() {
        getServer().getPluginManager().registerEvents(new EnvoyEditListener(), this);
        getServer().getPluginManager().registerEvents(new EnvoyClickListener(), this);
        getServer().getPluginManager().registerEvents(new FlareClickListener(), this);
        getServer().getPluginManager().registerEvents(new FireworkDamageListener(), this);

        if (Support.placeholder_api.isEnabled()) {
            new PlaceholderAPISupport().register();
        }

        registerCommand(getCommand("crazyenvoys"), new EnvoyTab(), new EnvoyCommand());
    }

    private void registerCommand(PluginCommand pluginCommand, TabCompleter tabCompleter, CommandExecutor commandExecutor) {
        if (pluginCommand != null) {
            pluginCommand.setExecutor(commandExecutor);

            if (tabCompleter != null) pluginCommand.setTabCompleter(tabCompleter);
        }
    }

    public FileManager getFileManager() {
        return this.crazyHandler.getFileManager();
    }

    public Methods getMethods() {
        return this.methods;
    }

    // Envoy Required Classes.
    public EditorSettings getEditorSettings() {
        return this.editorSettings;
    }

    public FlareSettings getFlareSettings() {
        return this.flareSettings;
    }

    public CoolDownSettings getCoolDownSettings() {
        return this.coolDownSettings;
    }

    public LocationSettings getLocationSettings() {
        return this.locationSettings;
    }

    public CrazyManager getCrazyManager() {
        return this.crazyManager;
    }
}