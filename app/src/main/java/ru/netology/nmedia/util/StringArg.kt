package ru.netology.nmedia.util

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object StringArg: ReadWriteProperty<Bundle, String?> {

    override fun setValue(thisRef: Bundle, property: KProperty<*>, value: String?) {
        thisRef.putString(property.name, value)
    }

    override fun getValue(thisRef: Bundle, property: KProperty<*>): String? =
        thisRef.getString(property.name)
}

fun Context.toast(message: CharSequence) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()