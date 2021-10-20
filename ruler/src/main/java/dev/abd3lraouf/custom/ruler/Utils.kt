package dev.abd3lraouf.custom.ruler

import android.text.TextUtils
import androidx.core.view.ViewCompat
import java.util.Locale

fun isRtl() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
