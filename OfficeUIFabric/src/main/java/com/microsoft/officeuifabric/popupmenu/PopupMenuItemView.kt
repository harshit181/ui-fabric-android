/*
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License.
 */

package com.microsoft.officeuifabric.popupmenu

import android.content.Context
import android.graphics.PorterDuff
import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import com.microsoft.officeuifabric.R
import com.microsoft.officeuifabric.popupmenu.PopupMenu.Companion.DEFAULT_ITEM_CHECKABLE_BEHAVIOR
import com.microsoft.officeuifabric.util.isVisible
import com.microsoft.officeuifabric.view.TemplateView

internal class PopupMenuItemView : TemplateView {
    var itemCheckableBehavior: PopupMenu.ItemCheckableBehavior = DEFAULT_ITEM_CHECKABLE_BEHAVIOR
        set(value) {
            if (field == value)
                return
            field = value

            when (itemCheckableBehavior) {
                PopupMenu.ItemCheckableBehavior.SINGLE -> {
                    showRadioButton = true
                    showCheckBox = false
                }
                PopupMenu.ItemCheckableBehavior.ALL -> {
                    showRadioButton = false
                    showCheckBox = true
                }
                PopupMenu.ItemCheckableBehavior.NONE -> {
                    showRadioButton = false
                    showCheckBox = false
                }
            }
        }

    private var title: String = ""
    @DrawableRes
    internal var iconResourceId: Int? = null
    private var isChecked: Boolean = false
    private var showDividerBelow: Boolean = false
    private var showRadioButton: Boolean = false
    private var showCheckBox: Boolean = false

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr)

    fun setMenuItem(popupMenuItem: PopupMenuItem) {
        title = popupMenuItem.title
        iconResourceId = popupMenuItem.iconResourceId
        isChecked = popupMenuItem.isChecked
        showDividerBelow = popupMenuItem.showDividerBelow

        updateViews()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return true
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                setPressedState(true)
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                setPressedState(false)
            }
            MotionEvent.ACTION_CANCEL -> {
                setPressedState(false)
            }
            else -> return false
        }
        return true
    }

    // Template

    override val templateId: Int
        get() = R.layout.view_popup_menu_item

    private var iconImageView: ImageView? = null
    private var titleView: TextView? = null
    private var radioButton: RadioButton? = null
    private var checkBox: CheckBox? = null
    private var dividerView: View? = null

    override fun onTemplateLoaded() {
        super.onTemplateLoaded()

        iconImageView = findViewInTemplateById(R.id.icon)
        titleView = findViewInTemplateById(R.id.title)
        radioButton = findViewInTemplateById(R.id.radio_button)
        checkBox = findViewInTemplateById(R.id.check_box)
        dividerView = findViewInTemplateById(R.id.divider)

        updateViews()
    }

    private fun updateViews() {
        titleView?.text = title

        iconResourceId?.let { iconImageView?.setImageResource(it) }
        iconImageView?.isVisible = iconResourceId != null

        radioButton?.isVisible = showRadioButton
        checkBox?.isVisible = showCheckBox
        dividerView?.isVisible = showDividerBelow

        updateCheckedState(isChecked)
    }

    private fun setPressedState(isPressed: Boolean) {
        this.isPressed = isPressed
        radioButton?.isPressed = isPressed
        checkBox?.isPressed = isPressed
    }

    private fun updateCheckedState(isChecked: Boolean) {
        radioButton?.isChecked = isChecked
        checkBox?.isChecked = isChecked

        if (isChecked) {
            val foregroundSelectedColor = ContextCompat.getColor(context, R.color.uifabric_popup_menu_item_foreground_selected)
            titleView?.setTextColor(foregroundSelectedColor)
            // Using post helps ensure that the color filter is applied to the correct image in API <= Lollipop.
            iconImageView?.post {
                iconImageView?.setColorFilter(foregroundSelectedColor, PorterDuff.Mode.SRC_IN)
                iconImageView?.invalidate()
            }
        } else {
            titleView?.setTextColor(ContextCompat.getColor(context, R.color.uifabric_popup_menu_item_title))
            iconImageView?.post {
                iconImageView?.clearColorFilter()
                iconImageView?.invalidate()
            }
        }
    }
}