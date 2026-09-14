package sh.haven.core.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Traverses any [ContextWrapper] chain to find the underlying [Activity], if any.
 */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
