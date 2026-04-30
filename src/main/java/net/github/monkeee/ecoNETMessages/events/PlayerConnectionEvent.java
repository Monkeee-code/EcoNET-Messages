package net.github.monkeee.ecoNETMessages.events;

import net.github.monkeee.ecoNETMessages.Database;
import net.github.monkeee.ecoNETMessages.EcoNETMessages;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectionEvent implements Listener {

    @EventHandler
    public void onConnect(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        Database db = EcoNETMessages.getInstance().getDatabase();
        db.createPlayerToggles(player);
    }
}
