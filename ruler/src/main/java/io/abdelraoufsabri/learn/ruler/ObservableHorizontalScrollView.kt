package io.abdelraoufsabri.learn.ruler

import android.content.Context
import android.util.AttributeSet
import android.widget.HorizontalScrollView
import io.abdelraoufsabri.learn.ruler.ObservableHorizontalScrollView.OnScrollChangedListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.security.InvalidParameterException
import java.util.concurrent.TimeUnit


/**
 * A [HorizontalScrollView] with an [OnScrollChangedListener] interface
 * to notify listeners of scroll position changes.
 */
class ObservableHorizontalScrollView : HorizontalScrollView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)

    private var mOnScrollChangedListener: OnScrollChangedListener? = null

    /**
     * Interface definition for a callback to be invoked with the scroll
     * position changes.
     */
    interface OnScrollChangedListener {
        /**
         * Called when the scroll position of `view` changes.
         *
         * @param view The view whose scroll position changed.
         * @param l Current horizontal scroll origin.
         * @param t Current vertical scroll origin.
         */
        fun onScrollChanged(x: Int)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val fadingLength = (measuredWidth * 0.5F).toInt()
        setFadingEdgeLength(fadingLength)
        isHorizontalFadingEdgeEnabled = true
    }

    override fun getRightFadingEdgeStrength(): Float {
        return 0F
    }

    override fun getLeftFadingEdgeStrength(): Float {
        return 2F
    }

    private var disposable: Disposable? = null
    private val source by lazy { PublishSubject.create<Int>() }
    private var throttleMillis = 0L

    fun setOnScrollChangedListener(l: OnScrollChangedListener?, throttleMillis: Long = 0L) {
        if (throttleMillis < 0) {
            throw InvalidParameterException("Parameter throttleMillis can be less than 0")
        }
        this.throttleMillis = throttleMillis
        mOnScrollChangedListener = l

        disposable?.dispose()
        if (throttleMillis != 0L) {
            disposable = source.throttleLatest(throttleMillis, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    mOnScrollChangedListener?.onScrollChanged(it)
                }
        }
    }

    override fun onScrollChanged(x: Int, y: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(x, y, oldl, oldt)
        if (throttleMillis == 0L) {
            mOnScrollChangedListener?.onScrollChanged(x)
        } else
            source.onNext(x)
    }
}