package org.anti_ad.mc.ipn.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation should be used by interested mods to inform Inventory Profiles Next(IPN)
 * that one or more of their screens are not to be treated as inventories.
 *
 * <h2>What NOT to blacklist?</h2>
 * <ul>
 *     <li>
 *     If your screen doesn't inherit from: (depending on the mappings you use)
 *   <ul>
 *       <li>net.minecraft.client.gui.screen.ingame.HandledScreen</li>
 *       <li>net.minecraft.client.gui.screen.inventory.ContainerScreen</li>
 *       <li>net.minecraft.client.gui.screens.inventory.AbstractContainerScreen</li>
 *       <li>May be others....</li>
 *   </ul>
 *   </li>
 *   <li>
 *   If your inventory screen follows the vanilla concepts of having slots that respect the stack
 *   limits it's probably already handled by IPN and you don't need to add it to the blacklist.
 *   </li>
 *   <li>
 *   The <code>slots</code> field/property is an empty. In other words the size is 0.
 *   </li>
 *   <li>
 *   If your screen inventory inherits:
 *   <ul>
 *       <li>EnchantingTable</li>
 *       <li>Anvil</li>
 *       <li>Beacon</li>
 *       <li>CartographyTable</li>
 *       <li>Grindstone</li>
 *       <li>Lectern</li>
 *       <li>Loom</li>
 *       <li>Stone Cutter</li>
 *       <li>Merchant</li>
 *       <li>CraftingTable</li>
 *       <li>Hopper</li>
 *       <li>BrewingStand</li>
 *       <li>Furnace and derivatives</li>
 *   </ul>
 *   </li>
 *   </ul>
 *
 *  <h2>What to blacklist?</h2>
 *  Anything that does not fall into the above categories. For example all containers from Dank Storage mod
 *  MUST be blacklisted since they over-stack items and mouse clicks on a slot have different behaviour
 *  compared to vanilla containers.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IPNIgnore {
}
