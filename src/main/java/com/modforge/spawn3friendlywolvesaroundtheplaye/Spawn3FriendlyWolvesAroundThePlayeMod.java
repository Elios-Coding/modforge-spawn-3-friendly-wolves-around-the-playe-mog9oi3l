package com.modforge.spawn3friendlywolvesaroundtheplaye;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Spawn3FriendlyWolvesAroundThePlayeMod implements ModInitializer {

    public static final String MOD_ID = "spawn3friendlywolvesaroundtheplaye";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private final Map<UUID, Boolean> playerSneakingState = new HashMap<>();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Spawn 3 Friendly Wolves Mod.");

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                try {
                    handlePlayerTick(player);
                } catch (Exception e) {
                    LOGGER.error("Error during player tick event for " + player.getName().getString(), e);
                }
            }
        });
    }

    private void handlePlayerTick(ServerPlayerEntity player) {
        UUID playerUuid = player.getUuid();
        boolean isHoldingBone = player.getMainHandStack().isOf(Items.BONE) || player.getOffHandStack().isOf(Items.BONE);
        boolean isCurrentlySneaking = player.isSneaking();
        boolean wasPreviouslySneaking = playerSneakingState.getOrDefault(playerUuid, false);

        if (isCurrentlySneaking && !wasPreviouslySneaking && isHoldingBone) {
            spawnWolvesForPlayer(player);
        }

        playerSneakingState.put(playerUuid, isCurrentlySneaking);
    }

    private void spawnWolvesForPlayer(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        if (world == null) {
            return;
        }

        int wolfCount = 3;
        LOGGER.info("Spawning {} wolves for player {}.", wolfCount, player.getName().getString());

        for (int i = 0; i < wolfCount; i++) {
            try {
                WolfEntity wolf = new WolfEntity(EntityType.WOLF, world);

                double angle = 2 * Math.PI * i / wolfCount; // Distribute wolves evenly
                double radius = 2.5;
                double x = player.getX() + radius * Math.cos(angle);
                double z = player.getZ() + radius * Math.sin(angle);
                double y = player.getY();

                wolf.updatePosition(x, y, z);
                wolf.setTamed(true);
                wolf.setOwnerUuid(player.getUuid());
                world.spawnEntity(wolf);
            } catch (Exception e) {
                LOGGER.error("Could not spawn wolf for player " + player.getName().getString(), e);
            }
        }
    }
}
