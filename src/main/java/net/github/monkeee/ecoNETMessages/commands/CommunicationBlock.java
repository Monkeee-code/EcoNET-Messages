package net.github.monkeee.ecoNETMessages.commands;

import net.github.monkeee.ecoNETMessages.Database;
import net.github.monkeee.ecoNETMessages.EcoNETMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CommunicationBlock implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command!");
            return true;
        }
        if (!player.hasPermission("econet.message.block")) {
            player.sendMessage(Component.text("You do not have the permission to use this!", NamedTextColor.RED));
            return true;
        }
        Database db = EcoNETMessages.getInstance().getDatabase();
        if (args.length == 0) {
            player.sendMessage(Component.text("Please, provide a player to block!").color(NamedTextColor.RED));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || target == player) {
            player.sendMessage(Component.text("Provide a valid player (player is either offline or its yourself"));
            return false;
        }
        if (db.isBlocked(target.getUniqueId(), player.getUniqueId())) {
            player.sendMessage(Component.text("Player "+target.getName()+" has already been blocked by you!").color(NamedTextColor.RED));
            return true;
        }
        db.setBlocked(target, player, true);
        player.sendMessage(Component.text("Player ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text(target.getName(), Style.style(TextDecoration.BOLD)).color(NamedTextColor.WHITE))
                .append(Component.text(" has been ", NamedTextColor.DARK_GRAY))
                .append(Component.text("Blocked!", NamedTextColor.RED)));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        List<String> playerNames = new ArrayList<>();
        if (!(sender instanceof Player p)) return new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerNames.add(player.getName());
        }
        playerNames.remove(p.getName());
        if (args.length == 1) return EcoNETMessages.GetBetterList(playerNames, args, 0);
        return new ArrayList<>();
    }
}
