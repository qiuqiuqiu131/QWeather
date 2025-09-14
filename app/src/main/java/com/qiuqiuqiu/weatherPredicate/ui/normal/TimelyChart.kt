package com.qiuqiuqiu.weatherPredicate.ui.normal

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import com.github.mikephil.charting.renderer.LineChartRenderer
import com.qiuqiuqiu.weatherPredicate.model.TimelyChartModel
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

// 数据结构
data class ChartPoint(
    val time: OffsetDateTime,
    val icon: Bitmap,
    val value: Float
)

// ValueFormatter，显示时间
class ChartPointTimeFormatter(private val data: List<ChartPoint>) : ValueFormatter() {
    @SuppressLint("NewApi")
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    @SuppressLint("NewApi")
    private val dayFormatter = DateTimeFormatter.ofPattern("MM/dd")

    @SuppressLint("NewApi")
    override fun getFormattedValue(value: Float): String {
        val idx = value.toInt()
        if (idx in data.indices) {
            val dateTime = data[idx].time
            return if (dateTime.hour == 0 && dateTime.minute == 0) {
                dateTime.format(dayFormatter)
            } else {
                dateTime.format(timeFormatter)
            }
        }
        return ""
    }
}

// 渲染器，绘制垂直线、交点和 bitmap
class CustomLineRendererWithIcon(
    chart: LineChart,
    private val chartPoints: List<ChartPoint>,
    private val onIntersectionUpdate: ((Entry) -> Unit)? = null,
    private val lineColor: Int = Color.RED,
    private val intersectionCircleColor1: Int = Color.GRAY,
    private val intersectionCircleColor2: Int = Color.GRAY,
    private val lineWidth: Float = 10f,
    private val intersectionCircleRadius: Float = 15f,
    private val intersectionCircleStroke: Float = 5f,
    var highlightEntry: Entry? = null,
    var lineRatio: Float = 0.1f
) : LineChartRenderer(chart, chart.animator, chart.viewPortHandler) {

    var currentSelectedEntry: Entry? = null

    private val linePaint = Paint().apply {
        color = lineColor
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
    }
    private val intersectionPaint1 = Paint().apply {
        color = intersectionCircleColor1
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val intersectionPaint2 = Paint().apply {
        color = intersectionCircleColor2
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    private val intersectionStrokePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = intersectionCircleStroke
        isAntiAlias = true
    }

    override fun drawExtras(c: Canvas?) {
        super.drawExtras(c)
        c?.let {
            drawCenterLineAndIntersection(it)
            drawIconsOnTop(it)
        }
    }

    private fun drawCenterLineAndIntersection(canvas: Canvas) {
        val viewPortHandler = mViewPortHandler
        val data = mChart.data
        if (data != null && data.dataSetCount > 0) {
            // 计算垂直线的像素位置和 xValue
            val centerX = viewPortHandler.contentLeft() + viewPortHandler.contentWidth() * lineRatio
            val pixels = floatArrayOf(centerX, 0f)
            val firstDataSet = data.getDataSetByIndex(0)
            val transformer = mChart.getTransformer(firstDataSet.axisDependency)
            transformer.pixelsToValue(pixels)
            val centerXValue = pixels[0]

            var updatedEntry: Entry? = null

            // 遍历所有数据集
            for (dataSetIndex in 0 until data.dataSetCount) {
                val dataSet = data.getDataSetByIndex(dataSetIndex) as ILineDataSet
                val t = mChart.getTransformer(dataSet.axisDependency)
                val entry = highlightEntry ?: findClosestEntry(dataSet, centerXValue)
                entry?.let {
                    val point = floatArrayOf(it.x, it.y)
                    t.pointValuesToPixel(point)
                    val left = viewPortHandler.contentLeft()
                    val right = viewPortHandler.contentRight()
                    if (point[0] in left..right) {
                        // 只画一次垂直线
                        if (dataSetIndex == 0) {
                            canvas.drawLine(
                                point[0],
                                viewPortHandler.contentTop(),
                                point[0],
                                viewPortHandler.contentBottom(),
                                linePaint
                            )
                        }
                        canvas.drawCircle(
                            point[0],
                            point[1],
                            intersectionCircleRadius,
                            if (dataSetIndex % 2 == 0) intersectionPaint1 else intersectionPaint2
                        )
                        canvas.drawCircle(
                            point[0],
                            point[1],
                            intersectionCircleRadius,
                            intersectionStrokePaint
                        )
                    }
                    // 只更新第一个数据集的 entry
                    if (dataSetIndex == 0) updatedEntry = it
                }
            }
            if (currentSelectedEntry != updatedEntry) {
                currentSelectedEntry = updatedEntry
                onIntersectionUpdate?.invoke(updatedEntry!!)
            }
        }
    }

    private fun drawIconsOnTop(canvas: Canvas) {
        val data = mChart.data ?: return
        val dataSet = data.getDataSetByIndex(0) ?: return
        val transformer = mChart.getTransformer(dataSet.axisDependency)
        val topY = mViewPortHandler.contentTop() + -66 // 10 为向下偏移，可调整
        val left = mViewPortHandler.contentLeft()
        val right = mViewPortHandler.contentRight()
        for (i in 0 until dataSet.entryCount) {
            val entry = dataSet.getEntryForIndex(i)
            val point = floatArrayOf(entry.x, entry.y)
            transformer.pointValuesToPixel(point)
            if (point[0] < left || point[0] > right) continue // 只绘制可见区域
            val chartPoint = chartPoints.getOrNull(i) ?: continue
            val iconBitmap = chartPoint.icon
            val iconX = point[0] - iconBitmap.width / 2
            canvas.drawBitmap(iconBitmap, iconX, topY, null)
        }
    }

    private fun findClosestEntry(dataSet: ILineDataSet, xValue: Float): Entry? {
        var closestEntry: Entry? = null
        var minDistance = Float.MAX_VALUE
        for (i in 0 until dataSet.entryCount) {
            val entry = dataSet.getEntryForIndex(i)
            val distance = kotlin.math.abs(entry.x - xValue)
            if (distance < minDistance) {
                minDistance = distance
                closestEntry = entry
            }
        }
        return closestEntry
    }
}

// 生成 LineData
fun generateLineDataFromChartPoints(
    chartModel: TimelyChartModel,
    primaryColor: Int,
    secondaryColor: Int,
    endColor: Int
): LineData {
    val entries1 = chartModel.data1.mapIndexed { idx, point ->
        Entry(idx.toFloat(), point.value)
    }
    val dataSet1 = LineDataSet(entries1, chartModel.dataName1).apply {
        color = primaryColor
        valueTextColor = Color.BLACK
        lineWidth = 2.5f
        circleRadius = 0f
        setDrawValues(false)
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        setDrawFilled(true)
        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(secondaryColor, endColor)
        )
        fillDrawable = gradientDrawable
    }

    if (chartModel.data2 != null) {
        val entries2 = chartModel.data2.mapIndexed { idx, point ->
            Entry(idx.toFloat(), point.value)
        }
        val dataSet2 = LineDataSet(entries2, chartModel.dataName2).apply {
            color = secondaryColor
            valueTextColor = Color.BLACK
            lineWidth = 2.5f
            circleRadius = 0f
            setDrawValues(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            enableDashedLine(20f, 10f, 0f)
        }
        return LineData(dataSet1, dataSet2)
    } else {
        return LineData(dataSet1)
    }
}

// Composable 适配新数据结构
@Composable
fun CustomLineChartView(
    chartModel: TimelyChartModel,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    onEntryLocked: (Int) -> Unit = {}
) {
    var rendererRef: CustomLineRendererWithIcon? by remember { mutableStateOf(null) }
    var dataRef: LineData? by remember { mutableStateOf(null) }
    var canTranslate: Boolean by remember { mutableStateOf(true) }

    var model: TimelyChartModel by remember { mutableStateOf(chartModel) }

    AndroidView(
        modifier = modifier.padding(start = 4.dp, top = 14.dp, bottom = 10.dp),
        factory = { ctx ->
            LineChart(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                setBackgroundColor(Color.WHITE)
                description.isEnabled = false
                setTouchEnabled(true)
                setScaleEnabled(false)
                setPinchZoom(false)
                isDragEnabled = true
                isHighlightPerTapEnabled = false
                isHighlightPerDragEnabled = false
                isDoubleTapToZoomEnabled = false
                overScrollMode = ViewGroup.OVER_SCROLL_ALWAYS
                isNestedScrollingEnabled = false
                setOnTouchListener { v, event ->
                    v.parent?.requestDisallowInterceptTouchEvent(true)
                    if (event.action == MotionEvent.ACTION_UP) {
                        v.performClick()
                    }
                    false // 让 chart 正常处理事件
                }

                xAxis.apply {
                    valueFormatter = ChartPointTimeFormatter(chartModel.data1)
                    position = XAxis.XAxisPosition.BOTTOM
                    textColor = 0X66000000.toInt()
                    axisLineColor = 0X11000000.toInt()

                    setDrawGridLines(false)
                    setDrawAxisLine(true)
                    granularity = 3f
                    textSize = 10f
                }
                axisLeft.apply {
                    textColor = 0X66000000.toInt()
                    textSize = 10f
                    axisLineColor = 0X22000000.toInt()
                    gridColor = 0X22000000.toInt()
                    setDrawGridLines(true)
                    setLabelCount(6, true)
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            return "${value.toInt()}${chartModel.type.yAxisUnit}"
                        }
                    }
                }

                axisRight.isEnabled = false

                if (chartModel.data2 != null) {
                    legend.isEnabled = true
                    legend.textSize = 12f
                    legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // 位置
                    legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                    legend.form = Legend.LegendForm.LINE
                }


                val customRenderer = CustomLineRendererWithIcon(
                    this,
                    chartPoints = chartModel.data1,
                    lineColor = 0X33000000.toInt(),
                    lineWidth = 6f,
                    intersectionCircleStroke = 7f,
                    intersectionCircleColor1 = chartModel.type.primaryColor,
                    intersectionCircleColor2 = chartModel.type.secondaryColor,
                    onIntersectionUpdate = { entry ->
                        val idx = entry.x.toInt()
                        onEntryLocked.invoke(idx)
                    }
                )
                customRenderer.highlightEntry = null
                customRenderer.lineRatio = 0.05f

                renderer = customRenderer
                rendererRef = customRenderer

                dataRef =
                    generateLineDataFromChartPoints(
                        chartModel,
                        chartModel.type.primaryColor,
                        chartModel.type.secondaryColor,
                        chartModel.type.endColor
                    )
                this.data = dataRef
                setVisibleXRangeMaximum(11f)
                moveViewToX(0f)

                onChartGestureListener = object : OnChartGestureListener {
                    override fun onChartGestureStart(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {
                        canTranslate = true
                    }

                    override fun onChartLongPressed(me: MotionEvent?) {}
                    override fun onChartDoubleTapped(me: MotionEvent?) {}
                    override fun onChartFling(
                        me1: MotionEvent?,
                        me2: MotionEvent?,
                        velocityX: Float,
                        velocityY: Float
                    ) {
                    }

                    override fun onChartSingleTapped(me: MotionEvent?) {
                        me ?: return
                        val chart = this@apply
                        val x = me.x
                        val y = me.y
                        val h = chart.getHighlightByTouchPoint(x, y)
                        if (h != null) {
                            val entry = chart.data.getEntryForHighlight(h)
                            rendererRef?.highlightEntry = entry
                            val visibleRange = chart.highestVisibleX - chart.lowestVisibleX
                            val targetCenterX = entry.x - visibleRange / 2f
                            val safeTargetX = if (targetCenterX < 0f) 0f else targetCenterX
                            chart.moveViewToAnimated(
                                safeTargetX,
                                entry.y,
                                chart.data.getDataSetByIndex(h.dataSetIndex).axisDependency,
                                400
                            )
                            chart.invalidate()
                        }
                    }

                    override fun onChartGestureEnd(
                        me: MotionEvent?,
                        lastPerformedGesture: ChartTouchListener.ChartGesture?
                    ) {
                    }

                    override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {}
                    override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                        if (canTranslate) {
                            rendererRef?.highlightEntry = null
                            val totalRange = xChartMax - xChartMin
                            val visibleRange = highestVisibleX - lowestVisibleX
                            val progress = if (totalRange > visibleRange) {
                                (lowestVisibleX - xChartMin) / (totalRange - visibleRange)
                            } else {
                                0f
                            }
                            rendererRef?.lineRatio = 0.05f + 0.9f * progress.coerceIn(0f, 1f)
                            invalidate()
                        }
                    }
                }
                invalidate()
            }
        },
        update = { chart ->
            if (model != chartModel) {
                model = chartModel
                chart.apply {
                    isNestedScrollingEnabled = false
                    axisLeft.apply {
                        textColor = 0X66000000.toInt()
                        textSize = 10f
                        axisLineColor = 0X22000000.toInt()
                        gridColor = 0X22000000.toInt()
                        setDrawGridLines(true)
                        setLabelCount(6, true)
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return "${value.toInt()}${chartModel.type.yAxisUnit}"
                            }
                        }
                    }

                    if (chartModel.data2 != null) {
                        legend.isEnabled = true
                        legend.textSize = 12f
                        legend.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM // 位置
                        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        legend.form = Legend.LegendForm.LINE
                    } else
                        legend.isEnabled = false

                    val customRenderer = CustomLineRendererWithIcon(
                        this,
                        chartPoints = chartModel.data1,
                        lineColor = 0X33000000.toInt(),
                        lineWidth = 6f,
                        intersectionCircleStroke = 7f,
                        intersectionCircleColor1 = chartModel.type.primaryColor,
                        intersectionCircleColor2 = chartModel.type.secondaryColor,
                        onIntersectionUpdate = { entry ->
                            val idx = entry.x.toInt()
                            onEntryLocked.invoke(idx)
                        }
                    )
                    customRenderer.highlightEntry = null
                    customRenderer.lineRatio = 0.05f

                    renderer = customRenderer
                    rendererRef = customRenderer

                    dataRef =
                        generateLineDataFromChartPoints(
                            chartModel,
                            chartModel.type.primaryColor,
                            chartModel.type.secondaryColor,
                            chartModel.type.endColor
                        )
                    this.data = dataRef
                    notifyDataSetChanged()

                    setVisibleXRangeMaximum(11f)
                    moveViewToX(0f)
                    invalidate()
                }
                Log.i("Chart", "Chart data updated, entry count: ${chartModel.data1.size}")
            }

            rendererRef?.let {
                val entry = dataRef?.getDataSetByIndex(0)?.getEntryForIndex(selectedIndex)
                if (entry != null && entry != it.currentSelectedEntry) {
                    canTranslate = false
                    it.highlightEntry = entry
                    chart.apply {
                        // 测算ratio
                        val totalRange = xChartMax - xChartMin
                        val visibleRange = highestVisibleX - lowestVisibleX
                        val progress = if (totalRange > visibleRange) {
                            (lowestVisibleX - xChartMin) / (totalRange - visibleRange)
                        } else {
                            0f
                        }
                        it.lineRatio = 0.05f + 0.9f * progress.coerceIn(0f, 1f)

                        // 滚动动画
                        val targetCenterX = entry.x - visibleRange / 2f
                        val safeTargetX = if (targetCenterX < 0f) 0f else targetCenterX
                        moveViewToAnimated(
                            safeTargetX,
                            entry.y,
                            dataRef?.getDataSetByIndex(0)?.axisDependency,
                            750
                        )
                        invalidate()
                    }
                }
            }
        }
    )
}