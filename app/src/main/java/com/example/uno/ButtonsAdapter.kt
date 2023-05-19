package com.example.uno


import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button

class ButtonsAdapter(private var context: Context, private var buttons: List<Button>) : BaseAdapter() {
    override fun getCount() = buttons.size
    override fun getItem(position: Int) = buttons[position]
    override fun getItemId(position: Int) = 0L
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val button = buttons[position]
        button.layoutParams = ViewGroup.LayoutParams(280, 420)
        return button
    }
}
