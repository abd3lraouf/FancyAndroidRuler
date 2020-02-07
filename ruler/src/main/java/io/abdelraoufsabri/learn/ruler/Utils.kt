package io.abdelraoufsabri.learn.ruler

import android.text.TextUtils
import androidx.core.view.ViewCompat
import java.util.*

fun isRtl() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL
