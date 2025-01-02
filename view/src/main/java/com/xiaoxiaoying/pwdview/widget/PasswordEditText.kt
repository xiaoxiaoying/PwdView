package com.xiaoxiaoying.pwdview.widget

import android.content.Context
import android.graphics.*
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue

import androidx.appcompat.widget.AppCompatEditText
import com.xiaoxiaoying.pwdview.R
import com.xiaoxiaoying.pwdview.utils.dipToPix
import kotlin.math.abs
import kotlin.math.min


class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        const val INPUT_TYPE_PASS = 0
        const val INPUT_TYPE_NUMBER = 1

        const val MODE_DIALOG = 0
        const val MODE_ACTIVITY = 1

        private const val PWD_TYPE_ORIGIN = 0
        private const val PWD_TYPE_STAR = 1

        private const val KEYBOARD_TYPE_CUSTOM = 0
        private const val KEYBOARD_TYPE_SYSTEM = 1

    }

    // 画笔-->绘制背景框
    private val mRectPaint = Paint()

    // 背景颜色
    private val backPaint = Paint()

    // 画笔--> 绘制密码
    private val mPasswordPaint = Paint()

    // 光标画笔
    private val mCursorPaint = Paint()

    // 一个密码所占的宽度
    private var mPasswordItemWidth = 0

    // 密码的个数默认为6位数
    private var mPasswordNumber = 6

    // 背景边框颜色
    private var mBgColor = Color.parseColor("#d1d2d6")

    // 背景边框大小
    private var mBgSize = 1

    // 背景边框圆角大小
    private var mBgCorner = context.dipToPix(4)

    // 分割线的颜色
    private var mDivisionLineColor = mBgColor

    // 分割线的大小
    private var mDivisionLineSize = 1

    // 密码圆点的颜色
    private var mPasswordColor = Color.parseColor("#000000")

    private var mPwdErrorColor = Color.RED

    private var backgroundColor = Color.TRANSPARENT

    private var mCursorColor = mPasswordColor

    private var keyboardType: Int = KEYBOARD_TYPE_CUSTOM

    // 密码圆点的半径大小
    private var mPasswordRadius = 4

    /**
     * 密码输入完毕需要一个接口回调出去
     */
    var onPasswordFullCall: (String) -> Unit = {}

    private var isPwdFull = false

    private var mInputType: Int = INPUT_TYPE_PASS
    var mode: Int = MODE_DIALOG
        set(value) {
            if (mode == value)
                return
            field = value
            removeCallbacks(cursorRun)
            postInvalidate()
        }
    private var space: Int = context.dipToPix(4)

    private var pwdType = PWD_TYPE_ORIGIN
    private var isCursorDraw = false


    /**
     * 密码输入完成是否在末尾
     */
    var endHasCursor: Boolean = false
        set(value) {
            if (field == value)
                return
            field = value
            postInvalidate()
        }

    private val cursorRun = Runnable {
        postInvalidate()
    }

    /**
     * 密码输入错误
     */
    var isPasswordError: Boolean = false
        set(value) {
            if (field == value)
                return
            field = value
            val color = if (isPasswordError)
                mPwdErrorColor
            else mPasswordColor
            removeCallbacks(cursorRun)
            mPasswordPaint.color = color
            postInvalidate()
        }

    /**
     * 初始化属性
     */
    private fun initAttributeSet(context: Context, attrs: AttributeSet?) {
        val array = context.obtainStyledAttributes(attrs, R.styleable.PasswordEditText)
        // 获取大小
        mDivisionLineSize = array.getDimension(
            R.styleable.PasswordEditText_divisionLineSize,
            dip2px(mDivisionLineSize).toFloat()
        ).toInt()
        mPasswordRadius = array.getDimension(
            R.styleable.PasswordEditText_passwordRadius,
            dip2px(mPasswordRadius).toFloat()
        ).toInt()
        mBgSize = array.getDimension(
            R.styleable.PasswordEditText_backgroundBorderSize,
            dip2px(mBgSize).toFloat()
        ).toInt()
        mBgCorner = array.getDimension(R.styleable.PasswordEditText_bgCorner, mBgCorner.toFloat())
            .toInt()
        // 获取颜色
        mBgColor = array.getColor(R.styleable.PasswordEditText_backgroundBorderColor, mBgColor)
        mCursorColor = array.getColor(R.styleable.PasswordEditText_cursorColor, mCursorColor)
        mDivisionLineColor =
            array.getColor(R.styleable.PasswordEditText_divisionLineColor, mDivisionLineColor)
        mPasswordColor = array.getColor(R.styleable.PasswordEditText_passwordColor, mPasswordColor)
        mPwdErrorColor = array.getColor(R.styleable.PasswordEditText_errorColor, mPwdErrorColor)

        mInputType = array.getInt(R.styleable.PasswordEditText_input_type, INPUT_TYPE_PASS)
        pwdType = array.getInt(R.styleable.PasswordEditText_pwd_type, PWD_TYPE_ORIGIN)

        mPasswordNumber = array.getInt(R.styleable.PasswordEditText_passwordNumber, mPasswordNumber)

        mode = array.getInt(R.styleable.PasswordEditText_mode, MODE_DIALOG)
        space = array.getDimensionPixelSize(R.styleable.PasswordEditText_interval, space)
        endHasCursor = array.getBoolean(R.styleable.PasswordEditText_endHasCursor, endHasCursor)
        backgroundColor =
            array.getColor(R.styleable.PasswordEditText_backgroundColor, backgroundColor)


        keyboardType = array.getInt(R.styleable.PasswordEditText_keyboard_type, keyboardType)
        array.recycle()

        filters = arrayOf(InputFilter.LengthFilter(mPasswordNumber))
    }

    /**
     * 初始化画笔
     */
    private fun initPaint() {
        //初始化绘制边框的画笔

        mRectPaint.isAntiAlias = true
        mRectPaint.isDither = true
        mRectPaint.color = mBgColor

        backPaint.isAntiAlias = true
        backPaint.isDither = true
        backPaint.color = backgroundColor
        backPaint.style = Paint.Style.FILL

        //初始化密码远点的画笔
        mPasswordPaint.isAntiAlias = true
        mPasswordPaint.isDither = true
        mPasswordPaint.color = mPasswordColor

        mCursorPaint.isAntiAlias = true
        mCursorPaint.isDither = true
        mCursorPaint.color = mCursorColor
        mCursorPaint.strokeWidth = dip2Px(1f)
        mCursorPaint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        //不需要调用super.onDraw(canvas); 为什么不需要呢？你去调用试试看，就明白为什么了
        //super.onDraw(canvas);
        //一个密码的宽度
        mPasswordItemWidth =
            (width - mBgSize * 2 - (mPasswordNumber - 1) * mDivisionLineSize) / mPasswordNumber
        drawRect(canvas)
        if (mode == MODE_DIALOG && space < 1) {
            drawDivisionLine(canvas)
        }

        drawPassword(canvas)
        drawCursor(canvas)
        //获取输入的密码
        val password = text.toString().trim { it <= ' ' }
        if (password.length == mPasswordNumber && !isPwdFull) {
            onPasswordFullCall(password)
            isPwdFull = true
        }
    }


    private fun getItemWidth(): Float {
        return (width - (mPasswordNumber - 1) * space) / mPasswordNumber.toFloat()
    }

    /**
     * 绘制背景框
     *
     * @param canvas 画布
     */
    private fun drawRect(canvas: Canvas) {
        if (mode == MODE_ACTIVITY) {
            val lineWidth = getItemWidth()
            val length = text?.toString()?.length ?: 0
            mRectPaint.strokeWidth = mBgSize.toFloat()
            mRectPaint.style = Paint.Style.FILL_AND_STROKE
            repeat(mPasswordNumber) {
                val startY = (height - mBgSize).toFloat()
                val startX = it * (lineWidth + space)
                val endX = startX + lineWidth
                mRectPaint.color = if (length == it) {
                    mCursorColor
                } else {
                    mBgColor
                }
                canvas.drawLine(startX, startY, endX, startY, mRectPaint)
            }
            return
        }

        mRectPaint.strokeWidth = mBgSize.toFloat()
        //画空心
        mRectPaint.style = Paint.Style.STROKE

        if (space > 0) {
            val lineWidth = getItemWidth()
            val endY = height - mBgSize
            repeat(mPasswordNumber) {
                val startX = it * (lineWidth + space)
                val endX = startX + lineWidth
                val rect =
                    RectF(startX + mBgSize, mBgSize.toFloat(), endX - mBgSize, endY.toFloat())
                if (mBgCorner == 0) {
                    canvas.drawRect(rect, backPaint)
                    canvas.drawRect(rect, mRectPaint)
                } else {
                    canvas.drawRoundRect(rect, mBgCorner.toFloat(), mBgCorner.toFloat(), backPaint)
                    canvas.drawRoundRect(rect, mBgCorner.toFloat(), mBgCorner.toFloat(), mRectPaint)
                }
            }
            return
        }
        //矩形
        val rect = RectF(
            (mBgSize shr 1).toFloat(),
            (mBgSize shr 1).toFloat(),
            (width - (mBgSize shr 1)).toFloat(),
            (height - (mBgSize shr 1)).toFloat()
        )
        if (mBgCorner == 0) {
            canvas.drawRect(rect, mRectPaint)
        } else {
            canvas.drawRoundRect(rect, mBgCorner.toFloat(), mBgCorner.toFloat(), mRectPaint)
        }

    }

    /**
     * 绘制分割线
     *
     * @param canvas 画布
     */
    private fun drawDivisionLine(canvas: Canvas) {
        mRectPaint.strokeWidth = mDivisionLineSize.toFloat()
        for (i in 0 until mPasswordNumber - 1) {
            val startX = mBgSize + (i + 1) * mPasswordItemWidth + i * mDivisionLineSize
            val startY = 0
            val endY = height - mBgSize
            canvas.drawLine(
                startX.toFloat(),
                startY.toFloat(),
                startX.toFloat(),
                endY.toFloat(),
                mRectPaint
            )
        }
    }

    /**
     * 绘制圆点密码
     *
     * @param canvas 画布
     */
    private fun drawPassword(canvas: Canvas) {
        //圆点密码是实行的
        mPasswordPaint.style = Paint.Style.FILL
        val length = text?.toString()?.length ?: 0

        val str = text.toString()
        val itemWidth = getItemWidth()
        repeat(length) {

            val cx = abs(itemWidth * it + it * space + itemWidth / 2F)
            val cy = height / 2F
            when (mInputType) {
                INPUT_TYPE_PASS -> {
                    when (pwdType) {
                        PWD_TYPE_STAR -> {
                            val content = "*"
                            mPasswordPaint.textSize = dip2Px(22f)
                            val bounds = Rect()
                            mPasswordPaint.getTextBounds(content, 0, content.length, bounds)

                            canvas.drawText(
                                content,
                                cx - bounds.width(),
                                cy + bounds.height(),
                                mPasswordPaint
                            )
                        }

                        else -> {

                            canvas.drawCircle(
                                cx,
                                cy,
                                mPasswordRadius.toFloat(),
                                mPasswordPaint
                            )
                        }
                    }

                }

                INPUT_TYPE_NUMBER -> {
                    mPasswordPaint.textSize = dip2Px(18f)
                    val bounds = Rect()
                    val content = str[it].toString()
                    mPasswordPaint.getTextBounds(content, 0, content.length, bounds)
                    canvas.drawText(
                        content, cx - bounds.width(),
                        cy + bounds.height() / 2, mPasswordPaint
                    )
                }
            }
        }

    }

    /**
     * 绘制光标一闪一动的
     */
    private fun drawCursor(canvas: Canvas) {

        if (!isFocused) {
            return
        }
        removeCallbacks(cursorRun)
        val length = text?.toString()?.length ?: 0
        if (!endHasCursor && mPasswordNumber - length <= 0) {
            return
        }
        if (isPasswordError)
            return
        if (!isCursorDraw) {

            val endLength = if (endHasCursor && length == mPasswordNumber) {
                length - 1
            } else {
                length
            }

            val lastX =
                mPasswordItemWidth / 2.toFloat() + if (endHasCursor && length == mPasswordNumber) {
                    dip2Px(13f)
                } else {
                    0F
                }
            val cx =
                mBgSize + endLength * mDivisionLineSize + endLength * mPasswordItemWidth + lastX
            val cy = height / 2
            val div10 = dip2Px(10f)
            canvas.drawLine(
                cx,
                cy - div10,
                cx,
                cy + div10,
                mCursorPaint
            )
        }
        isCursorDraw = !isCursorDraw

        postDelayed(
            cursorRun,
            700
        )
    }

    /**
     * 添加密码
     */
    fun addPassword(number: String) {
        if (TextUtils.isEmpty(number)) {
            return
        }
        //把密码取取出来
        var password = text?.toString()?.trim { it <= ' ' } ?: ""
        if (password.length <= mPasswordNumber) {
            isPasswordError = false
            //密码叠加
            isPwdFull = false
            password += number
            removeCallbacks(cursorRun)
            setText(password)
        }
    }

    /**
     * 删除密码
     */
    fun deletePassword() {
        var password = text.toString().trim { it <= ' ' }
        if (TextUtils.isEmpty(password)) {
            return
        }
        isPasswordError = false
        password = password.substring(0, password.length - 1)
        removeCallbacks(cursorRun)
        setText(password)
    }


    /**
     * 清空密码
     */
    fun clearPassword() {
        removeCallbacks(cursorRun)
        isPasswordError = false
        setText("")
    }

    private fun dip2px(dip: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip.toFloat(), resources.displayMetrics
        ).toInt()
    }

    private fun dip2Px(dip: Float): Float = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dip, resources.displayMetrics
    )

    override fun setInputType(type: Int) {
        if (keyboardType == KEYBOARD_TYPE_SYSTEM) {
            super.setInputType(type)
        }

    }

    init {
        initAttributeSet(context, attrs)
        //不显示光标
        isCursorVisible = false
        //不弹出系统软键盘
        if (keyboardType == KEYBOARD_TYPE_CUSTOM) {
            super.setInputType(InputType.TYPE_NULL)
        }
        //背景去掉
        background = null
        initPaint()
        setOnFocusChangeListener { _, _ ->
            removeCallbacks(cursorRun)
            postInvalidate()
        }
    }

    /**
     * 类型
     * * [INPUT_TYPE_PASS] 密码类型
     * * [INPUT_TYPE_NUMBER] 数字类型
     */
    fun setPwdInputType(inputType: Int) {
        if (mInputType == inputType)
            return
        mInputType = inputType
        postInvalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        removeCallbacks(cursorRun)
    }

}