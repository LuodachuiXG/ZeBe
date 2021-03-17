package com.lpddr5.nzhaibao.ui.custom

import android.content.Context
import android.graphics.Color
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.lpddr5.nzhaibao.R
import kotlinx.android.synthetic.main.edit_text_layout.view.*


class EditTextLayout(context: Context, atts: AttributeSet) : LinearLayout(context, atts) {

    init {
        // 加载布局文件
        LayoutInflater.from(context).inflate(R.layout.edit_text_layout, this)

        // 失焦/获取焦点卡片布局和EditText背景颜色设置
        editText_layout_editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText_layout_editText.setBackgroundColor(ContextCompat.getColor(context, R.color.editTextBackground))
                editText_layout_editText.setTextColor(ContextCompat.getColor(context, R.color.selected))
                editText_linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.editTextBackground))
            } else {
                editText_layout_editText.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                editText_layout_editText.setTextColor(ContextCompat.getColor(context, R.color.white))
                editText_linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }

        val attributes = context.obtainStyledAttributes(atts, R.styleable.EditTextLayout)

        // title标题内容
        val title = attributes.getString(R.styleable.EditTextLayout_layout_title) ?: ""
        if (title.isNotEmpty()) {
            editText_layout_title.text = title
        }

        // EditText提示内容
        val editTextHint = attributes.getString(R.styleable.EditTextLayout_layout_editText_hint) ?: ""
        if (editTextHint.isNotEmpty()) {
            editText_layout_editText.hint = editTextHint
        }

        // EditText输入类型
        when (attributes.getInt(R.styleable.EditTextLayout_layout_input_type, 1)) {
            0 -> {
                editText_layout_editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            1 -> {
                editText_layout_editText.inputType = InputType.TYPE_CLASS_TEXT
            }
            2 -> {
                editText_layout_editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        // EditText内容
        val str = attributes.getString(R.styleable.EditTextLayout_layout_editText_text) ?: ""
        if (str.isNotEmpty()) {
            editText_layout_editText.setText(str)
        }

        // EditText字体颜色
        val textColor = attributes.getColor(
            R.styleable.EditTextLayout_layout_editText_color,
            Color.WHITE
        )
        editText_layout_editText.setTextColor(textColor)

        // Layout是否拥有焦点
        val focus = attributes.getBoolean(R.styleable.EditTextLayout_layout_focus, false)
        if (focus) {
            editText_layout_editText.isFocusable = true
            editText_layout_editText.isFocusableInTouchMode = true
            editText_layout_editText.requestFocus()
        }

        // 是否显示必填星号
        val required = attributes.getBoolean(R.styleable.EditTextLayout_layout_required, false)
        if (required) {
            editText_layout_required.visibility = VISIBLE
        }

        // EditText最大长度
        val maxLength = attributes.getInteger(R.styleable.EditTextLayout_layout_maxLength, 100)
        editText_layout_editText.filters = arrayOf<InputFilter>(LengthFilter(maxLength))

        attributes.recycle()
    }

    fun getText(): String {
        return editText_layout_editText.text.toString()
    }

    fun setText(str: String) {
        editText_layout_editText.setText(str)
    }

    fun setFocus(boolean: Boolean) {
        if (boolean) {
            editText_layout_editText.setBackgroundColor(ContextCompat.getColor(context, R.color.editTextBackground))
            editText_layout_editText.setTextColor(ContextCompat.getColor(context, R.color.selected))
            editText_linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.editTextBackground))
            editText_layout_editText.isFocusable = true
            editText_layout_editText.isFocusableInTouchMode = true
            editText_layout_editText.requestFocus()
        } else {
            editText_layout_editText.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
            editText_layout_editText.setTextColor(ContextCompat.getColor(context, R.color.white))
            editText_linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }
    }

}