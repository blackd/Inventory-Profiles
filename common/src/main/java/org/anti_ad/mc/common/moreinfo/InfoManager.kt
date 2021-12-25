package org.anti_ad.mc.common.moreinfo

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.anti_ad.mc.common.Log
import java.net.URL
import java.util.concurrent.*

import javax.net.ssl.HttpsURLConnection

object InfoManager {

    var mcVersion = "game-version-missing"
    var version = "version-missing"
    var modId = "id-missing"
    var modName = "name-missing"
    var loader: String = "loader-missing"

    private val defaultRequest: Map<String, String> = mapOf("domain" to "ipn-stats.anti-ad.org",
                                                    "name" to "pageview")
    private val session: MutableMap<String, String> = mutableMapOf()
    private val target = URL("https://p.anti-ad.org/api/event")

    private val versionUrl = URL("https://ipn.anti-ad.org/ipn/versionCheckV2")

    var isEnabled: () -> Boolean = { true }

    private val isBeta by lazy { version.contains("BETA") }
    private val currentVer by lazy { SemVer.parse(if (isBeta) version.substringBefore("-")  else { version }) }
    private val mcVersionClean by lazy { mcVersion.split(".").joinToString(separator = "") }


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
            //Log.trace("request body $reqBody")
            val bodyBytes = reqBody.toByteArray()

            requestMethod = "POST"
            setRequestProperty("Content-Type", "application/json; utf-8")
            setRequestProperty("User-Agent", "Minecraft/$mcVersionClean IPN/${currentVer.major}${currentVer.minor}${currentVer.patch}")
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
                Log.warn("Update check failed with message - ${t.message}")
            }
        }
    }

    private fun doCheckVersion(function: (SemVer, SemVer, Boolean) -> Unit) {
        with(versionUrl.openConnection() as HttpsURLConnection) {
            val isBeta = version.contains("BETA")
            val currentVer = SemVer.parse(if (isBeta) version.substringBefore("-")  else { version })
            setRequestProperty("User-Agent", "Minecraft/$mcVersion; $loader; IPN/$currentVer;" + if (isBeta) " Beta" else "")

            instanceFollowRedirects = false
            val xIpn = getHeaderField("X-IPN")
            if (responseCode == 302 && xIpn != null) {
                val latestVer = SemVer.parse(xIpn)
                if (latestVer > currentVer || (isBeta && latestVer >= currentVer) ) {
                    function(latestVer, currentVer, isBeta)
                }
            }
        }
    }
}