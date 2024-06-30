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

package org.anti_ad.mc.ipnext.item.rule.parameter

import org.anti_ad.mc.alias.nbt.NbtElement
import org.anti_ad.mc.alias.util.Identifier
import org.anti_ad.mc.alias.util.IdentifierOf
import org.anti_ad.mc.common.extensions.trySwallow
import org.anti_ad.mc.common.extensions.usefulName

import org.anti_ad.mc.ipnext.item.NbtUtils
import org.anti_ad.mc.ipnext.item.rule.ArgumentType
import org.anti_ad.mc.ipnext.item.rule.Rule
import org.anti_ad.mc.ipnext.parser.TemporaryRuleParser


object IdentifierArgumentType: ArgumentType<Identifier> {
    override fun toString(value: Identifier) = "${value.namespace}:${value.path}"
    override fun parse(argument: String) = IdentifierOf(argument)
}
object StringArgumentType : ArgumentType<String> {
    override fun toString(value: String) = value
    override fun parse(argument: String) = argument
}

object BooleanArgumentType : ArgumentType<Boolean> {
    override fun toString(value: Boolean) = value.toString()
    override fun parse(argument: String) =
        argument.takeIf { it == "true" || it == "false" }?.toBoolean()
}

class EnumArgumentType<T : Enum<T>>(val enumClass: Class<T>) : ArgumentType<T> {
    override fun toString(value: T) = value.name.lowercase()
    override fun parse(argument: String) =
        trySwallow {
            java.lang.Enum.valueOf(enumClass,
                                   argument.uppercase())
        }
}

object NbtArgumentType : ArgumentType<NbtElement> {
    override fun toString(value: NbtElement) = value.toString()
    override fun parse(argument: String) = NbtUtils.parseNbtOrEmpty(argument)
}

object RuleArgumentType : ArgumentType<Rule> {
    override fun toString(value: Rule) = "[${value.javaClass.usefulName}]" //value.toString() // todo
    override fun parse(argument: String) = TemporaryRuleParser.parse(argument)
}

object TagNameArgumentType : ArgumentType<ItemTypeMatcher> {
    override fun toString(value: ItemTypeMatcher) = value.toString()
    override fun parse(argument: String) = ItemTypeMatcher.forTag(argument)
}

object ItemNameArgumentType : ArgumentType<ItemTypeMatcher> {
    override fun toString(value: ItemTypeMatcher) = value.toString()
    override fun parse(argument: String) = ItemTypeMatcher.forItem(argument)
}

object NbtPathArgumentType : ArgumentType<NbtUtils.NbtPath> {
    override fun toString(value: NbtUtils.NbtPath) = value.toString()
    override fun parse(argument: String) = NbtUtils.NbtPath.of(argument)
}
