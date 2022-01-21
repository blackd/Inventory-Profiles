package org.anti_ad.mc.common.integration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json as HiddenJson
import kotlinx.serialization.json.decodeFromStream
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.exists
import org.anti_ad.mc.common.extensions.name
import org.anti_ad.mc.ipn.api.IPNButton
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.outputStream

@Serializable
data class ButtonPositionHint(val horizontalOffset: Int = 0,
                              val top: Int = 0,
                              val bottom: Int = 0,
                              val hide: Boolean = false ) {

}

@Serializable
data class HintClassData(val ignore: Boolean = false,
                         val playerSideOnly: Boolean = false,
                         val buttonHints: Map<IPNButton, ButtonPositionHint> = emptyMap(),
                         val force: Boolean = false) {
}

val parser = HiddenJson {
    ignoreUnknownKeys = true;
}

@OptIn(ExperimentalSerializationApi::class)
fun registerFromConfigFile(file: Path) {

    if (file.exists()) {
        try {
            val res = HintClassData::class.java.classLoader.getResourceAsStream("assets/inventoryprofilesnext/config/ModIntegrationHints.json");
            val builtInConfig: Map<String, HintClassData> = if (res != null) parser.decodeFromStream(res) else mapOf();
            processConfig(builtInConfig)
        } catch (se: SerializationException) {
            Log.error("Builtin configuration is invalid! Please report this.", se)
        } catch (ioe: IOException) {
            Log.error("Builtin configuration is invalid! Please report this.", ioe)
        }
        try {
            val config: Map<String, HintClassData> = parser.decodeFromStream(file.inputStream());
            processConfig(config)
        } catch (se: SerializationException) {
            Log.error("Error parsing Mod compatibility config file: ${file.name}", se)
        } catch (ioe: IOException) {
            Log.error("Error reading Mod compatibility file: ${file.name}", ioe)
        }
    } else {
        try {
            val res = HintClassData::class.java.classLoader.getResourceAsStream("assets/inventoryprofilesnext/config/ModIntegrationHints.json");
            res?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                    registerFromConfigFile(file)
                }
            }
        } catch (ioe: IOException) {
            Log.error("Can't create default ModIntegrationHints.json", ioe)
        }
    }
}

private fun processConfig(config: Map<String, HintClassData>) {
    Log.trace("Read config: $config")
    config.forEach { (className, hint) ->
        if (hint.ignore) {
            Log.trace("Adding ignore for $className")
            HintsManager.addIgnore(className, hint.force)
        }
        if (hint.buttonHints.isNotEmpty()) {
            Log.trace("Adding gui hints for $className")
            HintsManager.addHints(className, hint)
        }
        if (hint.playerSideOnly) {
            HintsManager.addPlayerSideOnly(className)
        }
    }
}

