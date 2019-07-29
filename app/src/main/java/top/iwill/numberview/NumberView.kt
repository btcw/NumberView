package top.iwill.numberview

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * @description:
 * @author: btcw
 * @date: 2019/7/26
 */
class NumberView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var viewWidth = 0f

    private var cornerRadius = 0f

    private val bounds = RectF()

    private val cornerPath = Path()

    private val camera = Camera()

    private var currentText = ""

    private var nextText: String? = null

    private val bgColor = Color.parseColor("#333333")

    private val textColor = Color.parseColor("#CCCCCC")

    private val animator by lazy {
        ObjectAnimator.ofFloat(this, "degree", 0f, -180f).also {
            it.duration = 1000
            it.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {}
                override fun onAnimationEnd(animation: Animator?) {
                    currentText = nextText!!
                }

                override fun onAnimationCancel(animation: Animator?) {}
                override fun onAnimationStart(animation: Animator?) {}
            })
        }
    }

    private var degree = 180f
        set(value) {
            field = value
            invalidate()
        }

    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        paint.textSize = 360f
        paint.textAlign = Paint.Align.CENTER
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        paint.strokeWidth = 3f
        camera.setLocation(0f, 0f, -8 * resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth = height * 3 / 4f
        cornerRadius = viewWidth / 10f
        bounds.set((width - viewWidth) / 2f, 0f, (width + viewWidth) / 2f, height.toFloat())
        cornerPath.reset()
        cornerPath.addRoundRect(bounds, cornerRadius, cornerRadius, Path.Direction.CW)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            //切圆角
            paint.color = bgColor
            it.drawPath(cornerPath, paint)

            //下面画初始值
            paint.color = textColor
            it.save()
            it.clipRect(bounds.left, height / 2f, bounds.right, height.toFloat())
            it.drawText(
                currentText,
                width / 2f,
                height / 2f - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f,
                paint
            )
            it.restore()

            //上面画next值,没有就画初始值
            it.save()
            it.clipRect(bounds.left, 0f, bounds.right, height / 2f)
            it.drawText(
                nextText ?: currentText,
                width / 2f,
                height / 2f - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f,
                paint
            )
            it.restore()

            //旋转视图
            it.translate(width / 2f, height / 2f)
            camera.save()
            camera.rotateX(degree)
            camera.applyToCanvas(canvas)
            camera.restore()
            it.translate(-width / 2f, -height / 2f)

            //--------------绘制旋转之后的视图---------------------------

            //绘制滚动的上半部分的正面部分view
            if (animator.isRunning) {
                it.save()
                if (degree >= -90f) {
//                it.saveLayer(0f, 0f, width.toFloat(), height / 2f,paint)
                    it.clipRect(bounds.left, 0f, bounds.right, height / 2f)
                    //绘制正面
                    paint.color = bgColor
                    it.drawPath(cornerPath, paint)
                    paint.color = textColor
                    it.drawText(
                        currentText,
                        width / 2f,
                        height / 2f - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f,
                        paint
                    )
                } else {
                    //绘制滚动的半部分的背面部分view(翻转后的下半部分view)
//                it.saveLayer(0f, 0f, width.toFloat(), height / 2f,paint)
                    it.clipRect(bounds.left, 0f, bounds.right, height / 2f)
                    it.translate(width / 2f, height / 2f)
                    camera.save()
                    camera.rotateX(-180f)
                    camera.applyToCanvas(canvas)
                    camera.restore()
                    it.translate(-width / 2f, -height / 2f)
                    paint.color = bgColor
                    it.drawPath(cornerPath, paint)
                    paint.color = textColor
                    it.drawText(
                        nextText ?: currentText,
                        width / 2f,
                        height / 2f - (paint.fontMetrics.ascent + paint.fontMetrics.descent) / 2f,
                        paint
                    )
                    it.translate(width / 2f, height / 2f)
                    camera.save()
                    camera.rotateX(180f)
                    camera.applyToCanvas(canvas)
                    camera.restore()
                    it.translate(-width / 2f, -height / 2f)
                }
                it.restore()
            }
            //绘制横线
            paint.color = Color.BLACK
            it.drawLine(bounds.left, height / 2f, bounds.right, height / 2f, paint)
        }
    }

    fun setNextValue(nextValue: String): Boolean {
        if (animator.isRunning.not()) {
            nextText = nextValue
            animator.start()
            return true
        }
        return false
    }

    fun setCurrValue(currValue: String) {
        currentText = currValue
        invalidate()
    }
}