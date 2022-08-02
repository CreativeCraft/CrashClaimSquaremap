package org.creativecraft.crashclaimsquaremap;

import net.crashcraft.crashclaim.CrashClaim;
import org.bstats.bukkit.MetricsLite;
import org.creativecraft.crashclaimsquaremap.config.Settings;
import org.creativecraft.crashclaimsquaremap.hook.CrashClaimHook;
import org.creativecraft.crashclaimsquaremap.hook.SquaremapHook;
import org.bukkit.plugin.java.JavaPlugin;

public class CrashClaimSquaremap extends JavaPlugin {
    public static CrashClaimSquaremap plugin;
    private Settings settings;
    private SquaremapHook squaremapHook;
    private CrashClaim crashClaim;
    private CrashClaimHook crashClaimHook;

    @Override
    public void onEnable() {
        plugin = this;
        crashClaim = CrashClaim.getPlugin();

        registerSettings();
        registerHooks();

        new MetricsLite(this, 15709);
    }

    @Override
    public void onDisable() {
        if (squaremapHook != null) {
            squaremapHook.disable();
        }
    }

    /**
     * Register the plugin hooks.
     */
    public void registerHooks() {
        squaremapHook = new SquaremapHook(this);
        crashClaimHook = new CrashClaimHook(this);
    }

    /**
     * Register the plugin config.
     */
    public void registerSettings() {
        settings = new Settings(this);
    }

    /**
     * Retrieve the plugin config.
     *
     * @return Config
     */
    public Settings getSettings() {
        return settings;
    }

    /**
     * Retrieve the CrashClaim instance.
     *
     * @return CrashClaim
     */
    public CrashClaim getCrashClaim() {
        return crashClaim;
    }

    /**
     * Retrieve the CrashClaim hook instance.
     *
     * @return CrashClaimHook
     */
    public CrashClaimHook getCrashClaimHook() {
        return crashClaimHook;
    }
}
