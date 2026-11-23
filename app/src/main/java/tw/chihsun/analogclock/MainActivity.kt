package tw.chihsun.analogclock

import android.app.Activity
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.cos

/** Offline clock: system time only, no permissions, no network dependencies. */
class MainActivity : Activity() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var clock: ClockView
    private val ticker = object : Runnable {
        override fun run() {
            clock.invalidate()
            // Keep ticks aligned with the current second even after returning from background.
            val delay = 1000L - (System.currentTimeMillis() % 1000L)
            handler.postDelayed(this, delay)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.rgb(16, 21, 30)
        window.navigationBarColor = Color.rgb(16, 21, 30)
        clock = ClockView()
        setContentView(FrameLayout(this).apply {
            setBackgroundColor(Color.rgb(16, 21, 30))
            addView(clock, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                Gravity.CENTER
            ))
        })
    }

    override fun onResume() {
        super.onResume()
        handler.removeCallbacks(ticker)
        ticker.run()
    }

    override fun onPause() {
        handler.removeCallbacks(ticker)
        super.onPause()
    }

    private inner class ClockView : View(this) {
        private val p = Paint(Paint.ANTI_ALIAS_FLAG)
        private val white = Color.rgb(243, 246, 250)
        private val muted = Color.rgb(156, 175, 196)
        private val accent = Color.rgb(251, 120, 106)
        private val regular: Typeface = try {
            Typeface.createFromAsset(assets, "maruko_regular.ttf")
        } catch (_: Exception) { Typeface.create("sans-serif-rounded", Typeface.NORMAL) }
        private val timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.ROOT)
        private val dateFormat = DateTimeFormatter.ofPattern("yyyy年M月d日 EEEE", Locale.TAIWAN)

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val w = width.toFloat()
            val h = height.toFloat()
            val now = LocalDateTime.now()
            val size = min(w * 0.83f, h * 0.47f)
            val radius = size / 2f
            val cx = w / 2f
            val cy = h * 0.36f
            val scale = radius / 125f

            p.style = Paint.Style.FILL
            p.color = Color.rgb(28, 38, 52)
            canvas.drawCircle(cx, cy, radius, p)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 2f * scale
            p.color = Color.rgb(54, 69, 87)
            canvas.drawCircle(cx, cy, radius, p)
            p.style = Paint.Style.FILL

            for (i in 0 until 60) {
                val angle = Math.toRadians(i * 6.0 - 90)
                val major = i % 5 == 0
                p.color = if (major) white else Color.rgb(90, 108, 126)
                p.strokeWidth = (if (major) 2.5f else 1f) * scale
                p.strokeCap = Paint.Cap.ROUND
                val outer = radius * 0.976f
                val inner = radius * (if (major) 0.84f else 0.93f)
                canvas.drawLine(
                    cx + cos(angle).toFloat() * inner,
                    cy + sin(angle).toFloat() * inner,
                    cx + cos(angle).toFloat() * outer,
                    cy + sin(angle).toFloat() * outer,
                    p
                )
            }
            p.typeface = regular
            p.color = white
            p.textAlign = Paint.Align.CENTER
            p.textSize = 18f * scale
            for (n in 1..12) {
                val angle = Math.toRadians(n * 30.0 - 90)
                val tx = cx + cos(angle).toFloat() * radius * 0.728f
                val ty = cy + sin(angle).toFloat() * radius * 0.728f - (p.ascent() + p.descent()) / 2f
                canvas.drawText(n.toString(), tx, ty, p)
            }

            val hourAngle = ((now.hour % 12) + now.minute / 60f + now.second / 3600f) * 30f
            val minuteAngle = (now.minute + now.second / 60f) * 6f
            val secondAngle = now.second * 6f
            hand(canvas, cx, cy, radius * 0.47f, hourAngle, 7f * scale, white)
            hand(canvas, cx, cy, radius * 0.68f, minuteAngle, 4f * scale, white)
            hand(canvas, cx, cy, radius * 0.79f, secondAngle, 2f * scale, accent)
            p.color = accent
            p.style = Paint.Style.FILL
            canvas.drawCircle(cx, cy, 6f * scale, p)

            val dividerY = cy + radius + min(h * 0.075f, 62f * resources.displayMetrics.density)
            p.color = Color.rgb(52, 66, 84)
            p.strokeWidth = resources.displayMetrics.density
            canvas.drawLine(w * 0.12f, dividerY, w * 0.88f, dividerY, p)

            p.typeface = regular
            p.textAlign = Paint.Align.CENTER
            p.color = white
            p.textSize = min(w * 0.125f, 48f * resources.displayMetrics.scaledDensity)
            canvas.drawText(now.format(timeFormat), cx,
                dividerY + p.textSize * 1.55f, p)

            p.color = muted
            p.textSize = min(w * 0.048f, 18f * resources.displayMetrics.scaledDensity)
            canvas.drawText(now.format(dateFormat), cx,
                dividerY + min(w * 0.125f, 48f * resources.displayMetrics.scaledDensity) * 1.55f +
                    p.textSize * 1.7f, p)
        }

        private fun hand(canvas: Canvas, x: Float, y: Float, length: Float,
                         degrees: Float, stroke: Float, color: Int) {
            val a = Math.toRadians(degrees.toDouble() - 90.0)
            p.style = Paint.Style.STROKE
            p.strokeWidth = stroke
            p.strokeCap = Paint.Cap.ROUND
            p.color = color
            canvas.drawLine(x, y, x + cos(a).toFloat() * length,
                y + sin(a).toFloat() * length, p)
            p.style = Paint.Style.FILL
        }
    }
}
