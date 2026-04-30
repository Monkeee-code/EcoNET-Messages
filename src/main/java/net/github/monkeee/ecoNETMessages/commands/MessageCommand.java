package net.github.monkeee.ecoNETMessages.commands;

import net.github.monkeee.ecoNETMessages.Database;
import net.github.monkeee.ecoNETMessages.EcoNETMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class MessageCommand implements CommandExecutor, TabCompleter
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Database db = EcoNETMessages.getInstance().getDatabase();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command!");
            return false;
        }
        if (!player.hasPermission("econet.message.msg")) {
            player.sendMessage(Component.text("You do not have the permission to run this command!", NamedTextColor.RED));
            return true;
        }
        if (args.length < 2) {
            player.sendMessage(Component.text("Please, send a valid message and a valid user!").color(NamedTextColor.RED));
            return true;
        }
        Player receiver = Bukkit.getPlayer(args[0]);
        if (receiver == null) {
            sender.sendMessage(Component.text("Please mention a valid player!").color(NamedTextColor.RED));
            return true;
        }
        if (receiver == player) {
            player.sendMessage(Component.text("You cannot message yourself!").color(NamedTextColor.RED));
            return true;
        }
        UUID uuidPlayer = player.getUniqueId();
        UUID uuidReceiver = receiver.getUniqueId();

        if (!db.getToggle(player)) {
            player.sendMessage(Component.text("You have Messages disabled!\nPlease use ").color(NamedTextColor.RED)
                    .append(Component.text("/ctoggle ").color(NamedTextColor.YELLOW))
                    .append(Component.text("to toggle it back on!").color(NamedTextColor.RED)));
            return true;
        }
        if (!db.getToggle(receiver)) {
            player.sendMessage(Component.text("The player has toggled off private messages!").color(NamedTextColor.RED));
            return true;
        }
        if (db.isBlocked(uuidPlayer, uuidReceiver)) {
            player.sendMessage(Component.text(receiver.getName()+" ").color(NamedTextColor.RED)
                    .append(Component.text("has you blocked!")).color(NamedTextColor.RED));
            return true;
        }
        if (db.isBlocked(uuidReceiver, uuidPlayer)) {
            player.sendMessage(Component.text("Player ").color(NamedTextColor.RED)
                    .append(Component.text(receiver.getName())).color(NamedTextColor.RED)
                    .append(Component.text(" is being blocked by you!\nUnable to send the message").color(NamedTextColor.RED)));
            return true;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String senderFormat = "&8[&bME &8-> &b"+receiver.getName()+"&8]&f "+message;
        Component senderMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(senderFormat);
        String receiverFormat = "&8[&b"+player.getName()+" &8-> &bME&8]&f "+message;
        Component receiverMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(receiverFormat);
        player.sendMessage(senderMessage);
        receiver.sendMessage(receiverMessage);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player p)) return List.of();
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        players.remove(p.getName());
        if (args.length == 1) return EcoNETMessages.GetBetterList(players, args, 0);
        if (args.length == 2) return List.of("<message>");
        return new ArrayList<>();
    }
}
