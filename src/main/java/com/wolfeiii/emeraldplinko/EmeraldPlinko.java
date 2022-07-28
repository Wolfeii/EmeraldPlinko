package com.wolfeiii.emeraldplinko;

import com.wolfeiii.emeraldplinko.command.CommandHandler;
import com.wolfeiii.emeraldplinko.command.PlinkoCommand;
import com.wolfeiii.emeraldplinko.configuration.Setting;
import com.wolfeiii.emeraldplinko.data.SchematicHandler;
import com.wolfeiii.emeraldplinko.economy.EconomyHandler;
import com.wolfeiii.emeraldplinko.game.PlinkoGame;
import com.wolfeiii.emeraldplinko.game.PlinkoGameHandler;
import com.wolfeiii.emeraldplinko.game.options.SideOptionLoader;
import com.wolfeiii.emeraldplinko.game.win.WinPoolHandler;
import com.wolfeiii.emeraldplinko.listener.InventoryListener;
import com.wolfeiii.emeraldplinko.listener.PlinkoGameListener;
import com.wolfeiii.emeraldplinko.listener.SideOptionListener;
import com.wolfeiii.emeraldplinko.map.MapLoader;
import com.wolfeiii.emeraldplinko.worldedit.WorldEditHook;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

@Getter
public final class EmeraldPlinko extends JavaPlugin implements Listener {

    private static EmeraldPlinko instance;

    private EconomyHandler economyHandler;
    private CommandHandler commandHandler;
    private SchematicHandler schematicHandler;
    private PlinkoGameHandler gameHandler;
    private SideOptionLoader sideOptionLoader;
    private WinPoolHandler winPoolHandler;

    private MapLoader mapLoader;
    private WorldEditHook worldEditHook;

    public EmeraldPlinko() {
        instance = this;
    }

    public static EmeraldPlinko getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        reloadConfig();

        // Create WorldEdit hook
        loadWorldEditHook();

        this.mapLoader = new MapLoader(this);
        this.commandHandler = new CommandHandler();
        this.sideOptionLoader = new SideOptionLoader(this);
        this.schematicHandler = new SchematicHandler(this);
        this.winPoolHandler = new WinPoolHandler(this);
        this.economyHandler = new EconomyHandler();

        this.winPoolHandler.loadWinPools();
        this.schematicHandler.loadAllSchematics();
        this.sideOptionLoader.loadSideOptions();

        if (!economyHandler.setupEconomy(this)) {
            getLogger().severe(() -> "Disabling plugin because Economy Handler is not installed.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Create Game Handler
        this.gameHandler = loadMapFactory();

        getCommand("plinko").setExecutor(new PlinkoCommand(this));
        registerListeners(new PlinkoGameListener(this), new InventoryListener(), new SideOptionListener(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getGameHandler().getGames().forEach(PlinkoGame::unregister);
    }

    private PlinkoGameHandler loadMapFactory() {
        return new PlinkoGameHandler(this, worldEditHook.createMapFactoryCompat());
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners)
                .forEach(listener ->
                getServer().getPluginManager().registerEvents(listener, this));
    }

    @SuppressWarnings("unchecked")
    private void loadWorldEditHook() {
        Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin == null || !worldEditPlugin.isEnabled()) {
            return;
        }

        try {
            this.worldEditHook = (WorldEditHook) Class.forName("com.wolfeiii.emeraldplinko.worldedit.implementations.ModernWEHook")
                    .getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            exception.printStackTrace();
        }
    }

    public String getStringSetting(Setting setting) {
        return getConfig().getString(setting.getPath());
    }

    public Integer getIntegerSetting(Setting setting) {
        return getConfig().getInt(setting.getPath());
    }
}
