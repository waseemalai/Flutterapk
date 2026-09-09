package com.example.keyboard_app

import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {
    private val CHANNEL = "keyboard_app/channel"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "isKeyboardEnabled" -> result.success(isKeyboardEnabled())
                "isKeyboardSelected" -> result.success(isKeyboardSelected())
                "openLanguageSettings" -> {
                    startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
                    result.success(null)
                }
                "showInputMethodPicker" -> {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showInputMethodPicker()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    /** True once the user has toggled our keyboard ON in system settings. */
    private fun isKeyboardEnabled(): Boolean {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.enabledInputMethodList.any { it.packageName == packageName }
    }

    /** True once our keyboard is the currently-active input method. */
    private fun isKeyboardSelected(): Boolean {
        val current = Settings.Secure.getString(contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
        return current?.startsWith(packageName) == true
    }
}
