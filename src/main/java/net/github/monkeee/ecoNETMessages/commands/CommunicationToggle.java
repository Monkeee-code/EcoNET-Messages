package net.github.monkeee.ecoNETMessages.commands;

import net.github.monkeee.ecoNETMessages.Database;
import net.github.monkeee.ecoNETMessages.EcoNETMessages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommunicationToggle implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Database db = EcoNETMessages.getInstance().getDatabase();
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can run this!");
            return true;
        }
        if (!player.hasPermission("econet.message.toggle")) {
            player.sendMessage(Component.text("You do not have permission to use this!", NamedTextColor.RED));
            return true;
        }

        boolean currentState = db.getToggle(player);
        Component msg = (currentState) ? Component.text("You have ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("Disabled ").color(NamedTextColor.RED)
                .append(Component.text("private messages!").color(NamedTextColor.DARK_GRAY))) :
                Component.text("You have ").color(NamedTextColor.DARK_GRAY)
                .append(Component.text("Enabled ").color(NamedTextColor.GREEN)
                .append(Component.text("private messages!").color(NamedTextColor.DARK_GRAY)));
        db.setToggle(player, !currentState);
        player.sendMessage(msg);
        return false;
    }
}
