/*
 * Inventory Profiles Next
 *
 *   Copyright (c) 2022 Plamen K. Kosseff <p.kosseff@gmail.com>
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

package org.anti_ad.mc.ipnext.mixin;

import net.minecraft.world.item.CreativeModeTab;
import org.anti_ad.mc.ipnext.mixinhelpers.IMixinItemGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(CreativeModeTab.class)
abstract class MixinItemGroup implements IMixinItemGroup {

    @Unique
    private int ipnPriorityIndex = -1;

    @Override
    public int getIPNPriorityIndex() {
        return ipnPriorityIndex;
    }

    @Override
    public void setIPNPriorityIndex(int value) {
        ipnPriorityIndex = value;
    }
}
