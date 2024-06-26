package ru.surf.learn2invest.ui.components.chart

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import ru.surf.learn2invest.R

/**
 * Docs?
 */
class CustomMarkerView(context: Context, layoutResource: Int) :
    MarkerView(context, layoutResource) {
    private var tvContent: TextView = findViewById(R.id.tvContent)

    // Вызывается каждый раз при перерисовке MarkerView, можно использовать для обновления контента (пользовательского интерфейса)
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tvContent.text = e?.y?.toBigDecimal()?.toPlainString() ?: ""
        // Выполнит необходимое позиционирование
        super.refreshContent(e, highlight)
    }

    private var mOffset: MPPointF? = null

    override fun getOffset(): MPPointF {
        if (mOffset == null) {
            // Центрировать маркер горизонтально и вертикально
            mOffset = MPPointF(-(width / 2f), -height.toFloat())
        }
        return mOffset ?: MPPointF()
    }
}