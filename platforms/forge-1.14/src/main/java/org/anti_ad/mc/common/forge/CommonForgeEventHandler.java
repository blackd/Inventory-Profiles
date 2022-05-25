/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2019-2020 jsnimda <7615255+jsnimda@users.noreply.github.com>
 *   Copyright (c) 2021-2022 Plamen K. Kosseff <p.kosseff@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.anti_ad.mc.common.forge;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.anti_ad.mc.common.input.GlobalInputHandler;
import org.anti_ad.mc.common.input.GlobalScreenEventListener;
import org.anti_ad.mc.common.vanilla.Vanilla;
import org.anti_ad.mc.ipnext.config.LockedSlotsSettings;
import org.anti_ad.mc.ipnext.config.ModSettings;
import org.anti_ad.mc.ipnext.event.LockSlotsHandler;
import org.lwjgl.glfw.GLFW;

public class CommonForgeEventHandler {

    // Keyboard <-> KeyboardListener , onKey <-> onKeyEvent // ref: malilib forge 1.14.4 ForgeInputEventHandler
    @SubscribeEvent
    public void onKeyLast(InputEvent.KeyInputEvent event) {
        if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
            GlobalInputHandler.INSTANCE.onKey(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers(), true, 0l);
        }
    }

    @SubscribeEvent
    public void onKeyPressed(GuiScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        onScreenKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_PRESS, event.getModifiers(), event);
        if (event.isCanceled()) return;
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onKeyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers(), true));
    }

    @SubscribeEvent
    public void onKeyRelease(GuiScreenEvent.KeyboardKeyReleasedEvent.Pre event) {
        onScreenKey(event.getKeyCode(), event.getScanCode(), GLFW.GLFW_RELEASE, event.getModifiers(), event);
        if (event.isCanceled()) return;
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onKeyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers(), true));
    }

    @SubscribeEvent
    public void onKeyPressedPost(GuiScreenEvent.KeyboardKeyPressedEvent.Post event) {
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onKeyPressed(event.getKeyCode(), event.getScanCode(), event.getModifiers(), false));
    }

    @SubscribeEvent
    public void onKeyReleasePost(GuiScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onKeyReleased(event.getKeyCode(), event.getScanCode(), event.getModifiers(), false));
    }

    // Keyboard.onKey()
    // fix vanilla keybind swallow my listener
    // by line 308 aboolean[0] early returned
    // (e.g. pressing z + 1 while hovering slots)
    private void onScreenKey(int key, int scanCode, int action, int modifiers, GuiScreenEvent event) {
        // tmp solution fixing crafting recipe crash when opening other screen
        // (as post will also be swallowed if vanilla screen handle it)
        // fixme better approach
        Screen lastScreen = Vanilla.INSTANCE.screen();
        long handle = Vanilla.INSTANCE.window().getHandle();
        boolean result = GlobalInputHandler.INSTANCE.onKey(key, scanCode, action, modifiers, true, handle);
        event.setCanceled(result || lastScreen != Vanilla.INSTANCE.screen()); // detect gui change, cancel vanilla
    }

    // ============
    // mouse
    // ============

    @SubscribeEvent
    public void onMouseButtonLast(InputEvent.MouseInputEvent event) {
        if (Vanilla.INSTANCE.screen() == null) { // non null is handled below
            GlobalInputHandler.INSTANCE.onMouseButton(event.getButton(), event.getAction(), event.getMods());
        }
    } // fixme occasionally throw npe on Vanilla.mc() (idk why, build/class loading related?)

    @SubscribeEvent
    public void onMouseClicked(GuiScreenEvent.MouseClickedEvent.Pre event) {
        onScreenMouseButton(event.getButton(), GLFW.GLFW_PRESS, lastMods, event);
        if (event.isCanceled()) return;
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onMouseClicked(event.getMouseX(), event.getMouseY(), event.getButton(), true));
    }

    @SubscribeEvent
    public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        onScreenMouseButton(event.getButton(), GLFW.GLFW_RELEASE, lastMods, event);
        if (event.isCanceled()) return;
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onMouseReleased(event.getMouseX(), event.getMouseY(), event.getButton(), true));
    }

    @SubscribeEvent
    public void onMouseClickedPost(GuiScreenEvent.MouseClickedEvent.Post event) {
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onMouseClicked(event.getMouseX(), event.getMouseY(), event.getButton(), false));
    }

    @SubscribeEvent
    public void onMouseReleasedPost(GuiScreenEvent.MouseReleasedEvent.Post event) {
        event.setCanceled(GlobalScreenEventListener.INSTANCE
                .onMouseReleased(event.getMouseX(), event.getMouseY(), event.getButton(), false));
    }

    private void onScreenMouseButton(int button, int action, int mods, GuiScreenEvent event) {
        Screen lastScreen = Vanilla.INSTANCE.screen();
        boolean result = GlobalInputHandler.INSTANCE.onMouseButton(button, action, mods);
        event.setCanceled(result || lastScreen != Vanilla.INSTANCE.screen()); // detect gui change, cancel vanilla
    }

    private int lastMods = 0;

    @SubscribeEvent
    public void onRawMouse(InputEvent.MouseInputEvent event) {
        lastMods = event.getMods();
    }


//  @SubscribeEvent
//  public void onWorldLoad(WorldEvent.Load event) {
//    // clear keybind (pressing keys)
//    GlobalInputHandler.INSTANCE.getPressedKeys().clear();
//  }

    // todo mouse move event
  /*
  @Inject(method = "onCursorPos", at = @At("RETURN"))
  private void onCursorPos(long handle, double xpos, double ypos, CallbackInfo ci) {
    VanillaUtil.INSTANCE.updateMouse();
  }
   */

}
