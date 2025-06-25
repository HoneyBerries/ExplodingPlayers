package me.honeyberries.explodingPlayers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class EntityListener implements Listener {



    @EventHandler
    //when an entity gets right-clicked
    public void onEntityRightClick(PlayerInteractAtEntityEvent event) {

        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();


        if (entity instanceof Player hostage) {

            //check if the hostage is in the death row
            if (ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers().contains(hostage.getUniqueId().toString())) {
                if (player.hasPermission("explodingPlayers.explode.use")) {
                    //torture the hostage
                    ExplodingPlayers plugin = ExplodingPlayers.getInstance();
                    hostage.getScheduler().execute(plugin, () -> {
                        // We are now on the entity's region thread, get current location again if needed,
                        // or use the location captured when the event fired if that's more appropriate.
                        // For an immediate explosion, the captured event location for the explosion itself is fine.
                        hostage.getWorld().createExplosion(hostage.getLocation(), ExplodingPlayersSettings.getInstance().getExplosionPower());
                    }, null, 0L);
                }

                else {

                    player.sendMessage("You can't randomly make people explode!");
                }
            }

        }

    }

}
