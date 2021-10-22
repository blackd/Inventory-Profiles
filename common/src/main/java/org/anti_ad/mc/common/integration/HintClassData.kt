package org.anti_ad.mc.common.integration

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
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
data class ButtonPositionHint(val right: Int = 0,
                              val top: Int = 0,
                              val bottom: Int = 0,
                              val hide: Boolean = false ) {

}

@Serializable
data class HintClassData(val ignore: Boolean = false,
                         val buttonHints: Map<IPNButton, ButtonPositionHint> = emptyMap(),
                         val forceButtonHints: Boolean = false) {
}

@OptIn(ExperimentalSerializationApi::class)
fun registerFromConfig(file: Path) {
    if (file.exists()) {
        try {
            val config: Map<String, HintClassData> = Json.decodeFromStream(file.inputStream());
            Log.trace("Read config: $config")
            config.forEach { (s, hint) ->
                Log.trace("Checking for class: $s")
                val cl: Class<*>
                try {
                    cl = Class.forName(s)
                    Log.trace("Successfully loaded class: ${cl.name}")
                } catch (_: Throwable) {
                    Log.trace("Could not load class $s")
                    return@forEach
                }
                if (hint.ignore) {
                    Log.trace("Adding ignore for ${cl.name}")
                    IgnoredManager.addIgnore(cl)
                }
                if (hint.buttonHints.isNotEmpty()) {
                    Log.trace("Adding gui hints for ${cl.name}")
                    HintsManager.addHints(cl, hint)
                }
            }
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
                    registerFromConfig(file)
                }
            }
            /*
            if (res != null) {
                val fos = file.outputStream()
                res.copyTo(fos)
                try {
                    fos.close()
                    res.close()
                } catch(_: Throwable) {
                    //ignored
                }
                registerFromConfig(file)
            }
             */

        } catch (ioe: IOException) {
            Log.error("Can't create default ModIntegrationHints.json", ioe)
        }
    }
}

