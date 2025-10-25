package org.wit.placemark.models

package org.wit.placemark.models

import android.content.Context
import java.io.File

fun write(context: Context, filename: String, data: String) {
    context.openFileOutput(filename, Context.MODE_PRIVATE).use {
        it.write(data.toByteArray())
    }
}

fun read(context: Context, filename: String): String? {
    return try {
        context.openFileInput(filename).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        null
    }
}

fun fileExists(context: Context, filename: String): Boolean {
    val file = File(context.filesDir, filename)
    return file.exists()
}
