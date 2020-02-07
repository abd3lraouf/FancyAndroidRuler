package io.abdelraoufsabri.learn.ruler.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.annotation.ColorRes
import androidx.annotation.StyleableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import io.abdelraoufsabri.learn.ruler.ObservableHorizontalScrollView
import io.abdelraoufsabri.learn.ruler.R
import kotlinx.android.synthetic.main.units.view.*
import java.util.*
import kotlin.math.abs
import kotlin.math.roundToInt


class FancyRuler : LinearLayout {
    var value: Float = 0F

    private var multiplier = 0

    private val mLeftSpacer: View
    private val mRightSpacer: View
    private val mScrollView: ObservableHorizontalScrollView

    private val rulerMaxValue: Int
    private val rulerMinValue: Int
    private val rulerSystem: Int

    private val defaultPosition: Float
    private val step by lazy { 16F.dpAsPixels() }

    private val METRIC_SYSTEM = 10

    private val DEFAULT_RULER_MAX = 100
    private val DEFAULT_RULER_MIN = 0

    private val START_POSITION = -1F
    private val MIDDLE_POSITION = -2F
    private val END_POSITION = -3F

    private lateinit var styledAttributes: TypedArray

    private val mainUnitBarColor: Int
    private val quarterUnitBarColor: Int
    private val middleUnitBarColor: Int
    private val threeQuartersUnitBarColor: Int
    private val mainUnitTextColor: Int
    private val quarterUnitTextColor: Int
    private val middleUnitTextColor: Int
    private val threeQuartersUnitTextColor: Int
    private val normalUnitBarColor: Int
    private val rulerPointerColor: Int
    private val rulerBackgroundColor: Int


    constructor(context: Context) : super(context) {
        mLeftSpacer = View(context)
        mRightSpacer = View(context)
        mScrollView = ObservableHorizontalScrollView(context)

        mainUnitBarColor = ContextCompat.getColor(context, R.color.mainUnitBarColor)
        quarterUnitBarColor = ContextCompat.getColor(context, R.color.quarterUnitBarColor)
        middleUnitBarColor = ContextCompat.getColor(context, R.color.middleUnitBarColor)
        threeQuartersUnitBarColor = ContextCompat.getColor(context, R.color.threeQuartersUnitBarColor)
        mainUnitTextColor = ContextCompat.getColor(context, R.color.mainUnitTextColor)
        quarterUnitTextColor = ContextCompat.getColor(context, R.color.quarterUnitTextColor)
        middleUnitTextColor = ContextCompat.getColor(context, R.color.middleUnitTextColor)
        threeQuartersUnitTextColor = ContextCompat.getColor(context, R.color.threeQuartersUnitTextColor)
        normalUnitBarColor = ContextCompat.getColor(context, R.color.normalUnitBarColor)
        rulerPointerColor = ContextCompat.getColor(context, R.color.rulerPointerColor)
        rulerBackgroundColor = ContextCompat.getColor(context, R.color.rulerBackgroundColor)

        rulerMaxValue = DEFAULT_RULER_MAX
        rulerMinValue = DEFAULT_RULER_MIN
        rulerSystem = METRIC_SYSTEM
        defaultPosition = MIDDLE_POSITION
        initialize()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mLeftSpacer = View(context)
        mRightSpacer = View(context)
        mScrollView = ObservableHorizontalScrollView(context)

        styledAttributes = context.obtainStyledAttributes(attr, R.styleable.FancyRuler, 0, 0)

        mainUnitBarColor = getColorFromXml(context, R.styleable.FancyRuler_rulerMainUnitBarColor, R.color.mainUnitBarColor)
        quarterUnitBarColor = getColorFromXml(context, R.styleable.FancyRuler_rulerQuarterUnitBarColor, R.color.quarterUnitBarColor)
        middleUnitBarColor = getColorFromXml(context, R.styleable.FancyRuler_rulerMiddleUnitBarColor, R.color.middleUnitBarColor)
        threeQuartersUnitBarColor = getColorFromXml(context, R.styleable.FancyRuler_rulerThreeQuartersUnitBarColor, R.color.threeQuartersUnitBarColor)
        mainUnitTextColor = getColorFromXml(context, R.styleable.FancyRuler_rulerMainUnitTextColor, R.color.mainUnitTextColor)
        quarterUnitTextColor = getColorFromXml(context, R.styleable.FancyRuler_rulerQuarterUnitTextColor, R.color.quarterUnitTextColor)
        middleUnitTextColor = getColorFromXml(context, R.styleable.FancyRuler_rulerMiddleUnitTextColor, R.color.middleUnitTextColor)
        threeQuartersUnitTextColor = getColorFromXml(context, R.styleable.FancyRuler_rulerThreeQuartersUnitTextColor, R.color.threeQuartersUnitTextColor)
        normalUnitBarColor = getColorFromXml(context, R.styleable.FancyRuler_rulerNormalUnitBarColor, R.color.normalUnitBarColor)
        rulerPointerColor = getColorFromXml(context, R.styleable.FancyRuler_rulerPointerColor, R.color.rulerPointerColor)
        rulerBackgroundColor = getColorFromXml(context, R.styleable.FancyRuler_rulerBackgroundColor, R.color.rulerBackgroundColor)

        rulerMaxValue = getIntFromXml(R.styleable.FancyRuler_rulerMaxValue, DEFAULT_RULER_MAX)
        rulerMinValue = getIntFromXml(R.styleable.FancyRuler_rulerMinValue, DEFAULT_RULER_MIN)
        rulerSystem = getIntFromXml(R.styleable.FancyRuler_rulerSystem, METRIC_SYSTEM)
        defaultPosition = getFloatFromXml(R.styleable.FancyRuler_rulerDefaultPosition, MIDDLE_POSITION)

        styledAttributes.recycle()

        initialize()
    }

    private fun getFloatFromXml(@StyleableRes styledId: Int, defaultValue: Float): Float {
        return if (styledAttributes.hasValue(styledId)) {
            styledAttributes.getFloat(styledId, defaultValue)
        } else
            defaultValue
    }

    private fun getIntFromXml(@StyleableRes styledId: Int, defaultValue: Int): Int {
        return if (styledAttributes.hasValue(styledId)) {
            styledAttributes.getInt(styledId, defaultValue)
        } else
            defaultValue
    }

    private fun getColorFromXml(context: Context, @StyleableRes styledColor: Int, @ColorRes defaultColor: Int): Int {
        return if (styledAttributes.hasValue(styledColor)) {
            styledAttributes.getColor(
                styledColor,
                ContextCompat.getColor(context, defaultColor)
            )
        } else
            ContextCompat.getColor(context, defaultColor)
    }

    private lateinit var topPointer: View
    private lateinit var bottomPointer: View


    private fun initialize() {

        mScrollView.isHorizontalScrollBarEnabled = false
        mScrollView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 45F.dpAsPixels())
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL.and(Gravity.TOP)
        layoutParams.setMargins(0, 2F.dpAsPixels(), 0, 2F.dpAsPixels())

        mScrollView.layoutParams = layoutParams
        orientation = VERTICAL

        topPointer = getPointer()
        addView(topPointer)

        addView(mScrollView)

        bottomPointer = getPointer()
        addView(bottomPointer)

        val container = LinearLayout(context)

        mScrollView.addView(container)

        multiplier = (if (rulerSystem == METRIC_SYSTEM) 1 else 8)

        for (i in 0..rulerMaxValue) {

            val view =
                LayoutInflater.from(context).inflate(R.layout.units, null) as ConstraintLayout

            changeViewColor(view.mainUnitBar, mainUnitBarColor)
            changeViewColor(view.middleBar, middleUnitBarColor)
            changeViewColor(view.quarterUnitBar, quarterUnitBarColor)
            changeViewColor(view.threeQuartersBar, threeQuartersUnitBarColor)

            changeViewColor(view.bar1, normalUnitBarColor)
            changeViewColor(view.bar3, normalUnitBarColor)
            changeViewColor(view.bar4, normalUnitBarColor)
            changeViewColor(view.bar6, normalUnitBarColor)
            changeViewColor(view.bar8, normalUnitBarColor)
            changeViewColor(view.bar9, normalUnitBarColor)

            view.mainUnit.setTextColor(mainUnitTextColor)
            view.middleUnit.setTextColor(middleUnitTextColor)
            view.quarterUnit.setTextColor(quarterUnitTextColor)
            view.threeQuartersUnit.setTextColor(threeQuartersUnitTextColor)

            val value = (i * rulerSystem) + rulerMinValue

            view.mainUnit.text = String.format("%d", value)
            view.middleUnit.text = String.format("%d", 5)

            view.setBackgroundColor(rulerBackgroundColor)


            if (rulerSystem != METRIC_SYSTEM) { // imperial

                view.bar4.visibility = View.GONE
                view.bar8.visibility = View.GONE
                view.quarterUnitBar.layoutParams.height =
                    resources.getDimension(R.dimen.quarter_bar_height).toInt()

                view.threeQuartersBar.layoutParams.height =
                    resources.getDimension(R.dimen.quarter_bar_height).toInt()

                if (isRtl()) {
                    view.middleUnit.text = String.format("%d\\%d", 1, 2)
                    view.quarterUnit.text = String.format("%d\\%d", 1, 4)
                    view.threeQuartersUnit.text = String.format("%d\\%d", 3, 4)
                } else
                    view.middleUnit.text = "1/2"
                view.quarterUnit.visibility = View.VISIBLE
                view.threeQuartersUnit.visibility = View.VISIBLE
            }


            container.addView(view)

            if (value >= rulerMaxValue) {
                view.removeViews(2, view.childCount - 2)
                break
            }
        }

        when (defaultPosition) {
            START_POSITION -> scrollTo(startPosition())
            MIDDLE_POSITION -> scrollTo(middlePosition())
            END_POSITION -> scrollTo(endPosition())
            else -> scrollTo(customPosition())
        }

        container.addView(mLeftSpacer, 0)
        container.addView(mRightSpacer)
    }

    private fun changeViewColor(item: View, color: Int) {
        val gradientDrawable = item.background as GradientDrawable
        gradientDrawable.mutate()
        gradientDrawable.setColor(color)
        item.background = gradientDrawable
    }

    private fun customPosition(newPosition: Float = defaultPosition) = ((newPosition - rulerMinValue) * step * multiplier) + step

    private fun startPosition() = step.toFloat()

    private fun middlePosition() =
        (((rulerMaxValue - rulerMinValue) / 2F) * multiplier * step) + step

    private fun endPosition() = ((rulerMaxValue - rulerMinValue) * multiplier * 1F * step) + step

    private fun getPointer(): View {
        val pointer = ImageView(context)
        val shape = ContextCompat.getDrawable(context, R.drawable.pointer_line) as GradientDrawable
        shape.mutate()
        shape.setColor(rulerPointerColor)
        pointer.setImageDrawable(shape)
        val params = LayoutParams(2F.dpAsPixels(), 6F.dpAsPixels())
        params.gravity = Gravity.CENTER_HORIZONTAL
        pointer.layoutParams = params
        return pointer
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        val cx = width / 2
        val leftRtl = getLeftRtl(cx)
        val rightRtl = getRightRtl(cx)

        topPointer.layout(leftRtl, topPointer.top, rightRtl, topPointer.bottom)

        bottomPointer.layout(leftRtl, bottomPointer.top, rightRtl, bottomPointer.bottom)

        val leftParams = mLeftSpacer.layoutParams
        leftParams.width = width / 2
        mLeftSpacer.layoutParams = leftParams

        val rightParams = mRightSpacer.layoutParams
        rightParams.width = width / 2
        mRightSpacer.layoutParams = rightParams

    }

    private fun getLeftRtl(cx: Int): Int {
        return if (isRtl())
            cx - 9.5F.dpAsPixels()
        else
            cx - 2.5F.dpAsPixels()
    }

    private fun getRightRtl(cx: Int): Int {
        return if (isRtl())
            cx - 6.5F.dpAsPixels()
        else
            cx + .5F.dpAsPixels()
    }

    private var listener: ObservableHorizontalScrollView.OnScrollChangedListener? = null

    fun setOnScrollChangedListener(
        listener: ObservableHorizontalScrollView.OnScrollChangedListener?,
        throttleMillis: Long = 0L
    ) {
        this.listener = listener
        mScrollView.setOnScrollChangedListener(listener, throttleMillis)
    }

    fun setOnScrollChangedListener(listener: (x: Int) -> Unit, throttleMillis: Long = 0L) {
        this.listener = object : ObservableHorizontalScrollView.OnScrollChangedListener {
            override fun onScrollChanged(x: Int) {
                listener(x)
            }
        }
        mScrollView.setOnScrollChangedListener(this.listener, throttleMillis)
    }

    private var runnable: Runnable? = null
    private fun scrollTo(x: Float) {
        mScrollView.removeCallbacks(runnable)
        runnable = Runnable {
            ObjectAnimator.ofInt(mScrollView, "scrollX", x.roundToInt()).setDuration(1).start();
        }
        mScrollView.post(runnable)
    }

    private fun overScrolled(value: Float) = value > rulerMaxValue


    private fun underScrolled(value: Float) = value < rulerMinValue


    fun getReading(xValue: Int): Float {
        val nearestMark = when {
            approachesNextStep(xValue) -> xValue / step
            else -> (xValue - step) / step
        }

        val correctedMark = (nearestMark * step) + step

        val value = nearestMark / multiplier * 1F + rulerMinValue

        Log.e("ManoO nearestMark", nearestMark.toString())
        Log.e("ManoO xValue", xValue.toString())

        when {
            underScrolled(value) -> scrollTo(startPosition())
            overScrolled(value) -> scrollTo(endPosition())
            else -> scrollTo(correctedMark.toFloat())
        }
        this.value = value

        if (isRtl()) {
            return abs(rulerMaxValue - value + rulerMinValue)
        }

        return value
    }

    private fun isRtl() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_RTL

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("SUPER_STATE", super.onSaveInstanceState())
        bundle.putFloat("value", value)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            value = state.getFloat("value")
            moveTo(value)
            return super.onRestoreInstanceState(state.getParcelable<Parcelable>("SUPER_STATE"))
        }
        return super.onRestoreInstanceState(state)
    }

    private fun approachesNextStep(xValue: Int) = xValue % step > (step * .5)

    private fun Float.dpAsPixels(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale).toInt()
    }

    fun moveTo(newPosition: Float) {
        scrollTo(customPosition(newPosition))
    }
}