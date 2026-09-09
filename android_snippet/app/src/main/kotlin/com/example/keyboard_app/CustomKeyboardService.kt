package com.example.keyboard_app

import android.graphics.Color
import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams

/**
 * A lightweight, fully-native keyboard (IME). Keyboards must be drawn with
 * native Android views because the system talks to InputMethodService
 * directly; Flutter is used only for the setup/settings screen in
 * lib/main.dart. This keeps the keyboard fast and battery-friendly.
 */
class CustomKeyboardService : InputMethodService() {

    private val rowsLower = listOf(
        "q w e r t y u i o p",
        "a s d f g h j k l",
        "⇧ z x c v b n m ⌫",
        "123 , SPACE . ⏎"
    )
    private val rowsUpper = listOf(
        "Q W E R T Y U I O P",
        "A S D F G H J K L",
        "⇧ Z X C V B N M ⌫",
        "123 , SPACE . ⏎"
    )
    private val rowsSymbols = listOf(
        "1 2 3 4 5 6 7 8 9 0",
        "@ # \$ _ & - + ( )",
        "= ' \" : ; ! ?  ⌫",
        "ABC , SPACE . ⏎"
    )

    private enum class Mode { LOWER, UPPER, SYMBOLS }
    private var mode = Mode.LOWER

    override fun onCreateInputView(): View {
        return buildKeyboardView()
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        mode = Mode.LOWER
    }

    private fun buildKeyboardView(): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#212121"))
            setPadding(6, 10, 6, 10)
        }

        val rows = when (mode) {
            Mode.LOWER -> rowsLower
            Mode.UPPER -> rowsUpper
            Mode.SYMBOLS -> rowsSymbols
        }

        for (row in rows) {
            val rowLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER
                layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 4, 0, 4)
                }
            }
            for (key in row.split(" ")) {
                if (key.isEmpty()) continue
                rowLayout.addView(makeKey(key))
            }
            container.addView(rowLayout)
        }
        return container
    }

    private fun makeKey(label: String): Button {
        val isWide = label == "SPACE"
        val displayText = if (label == "SPACE") "" else label
        return Button(this).apply {
            text = displayText
            isAllCaps = false
            textSize = 16f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.parseColor("#424242"))
            layoutParams = LinearLayout.LayoutParams(
                0,
                LayoutParams.WRAP_CONTENT,
                if (isWide) 4f else 1f
            ).apply { setMargins(3, 0, 3, 0) }
            setOnClickListener { handleKey(label) }
        }
    }

    private fun handleKey(label: String) {
        val ic = currentInputConnection ?: return
        when (label) {
            "⇧" -> {
                mode = if (mode == Mode.LOWER) Mode.UPPER else Mode.LOWER
                setInputView(buildKeyboardView())
            }
            "123" -> {
                mode = Mode.SYMBOLS
                setInputView(buildKeyboardView())
            }
            "ABC" -> {
                mode = Mode.LOWER
                setInputView(buildKeyboardView())
            }
            "⌫" -> ic.deleteSurroundingText(1, 0)
            "⏎" -> ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER))
            "SPACE" -> ic.commitText(" ", 1)
            else -> {
                ic.commitText(label, 1)
                // Auto-return to lowercase after one uppercase letter, like most keyboards.
                if (mode == Mode.UPPER) {
                    mode = Mode.LOWER
                    setInputView(buildKeyboardView())
                }
            }
        }
    }
}
