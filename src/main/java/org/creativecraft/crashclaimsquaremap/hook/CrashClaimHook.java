package org.creativecraft.crashclaimsquaremap.hook;

import net.crashcraft.crashclaim.CrashClaim;
import net.crashcraft.crashclaim.claimobjects.BaseClaim;
import net.crashcraft.crashclaim.claimobjects.Claim;
import net.crashcraft.crashclaim.claimobjects.SubClaim;
import net.crashcraft.crashclaim.config.GlobalConfig;
import net.crashcraft.crashclaim.fastutil.fastutil.longs.Long2ObjectOpenHashMap;
import org.creativecraft.crashclaimsquaremap.CrashClaimSquaremap;

import java.util.*;
import java.util.stream.Collectors;

public class CrashClaimHook {
    private final CrashClaimSquaremap plugin;

    public CrashClaimHook(CrashClaimSquaremap plugin) {
        this.plugin = plugin;
    }

    /**
     * Determine if the world is enabled.
     *
     * @param  uuid The world UUID.
     * @return boolean
     */
    public static boolean isWorldEnabled(UUID uuid) {
        return !GlobalConfig.disabled_worlds.contains(uuid);
    }

    /**
     * Retrieve the claims.
     *
     * @param  uuid The world UUID.
     * @return HashSet
     */
    public HashSet<Claim> getClaims(UUID uuid) {
        HashSet<Claim> claims = new HashSet<>();

        Long2ObjectOpenHashMap<ArrayList<Integer>> claimChunks = plugin.getCrashClaim().getDataManager().getClaimChunkMap(uuid);

        claimChunks.values().forEach(x -> claims.add(
            plugin.getCrashClaim().getApi().getClaim(x.get(0))
        ));

        if (claims.isEmpty()) {
            return null;
        }

        return claims;
    }

    /**
     * Retrieve the subclaim size.
     *
     * @param  claim The claim.
     * @return int
     */
    public int getSubclaimsSize(Claim claim) {
        List<SubClaim> subclaims = claim.getSubClaims();

        if (subclaims == null || subclaims.size() == 0) {
            return 0;
        }

        return subclaims.size();
    }

    /**
     * Retrieve the subclaims.
     *
     * @param  claim The claim.
     * @return String
     */
    public String getSubclaims(Claim claim) {
        List<SubClaim> subclaims = claim.getSubClaims();

        if (subclaims == null || subclaims.size() == 0) {
            return plugin.getSettings().getConfig().getString("settings.tooltip.none");
        }

        return subclaims.stream().map(BaseClaim::getName).collect(Collectors.joining(", "));
    }

    /**
     * Retrieve the claim entry message.
     *
     * @param  claim The claim.
     * @return String
     */
    public String getEntryMessage(Claim claim) {
        String entryMessage = claim.getEntryMessage();
        return entryMessage == null ? plugin.getSettings().getConfig().getString("settings.tooltip.none") : entryMessage;
    }

    /**
     * Retrieve the claim exit message.
     *
     * @param  claim The claim.
     * @return String
     */
    public String getExitMessage(Claim claim) {
        String exitMessage = claim.getExitMessage();
        return exitMessage == null ? plugin.getSettings().getConfig().getString("settings.tooltip.none") : exitMessage;
    }
}
