package net.github.monkeee.ecoNETMessages;

import net.github.monkeee.ecoNETMessages.commands.CommunicationBlock;
import net.github.monkeee.ecoNETMessages.commands.CommunicationToggle;
import net.github.monkeee.ecoNETMessages.commands.CommunicationUnblock;
import net.github.monkeee.ecoNETMessages.commands.MessageCommand;
import net.github.monkeee.ecoNETMessages.events.PlayerConnectionEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class EcoNETMessages extends JavaPlugin {

    private static EcoNETMessages instance;
    private Database database;


    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("Loading...");
        saveResource("storage.db", false);
        database = new Database(this);
        database.connect();

        getServer().getPluginManager().registerEvents(new PlayerConnectionEvent(), this);

        Objects.requireNonNull(getCommand("message")).setExecutor(new MessageCommand());
        Objects.requireNonNull(getCommand("message")).setTabCompleter(new MessageCommand());

        Objects.requireNonNull(getCommand("communicationtoggle")).setExecutor(new CommunicationToggle());

        Objects.requireNonNull(getCommand("communicationblock")).setExecutor(new CommunicationBlock());
        Objects.requireNonNull(getCommand("communicationblock")).setTabCompleter(new CommunicationBlock());

        Objects.requireNonNull(getCommand("communicationunblock")).setExecutor(new CommunicationUnblock());
        Objects.requireNonNull(getCommand("communicationunblock")).setTabCompleter(new CommunicationUnblock());
    }

    @Override
    public void onDisable() {
        getLogger().info("Shutting down...");
        database.disconnect();
    }

    public Database getDatabase() { return database; }

    public static EcoNETMessages getInstance() { return instance; }

    public static List<String> GetBetterList(List<String> list, String[] args, int argStage) {
        if (argStage >= args.length) return List.of();
        List<String> completions = null;
        String input = args[argStage].toLowerCase();
        for (String s : list) {
            if (s.toLowerCase().startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }
        if (completions != null) Collections.sort(completions);
        return completions != null ? completions : List.of();
    }
}
