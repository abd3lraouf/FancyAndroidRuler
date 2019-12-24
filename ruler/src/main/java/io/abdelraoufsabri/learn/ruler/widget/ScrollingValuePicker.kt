package io.abdelraoufsabri.learn.ruler.widget

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import io.abdelraoufsabri.learn.ruler.ObservableHorizontalScrollView
import io.abdelraoufsabri.learn.ruler.R
import kotlinx.android.synthetic.main.units.view.*
import kotlin.math.abs
import kotlin.math.roundToInt


class ScrollingValuePicker : LinearLayout {

    private var firstLaunchSkipped: Boolean = false
    private var oldValue: Int = 0
    private val mLeftSpacer: View
    private val mRightSpacer: View
    private val mScrollView: ObservableHorizontalScrollView
    private val rulerAccentColor: Int
    private val rulerPrimaryColor: Int
    private val rulerBackgroundColor: Int
    private val rulerPointerOutlineColor: Int
    private val rulerPointerBackgroundColor: Int
    val rulerMaxValue: Int
    val rulerMinValue: Int
    private val rulerPlaceValue: Int
    private val rulerDigits: Int
    private val rulerPointerThickness: Int

    private enum class DefaultPosition { BEGINNING, MIDDLE, END }

    private val defaultPosition: DefaultPosition
    val step by lazy { 16F.dpAsPixels() }

    constructor(context: Context) : super(context) {
        mLeftSpacer = View(context)
        mRightSpacer = View(context)
        mScrollView = ObservableHorizontalScrollView(context)

        rulerAccentColor = Color.BLACK
        rulerPrimaryColor = Color.GRAY
        rulerBackgroundColor = Color.WHITE
        rulerPointerOutlineColor = Color.GRAY
        rulerPointerBackgroundColor = Color.WHITE
        rulerMaxValue = 100
        rulerMinValue = 0
        rulerPlaceValue = 10
        rulerDigits = 10
        rulerPointerThickness = 2F.dpAsPixels()
        defaultPosition = DefaultPosition.MIDDLE


        initialize()
    }

    constructor(context: Context, attr: AttributeSet) : super(context, attr) {
        mLeftSpacer = View(context)
        mRightSpacer = View(context)
        mScrollView = ObservableHorizontalScrollView(context)

        val styledAttributes =
            context.obtainStyledAttributes(attr, R.styleable.ScrollingValuePicker, 0, 0)

        rulerAccentColor =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerAccentColor)) {
                styledAttributes.getColor(
                    R.styleable.ScrollingValuePicker_rulerAccentColor,
                    Color.BLACK
                )
            } else
                Color.BLACK

        rulerPrimaryColor =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerPrimaryColor)) {
                styledAttributes.getColor(
                    R.styleable.ScrollingValuePicker_rulerPrimaryColor,
                    Color.GRAY
                )
            } else
                Color.GRAY

        rulerBackgroundColor =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerBackgroundColor)) {
                styledAttributes.getColor(
                    R.styleable.ScrollingValuePicker_rulerBackgroundColor,
                    Color.LTGRAY
                )
            } else
                Color.LTGRAY

        rulerPointerOutlineColor =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerPointerOutlineColor)) {
                styledAttributes.getColor(
                    R.styleable.ScrollingValuePicker_rulerPointerOutlineColor,
                    Color.GRAY
                )
            } else
                Color.GRAY

        rulerPointerBackgroundColor =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerPointerBackgroundColor)) {
                styledAttributes.getColor(
                    R.styleable.ScrollingValuePicker_rulerPointerBackgroundColor,
                    Color.WHITE
                )
            } else
                Color.WHITE

        rulerMaxValue =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerMaxValue)) {
                styledAttributes.getInt(R.styleable.ScrollingValuePicker_rulerMaxValue, 10)
            } else
                10

        rulerPlaceValue =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerPlaceValue)) {
                styledAttributes.getInt(R.styleable.ScrollingValuePicker_rulerPlaceValue, 1)
            } else
                1

        rulerMinValue =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerMinValue)) {
                styledAttributes.getInt(R.styleable.ScrollingValuePicker_rulerMinValue, 0)
            } else
                0

        rulerPointerThickness =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerPointerThickness)) {
                styledAttributes.getInt(R.styleable.ScrollingValuePicker_rulerPointerThickness, 2)
            } else
                2

        rulerDigits = if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_rulerDigits)) {
            styledAttributes.getInt(R.styleable.ScrollingValuePicker_rulerDigits, 10)
        } else
            10

        defaultPosition =
            if (styledAttributes.hasValue(R.styleable.ScrollingValuePicker_defaultPosition)) {
                val value =
                    styledAttributes.getInt(R.styleable.ScrollingValuePicker_defaultPosition, 1)
                DefaultPosition.values()[value]
            } else
                DefaultPosition.MIDDLE

        styledAttributes.recycle()

        initialize()
    }

    private fun initialize() {
        mScrollView.isHorizontalScrollBarEnabled = false
        mScrollView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

        mScrollView.layoutParams =
            LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 55F.dpAsPixels())
        orientation = VERTICAL

        addView(getPointer())

        addView(mScrollView)

        addView(getPointer())

        // Create a horizontal (by default) LinearLayout as our child container
        val container = LinearLayout(context)
        mScrollView.addView(container)

        // Our actual content is an ImageView, but doesn't need to be

        for (i in 0..rulerMaxValue) {
            val view =
                LayoutInflater.from(context).inflate(R.layout.units, null) as ConstraintLayout

            val value = (i * rulerPlaceValue) + rulerMinValue
            view.main_unit.text = value.toString()
            view.main_unit.setTextColor(rulerAccentColor)

            view.sub_unit.text = (value + 5).toString()
            view.sub_unit.setTextColor(rulerAccentColor)

            view.setBackgroundColor(rulerBackgroundColor)


            container.addView(view)

            if (value >= rulerMaxValue) {
                view.removeViews(2, 10)
                break
            }
        }

        when (defaultPosition) {
            DefaultPosition.BEGINNING -> scrollTo(startPosition())
            DefaultPosition.MIDDLE -> scrollTo(middlePosition())
            DefaultPosition.END -> scrollTo(endPosition())
        }
        // Create the left and right spacers, don't worry about their dimensions, yet.

        container.addView(mLeftSpacer, 0)
        container.addView(mRightSpacer)
    }

    private fun startPosition() = step.toFloat()

    private fun middlePosition() = ((rulerMaxValue - rulerMinValue) / 2F + 1) * step

    private fun endPosition() = (rulerMaxValue - rulerMinValue + 1) * startPosition()

    private fun getPointer(): View {
        val pointer = ImageView(context)
        val shape = ContextCompat.getDrawable(context, R.drawable.pointer_line) as Drawable

        pointer.setImageDrawable(shape)
        val params = LayoutParams(rulerPointerThickness.toFloat().dpAsPixels(), 8F.dpAsPixels())
        params.setMargins(0, 3F.dpAsPixels(), 0, 3F.dpAsPixels())
        params.gravity = Gravity.CENTER_HORIZONTAL
        pointer.layoutParams = params

        return pointer
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        if (changed) {
            // Layout the spacers now that we are measured

            val leftParams = mLeftSpacer.layoutParams
            leftParams.width = width / 2
            mLeftSpacer.layoutParams = leftParams

            val rightParams = mRightSpacer.layoutParams
            rightParams.width = width / 2
            mRightSpacer.layoutParams = rightParams
        }
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

    private fun scrollTo(x: Float) {
        mScrollView.post {
            ObjectAnimator.ofInt(mScrollView, "scrollX", x.roundToInt()).setDuration(100L).start();
        }
    }

    fun getReading(xValue: Int): Int {

        val value = when {
            approachesNextStep(xValue) -> nextStep(xValue)
            else -> prevStep(xValue)
        }

        val correctedX = finalizeValue(value)

        when {
            underScrolled(value) -> scrollTo(startPosition())
            overScrolled(value) -> scrollTo(endPosition())
            else -> scrollTo(correctedX.toFloat())
        }

        if (valueChanged(value) && firstLaunchSkipped) {
            playTickSound(value)
        }

        oldValue = value
        firstLaunchSkipped = true

        return value
    }


    private fun overScrolled(value: Int) = value > rulerMaxValue

    private fun underScrolled(value: Int) = value < rulerMinValue

    private fun playTickSound(value: Int) {
        val soundPlayTimes = 8
        val diff = oldValue - value

        val abs = abs(diff) % soundPlayTimes

        for (i in 0..abs) {
            playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    private fun valueChanged(value: Int) = oldValue != value

    private fun finalizeValue(value: Int) = ((value - rulerMinValue) * step) + step

    private fun prevStep(xValue: Int) = nextStep(xValue - step)

    private fun nextStep(xValue: Int) = rulerMinValue + xValue / step

    private fun approachesNextStep(xValue: Int) = xValue % step > (step * .5)

    private fun Float.dpAsPixels(): Int {
        val scale = context.resources.displayMetrics.density
        return (this * scale).toInt()
    }

}