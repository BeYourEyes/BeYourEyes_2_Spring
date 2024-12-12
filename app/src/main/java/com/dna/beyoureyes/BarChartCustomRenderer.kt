package com.dna.beyoureyes

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.core.text.color
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.buffer.BarBuffer
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler

class BarChartCustomRenderer (
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?)
    : HorizontalBarChartRenderer(chart, animator, viewPortHandler) {
        private var mRightRadius = 5f
        private var mLeftRadius = 5f

    companion object {
        private const val Y_MINIMUM_RATIO = 0.13f
        private const val Y_MAXIMUM_RATIO = 0.85f
        private const val TEXT_POS_ADJUSTMENT = 0.21f
        private const val Y_VALUE_THRESHOLD = 20f
    }

    fun setRightRadius(mRightRadius: Float) {
        this.mRightRadius = mRightRadius
    }

    fun setLeftRadius(mLeftRadius: Float) {
        this.mLeftRadius = mLeftRadius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val isSingleDataSetAndEntry =
            mChart.barData.dataSetCount == 1 && mChart.barData.entryCount == 1
        if (isSingleDataSetAndEntry && mRightRadius > 0) {
            val trans = mChart.getTransformer(dataSet.axisDependency)
            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY

            // draw the bar shadow before the values
            if (mChart.isDrawBarShadowEnabled) {
                drawBarShadow(c, dataSet, trans)
            }

            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.feed(dataSet)

            trans.pointValuesToPixel(buffer.buffer)

            drawBarBorder(c, dataSet, buffer)
            drawBarFill(c, dataSet, buffer)

        } else {
            super.drawDataSet(c, dataSet, index)
        }
    }


    override fun drawValue(c: Canvas?, valueText: String?, x: Float, y: Float, color: Int) {
        val isSingleDataSetAndEntry =
            mChart.barData.dataSetCount == 1 && mChart.barData.dataSets.size == 1
        if (isSingleDataSetAndEntry) {
            val yValue = mChart.barData.dataSets[0].getEntryForIndex(0).y
            val barValueColor = mChart.barData.dataSets[0].color
            if (yValue < Y_VALUE_THRESHOLD) {
                val textColor = if (yValue == 0f) barValueColor else color
                super.drawValue(
                    c, valueText,
                    Y_MINIMUM_RATIO * mViewPortHandler.contentRight() * TEXT_POS_ADJUSTMENT,
                    y, textColor )
            } else if (yValue > 100f ) {
                super.drawValue(c, valueText, mViewPortHandler.contentRight() * Y_MAXIMUM_RATIO, y, color)
            } else {
                super.drawValue(c, valueText, x, y, color)
            }
        } else {
            super.drawValue(c, valueText, x, y, color)
        }
    }

    private fun drawBarShadow(c:Canvas, dataSet: IBarDataSet, trans:Transformer) {
        mShadowPaint.color = dataSet.barShadowColor
        val barData = mChart.barData
        val barWidth = barData.barWidth
        val barWidthHalf = barWidth / 2.0f
        var x: Float
        val mBarShadowRectBuffer = RectF()
        val e = dataSet.getEntryForIndex(0)
        x = e.x
        mBarShadowRectBuffer.top = x - barWidthHalf
        mBarShadowRectBuffer.bottom = x + barWidthHalf
        trans.rectValueToPixel(mBarShadowRectBuffer)
        if (mViewPortHandler.isInBoundsTop(mBarShadowRectBuffer.bottom)) {
            if (mViewPortHandler.isInBoundsBottom(mBarShadowRectBuffer.top)){
                mBarShadowRectBuffer.left = mViewPortHandler.contentLeft()
                mBarShadowRectBuffer.right = mViewPortHandler.contentRight()
                c.drawRoundRect(mBarShadowRectBuffer, mRightRadius, mRightRadius, mShadowPaint)
            }
        }
    }

    private fun drawBarBorder(c: Canvas, dataSet: IBarDataSet, buffer:BarBuffer) {
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)

        val drawBorder = dataSet.barBorderWidth > 0f
        val barValue = mChart.barData.dataSets[0]

        if (drawBorder) {
            val barY = barValue.getEntryForIndex(0).y
            val barRight = if ( barY < Y_VALUE_THRESHOLD && barY != 0f)
                Y_MINIMUM_RATIO * mViewPortHandler.contentRight()
            else if (barY > 100f ) mViewPortHandler.contentRight()
            else buffer.buffer[2]
            c.drawRect(
                buffer.buffer[0],
                buffer.buffer[1], barRight,
                buffer.buffer[3], mBarBorderPaint
            )
        }
    }

    private fun drawBarFill(c: Canvas, dataSet: IBarDataSet, buffer:BarBuffer) {
        val isSingleColor = dataSet.colors.size == 1

        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }

        if (mViewPortHandler.isInBoundsTop(buffer.buffer[3]) &&
            mViewPortHandler.isInBoundsBottom(buffer.buffer[1])){

            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(0)
            }
            val barValue = mChart.barData.dataSets[0]
            val barY = barValue.getEntryForIndex(0).y
            val barRight = if ( barY < Y_VALUE_THRESHOLD && barY != 0f)
                Y_MINIMUM_RATIO * mViewPortHandler.contentRight()
            else if (barY > 100f ) mViewPortHandler.contentRight()
            else buffer.buffer[2]

            c.drawRoundRect(
                RectF(
                    buffer.buffer[0],
                    buffer.buffer[1], barRight,
                    buffer.buffer[3]), mRightRadius, mRightRadius, mRenderPaint
            )
        }
    }
}