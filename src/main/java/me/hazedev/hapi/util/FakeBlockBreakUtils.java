package me.hazedev.hapi.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.EntityEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Pose;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// Common util methods
public final class FakeBlockBreakUtils {

    private FakeBlockBreakUtils() {}

    public static Item fakeBreakBlock(Block block) {
        Material materialToDrop = getMaterialToDrop(block);
        block.setType(Material.AIR);
        return getFakeItemDrop(new ItemStack(materialToDrop));
    }

    public static Material getMaterialToDrop(Block block) {
        Material blockMaterial = block.getType();
        switch (blockMaterial) {
            case EMERALD_ORE:
                return Material.EMERALD;
            case DIAMOND_ORE:
                return Material.DIAMOND;
            case REDSTONE_ORE:
                return Material.REDSTONE;
            case COAL_ORE:
                return Material.COAL;
            case STONE:
                return Material.COBBLESTONE;
            default:
                return blockMaterial;
        }
    }

    public static Item getFakeItemDrop(ItemStack itemStack) {
        return new FakeItem(itemStack);
    }

    private static class FakeItem implements Item {

        ItemStack itemStack;

        public FakeItem(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @NotNull
        @Override
        public ItemStack getItemStack() {
            return itemStack;
        }

        @Override
        public void setItemStack(@NotNull ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @NotNull
        @Override
        public EntityType getType() {
            return EntityType.DROPPED_ITEM;
        }

        @Override
        public void remove() {}

        @Override
        public int getPickupDelay() {
            return 0;
        }

        @Override
        public void setPickupDelay(int i) {}

        @Override
        public void setOwner(@Nullable UUID uuid) {}

        @Nullable
        @Override
        public UUID getOwner() {
            return null;
        }

        @Override
        public void setThrower(@Nullable UUID uuid) {

        }

        @Nullable
        @Override
        public UUID getThrower() {
            return null;
        }

        @NotNull
        @Override
        public Location getLocation() {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Location getLocation(@Nullable Location location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setVelocity(@NotNull Vector vector) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Vector getVelocity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getHeight() {
            throw new UnsupportedOperationException();
        }

        @Override
        public double getWidth() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public BoundingBox getBoundingBox() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOnGround() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInWater() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public World getWorld() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setRotation(float v, float v1) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean teleport(@NotNull Location location) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean teleport(@NotNull Location location, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean teleport(@NotNull Entity entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean teleport(@NotNull Entity entity, @NotNull PlayerTeleportEvent.TeleportCause teleportCause) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public List<Entity> getNearbyEntities(double v, double v1, double v2) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getEntityId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getFireTicks() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getMaxFireTicks() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFireTicks(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isDead() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isValid() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendMessage(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendMessage(@NotNull String[] strings) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendMessage(@Nullable UUID uuid, @NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sendMessage(@Nullable UUID uuid, @NotNull String[] strings) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Server getServer() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPersistent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPersistent(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Entity getPassenger() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean setPassenger(@NotNull Entity entity) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public List<Entity> getPassengers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addPassenger(@NotNull Entity entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removePassenger(@NotNull Entity entity) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean eject() {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFallDistance() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFallDistance(float v) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLastDamageCause(@Nullable EntityDamageEvent entityDamageEvent) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public EntityDamageEvent getLastDamageCause() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public UUID getUniqueId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getTicksLived() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTicksLived(int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void playEffect(@NotNull EntityEffect entityEffect) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInsideVehicle() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean leaveVehicle() {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public Entity getVehicle() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCustomNameVisible(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCustomNameVisible() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setGlowing(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isGlowing() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setInvulnerable(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInvulnerable() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isSilent() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setSilent(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasGravity() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setGravity(boolean b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getPortalCooldown() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setPortalCooldown(int i) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Set<String> getScoreboardTags() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addScoreboardTag(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeScoreboardTag(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public PistonMoveReaction getPistonMoveReaction() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public BlockFace getFacing() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Pose getPose() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Spigot spigot() {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public String getCustomName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCustomName(@Nullable String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setMetadata(@NotNull String s, @NotNull MetadataValue metadataValue) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public List<MetadataValue> getMetadata(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasMetadata(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeMetadata(@NotNull String s, @NotNull Plugin plugin) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPermissionSet(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPermissionSet(@NotNull Permission permission) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPermission(@NotNull String s) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean hasPermission(@NotNull Permission permission) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String s, boolean b, int i) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public PermissionAttachment addAttachment(@NotNull Plugin plugin, int i) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeAttachment(@NotNull PermissionAttachment permissionAttachment) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void recalculatePermissions() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public Set<PermissionAttachmentInfo> getEffectivePermissions() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isOp() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setOp(boolean b) {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        public PersistentDataContainer getPersistentDataContainer() {
            throw new UnsupportedOperationException();
        }

        // PAPER specific


        @Override
        public boolean canMobPickup() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCanMobPickup(boolean canMobPickup) {
            throw new UnsupportedOperationException();

        }

        @Override
        public boolean canPlayerPickup() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCanPlayerPickup(boolean canPlayerPickup) {
            throw new UnsupportedOperationException();

        }

        @Override
        public boolean willAge() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setWillAge(boolean willAge) {
            throw new UnsupportedOperationException();

        }

        @Override
        public @Nullable Location getOrigin() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean fromMobSpawner() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull Chunk getChunk() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull CreatureSpawnEvent.SpawnReason getEntitySpawnReason() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInRain() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInBubbleColumn() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInWaterOrRain() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInWaterOrBubbleColumn() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInWaterOrRainOrBubbleColumn() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isInLava() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isTicking() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @Nullable Component customName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void customName(@Nullable Component customName) {
            throw new UnsupportedOperationException();
        }
    }

}
