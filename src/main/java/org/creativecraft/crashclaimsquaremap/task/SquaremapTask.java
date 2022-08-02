package org.creativecraft.crashclaimsquaremap.task;

import org.creativecraft.crashclaimsquaremap.CrashClaimSquaremap;
import net.crashcraft.crashclaim.claimobjects.Claim;
import xyz.jpenilla.squaremap.api.Key;
import xyz.jpenilla.squaremap.api.Point;
import xyz.jpenilla.squaremap.api.SimpleLayerProvider;
import xyz.jpenilla.squaremap.api.marker.Marker;
import xyz.jpenilla.squaremap.api.marker.MarkerOptions;
import xyz.jpenilla.squaremap.api.marker.Rectangle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SquaremapTask extends BukkitRunnable {
    private final UUID world;
    private final SimpleLayerProvider provider;
    private final CrashClaimSquaremap plugin;

    private boolean stop;

    public SquaremapTask(CrashClaimSquaremap plugin, UUID world, SimpleLayerProvider provider) {
        this.plugin = plugin;
        this.world = world;
        this.provider = provider;
    }

    @Override
    public void run() {
        if (stop) {
            cancel();
        }

        updateClaims();
    }

    /**
     * Update the claims.
     */
    private void updateClaims() {
        provider.clearMarkers();

        plugin.getCrashClaimHook().getClaims(this.world)
            .stream()
            .filter(claim -> claim.getWorld().equals(this.world))
            .forEach(this::handleClaim);
    }

    /**
     * Handle the claim on the map.
     *
     * @param claim The claim.
     */
    private void handleClaim(Claim claim) {
        World world = Bukkit.getWorld(claim.getWorld());

        Location min = new Location(world, claim.getMinX(), world.getMinHeight(), claim.getMinZ());
        Location max = new Location(world, claim.getMaxX(), world.getMaxHeight(), claim.getMaxZ());

        Rectangle rect = Marker.rectangle(
            Point.of(min.getBlockX(), min.getBlockZ()),
            Point.of(max.getBlockX() + 1, max.getBlockZ() + 1)
        );

        MarkerOptions.Builder options = MarkerOptions
            .builder()
            .strokeColor(plugin.getSettings().getStrokeColor())
            .strokeWeight(plugin.getSettings().getConfig().getInt("settings.style.stroke.weight"))
            .strokeOpacity(plugin.getSettings().getConfig().getDouble("settings.style.stroke.opacity"))
            .fillColor(plugin.getSettings().getFillColor())
            .fillOpacity(plugin.getSettings().getConfig().getDouble("settings.style.fill.opacity"))
            .clickTooltip(
                plugin.getSettings().getConfig().getString("settings.tooltip.claim")
                    .replace("{world}", world.getName())
                    .replace("{id}", Long.toString(claim.getId()))
                    .replace("{owner}", getPlayerName(claim.getOwner()))
                    .replace("{name}", claim.getName())
                    .replace("{subclaims_amount}", Integer.toString(plugin.getCrashClaimHook().getSubclaimsSize(claim)))
                    .replace("{subclaims}", plugin.getCrashClaimHook().getSubclaims(claim))
                    .replace("{entrymessage}", plugin.getCrashClaimHook().getEntryMessage(claim))
                    .replace("{exitmessage}", plugin.getCrashClaimHook().getExitMessage(claim))
            );

        rect.markerOptions(options);

        String markerId = "crashclaim_" + world.getName() + "_region_" + Long.toHexString(claim.getId());

        this.provider.addMarker(Key.of(markerId), rect);
    }

    /**
     * Retrieve the player's name.
     *
     * @param uuid The player's UUID.
     * @return String
     */
    private static String getPlayerName(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid).getName();
    }

    /**
     * Disable the task.
     */
    public void disable() {
        cancel();
        this.stop = true;
        this.provider.clearMarkers();
    }
}
