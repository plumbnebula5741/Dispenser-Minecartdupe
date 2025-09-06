package plumb.MinecartDupe;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dispenser;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.StorageMinecart;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MinecartDupe extends JavaPlugin implements Listener {

    private static final Set<Material> SHULKER_TYPES = new HashSet<>(Arrays.asList(
            Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
            Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX,
            Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.SILVER_SHULKER_BOX,
            Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
            Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX,
            Material.BLACK_SHULKER_BOX
    ));

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Minecart dupe enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("Minecart dupe disabled");
    }

    @EventHandler(ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null || !isShulker(stack.getType())) return;

        Block dispenserBlock = event.getBlock();
        BlockFace facing = getFacing(dispenserBlock);
        if (facing == null) return;

        // location infront of dispenser
        Block targetBlock = dispenserBlock.getRelative(facing);
        Location targetCenter = targetBlock.getLocation().add(0.5, 0.5, 0.5);

        StorageMinecart cart = findChestMinecartAt(targetCenter, 0.75);
        if (cart == null) return;

        // makes so the dispenser keepts it shulker
        event.setCancelled(true);

        // Only dupe if the cart has space
        Inventory inv = cart.getInventory();
        if (inv.firstEmpty() == -1) {
            // if the minecart is full it does nothing
            return;
        }

        ItemStack clone = stack.clone();
        clone.setAmount(1);
        inv.addItem(clone);
    }

    private boolean isShulker(Material mat) {
        return SHULKER_TYPES.contains(mat) || mat.name().endsWith("_SHULKER_BOX");
    }

    private BlockFace getFacing(Block dispenserBlock) {
        BlockState state = dispenserBlock.getState();
        if (!(state.getData() instanceof Dispenser)) return null;
        Dispenser disp = (Dispenser) state.getData();
        return disp.getFacing();
    }

    private StorageMinecart findChestMinecartAt(Location center, double radius) {
        for (Entity e : center.getWorld().getNearbyEntities(center, radius, radius, radius)) {
            if (e instanceof StorageMinecart) {
                return (StorageMinecart) e;
            }
        }
        return null;
    }
}