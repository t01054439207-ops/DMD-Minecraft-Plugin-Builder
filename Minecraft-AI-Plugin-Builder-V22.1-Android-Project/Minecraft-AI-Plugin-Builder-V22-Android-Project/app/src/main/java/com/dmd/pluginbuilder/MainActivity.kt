package com.dmd.pluginbuilder

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class MainActivity : Activity() {
    private lateinit var web: WebView
    private var zipBytes: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        web = WebView(this)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.webViewClient = WebViewClient()
        web.webChromeClient = WebChromeClient()
        web.addJavascriptInterface(Bridge(), "Android")
        setContentView(web)
        web.loadUrl("file:///android_asset/index.html")
    }

    inner class Bridge {
        @JavascriptInterface
        fun exportZip(rawName: String, description: String, language: String, platform: String, version: String) {
            try {
                val name = rawName.replace(Regex("[^A-Za-z0-9_-]"), "_").ifBlank { "MyPlugin" }
                val entries = Generator.create(name, description, language, platform, version)
                val output = ByteArrayOutputStream()
                ZipOutputStream(output).use { zip ->
                    entries.forEach { (path, content) ->
                        zip.putNextEntry(ZipEntry("$name/$path"))
                        zip.write(content.toByteArray(Charsets.UTF_8))
                        zip.closeEntry()
                    }
                }
                val completedZip = output.toByteArray()
                runOnUiThread {
                    zipBytes = completedZip
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "application/zip"
                        putExtra(Intent.EXTRA_TITLE, "$name.zip")
                    }
                    startActivityForResult(intent, 2201)
                }
            } catch (e: Exception) {
                runOnUiThread { Toast.makeText(this@MainActivity, "오류: ${e.message}", Toast.LENGTH_LONG).show() }
            }
        }
    }

    @Deprecated("Deprecated by Android API")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2201 && resultCode == RESULT_OK) {
            val bytes = zipBytes
            val uri = data?.data
            if (bytes != null && uri != null) {
                contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                Toast.makeText(this, "ZIP 저장 완료", Toast.LENGTH_LONG).show()
            }
        }
        zipBytes = null
    }
}

private object Generator {
    fun create(name: String, description: String, language: String, platform: String, version: String): Map<String, String> {
        val desc = description.replace("\"", "'").take(180)
        val result = linkedMapOf<String, String>()
        result["README.md"] = """# $name

- Minecraft: $version
- Platform: $platform
- Language: $language

$desc

이 앱은 오프라인 기본 템플릿을 생성합니다. 서버 적용 전 빌드와 테스트가 필요합니다.
""".trimIndent()
        if (language == "Skript") {
            result["scripts/${name.lowercase()}.sk"] = """on join:
    send "&a서버에 오신 것을 환영합니다!" to player

command /${name.lowercase()}:
    trigger:
        send "&e$name 명령어입니다." to player
""".trimIndent()
        } else {
            val pkg = "com.example.${name.lowercase().replace(Regex("[^a-z0-9_]"), "")}".let { if (it.endsWith(".")) "com.example.plugin" else it }
            val dir = pkg.replace('.', '/')
            result["src/main/java/$dir/Main.java"] = """package $pkg;
import org.bukkit.plugin.java.JavaPlugin;
public final class Main extends JavaPlugin {
    @Override public void onEnable() {
        getLogger().info("$name enabled!");
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
    }
}
""".trimIndent()
            result["src/main/java/$dir/JoinListener.java"] = """package $pkg;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public final class JoinListener implements Listener {
    @EventHandler public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage("§a서버에 오신 것을 환영합니다!");
    }
}
""".trimIndent()
            result["src/main/resources/plugin.yml"] = """name: $name
version: 1.0.0
main: $pkg.Main
api-version: '1.20'
description: "$desc"
""".trimIndent()
            result["build.gradle"] = """plugins { id 'java' }
group = '$pkg'
version = '1.0.0'
repositories { mavenCentral(); maven { url = 'https://repo.papermc.io/repository/maven-public/' } }
dependencies { compileOnly 'io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT' }
java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }
""".trimIndent()
        }
        return result
    }
}
