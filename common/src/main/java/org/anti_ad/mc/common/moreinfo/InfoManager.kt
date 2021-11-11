package org.anti_ad.mc.common.moreinfo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.anti_ad.mc.common.Log
import org.anti_ad.mc.common.extensions.orDefault
import java.net.URL
import java.util.concurrent.*

import javax.net.ssl.HttpsURLConnection

object InfoManager {

    var mcVersion = "game-version-missing"
    var version = "version-missing"
    var modId = "id-missing"
    var modName = "name-missing"
    var loader: String = "loader-missing"

    val defaultRequest: Map<String, String> = mapOf("domain" to "ipn-stats.anti-ad.org",
                                                    "name" to "pageview")
    val session: MutableMap<String, String> = mutableMapOf()
    val target = URL("https://p.anti-ad.org/api/event")

    val versionUrl = URL("https://github.com/blackd/Inventory-Profiles/releases/latest")

    var isEnabled: () -> Boolean = { true }

    private val executor = Executors.newFixedThreadPool(2)


    fun event(name: Lazy<String>, value: Lazy<String>) {
        if (isEnabled()) {
            doEvent(name.value, value.value)
        }
    }

    fun event(name: String, value: Lazy<String>) {
        if (isEnabled()) {
            doEvent(name, value.value)
        }
    }

    fun event(name: String, value: String = "") {
        if (isEnabled()) {
            doEvent(name, value)
        }
    }

    fun event(name: Lazy<String>, value: String = "") {
        if (isEnabled()) {
            doEvent(name.value, value)
        }
    }

    private fun doEvent(name: String, value: String) {
        if (session[name] == null) {
            session[name] = name
            executor.execute {
                try {
                    sendEvent(name, value)
                } catch (t: Throwable) {
                    Log.error("", t)
                }
            }
        }
    }

    private fun sendEvent(name: String,
                          value: String) {
        val body = mutableMapOf<String, String>().apply {
            putAll(defaultRequest)
            put("url", "https://ipn-stats.anti-ad.org/$name/?$loader&$mcVersion&$modId&$version$value")
        }
        with (target.openConnection() as HttpsURLConnection) {
            val reqBody = Json.encodeToJsonElement<Map<String, String>>(body).toString()
            Log.trace("request body $reqBody")
            val bodyBytes = reqBody.toByteArray()

            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("Content-Length", bodyBytes.size.toString())
            doOutput = true

            with(outputStream) {
                write(bodyBytes)
                close()
            }
            with(inputStream) {
                readAllBytes()
                close()
            }
        }
        Log.trace("Event Sent!")
    }

    fun checkVersion(function: (SemVer, SemVer, Boolean) -> Unit) {
        executor.execute {
            try {
                doCheckVersion(function)
            } catch (t: Throwable) {
                Log.error("", t)
            }
        }
    }

    private fun doCheckVersion(function: (SemVer, SemVer, Boolean) -> Unit) {
        with(versionUrl.openConnection() as HttpsURLConnection) {
            val isBeta = version.contains("BETA")
            val currentVer = SemVer.parse(if (isBeta) version.substringBefore("-")  else { version })
            instanceFollowRedirects = false
            if (responseCode == 302) {
                val latestVer = SemVer.parse(getHeaderField("location").orDefault { "" }.substringAfterLast("/", version).substringAfter("v"))
                if (latestVer > latestVer || (isBeta && latestVer >= currentVer) ) {
                    function(latestVer, currentVer, isBeta)
                }
            }
        }
    }
}