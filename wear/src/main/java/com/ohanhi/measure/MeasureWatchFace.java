package com.ohanhi.measure;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;

/**
 * Created by ohan on 10.4.2015.
 */
public class MeasureWatchFace {
    private static final String TIME_FORMAT_HOURS = "%02d";
    private static final String TIME_FORMAT_MINUTES = "%02d";
    private static final String TIME_FORMAT = TIME_FORMAT_HOURS + "  " + TIME_FORMAT_MINUTES;
    private static final String DATE_FORMAT = "%02d.%02d.";
    private static final int TICK_COUNT = 40;
    private static final float TICK_SPACING = 20f;
    private static final float TICK_HOUR_LENGTH = 20f;
    private static final float TICK_HALF_HOUR_LENGTH = TICK_HOUR_LENGTH * 0.5f;
    private static final float TICK_QUARTER_HOUR_LENGTH = TICK_HOUR_LENGTH * 0.4f;
    private static final float CARD_FEATHER_RADIUS = 5f;

    private final Paint backgroundPaint;
    private final Paint indicatorAmbientModePaint;
    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint tickPaint;
    private final Paint indicatorPaint;
    private final Time time;

    private boolean inAmbientMode = false;

    public static MeasureWatchFace newInstance(Context context) {
        Paint timePaint = new Paint();
        timePaint.setColor(Color.WHITE);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        timePaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        Paint datePaint = new Paint();
        datePaint.setColor(Color.WHITE);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));

        Paint tickPaint = new Paint();
        tickPaint.setARGB(128, 255,255,255);
        tickPaint.setStrokeWidth(2f);
        tickPaint.setStrokeCap(Paint.Cap.ROUND);
        tickPaint.setAntiAlias(true);

        Paint indicatorPaint = new Paint();
        indicatorPaint.setARGB(168, 255,0,0);
        indicatorPaint.setStrokeWidth(2f);
        indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        indicatorPaint.setAntiAlias(true);

        return new MeasureWatchFace(timePaint, datePaint, tickPaint, indicatorPaint, new Time());
    }

    MeasureWatchFace(Paint timePaint, Paint datePaint, Paint tickPaint, Paint indicatorPaint, Time time) {
        this.backgroundPaint = new Paint(Color.BLACK);
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.tickPaint = tickPaint;
        this.indicatorPaint = indicatorPaint;
        this.indicatorAmbientModePaint = indicatorPaint;
        this.time = time;
    }

    public void draw(Canvas canvas, Rect bounds) {
        time.setToNow();
        canvas.drawColor(backgroundPaint.getColor());

        // Draw "measuring tape"

        int width = bounds.width();
        int height = bounds.height();
        int centerX = width / 2;

        drawTicks(canvas, bounds, time.minute / 60f);
        canvas.drawLine(centerX, -1, centerX, height+1, inAmbientMode ? indicatorAmbientModePaint : indicatorPaint);

        // Draw text

        String timeText = String.format(TIME_FORMAT, time.hour, time.minute);
        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

//        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1));
//        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
//        float dateYOffset = computeDateYOffset(dateText, datePaint, bounds);
//        canvas.drawText(dateText, dateXOffset, dateYOffset, datePaint);
    }

    public void draw(Canvas canvas, Rect bounds, Rect cardBounds) {
        draw(canvas, bounds);

        // Draw black rectangle behind the card
        canvas.drawRect(cardBounds.left - CARD_FEATHER_RADIUS,
                cardBounds.top - CARD_FEATHER_RADIUS,
                cardBounds.right + CARD_FEATHER_RADIUS,
                cardBounds.bottom + CARD_FEATHER_RADIUS, backgroundPaint);
    }

    private void drawTicks(Canvas canvas, Rect bounds, float minuteFloat) {
        /*
        minuteFloat is the fraction of the current hour we're at:
            :00 -> 0.0
            :15 -> 0.25
            :30 -> 0.50
            :45 -> 0.75
        */

        // tick points are bottom-aligned on y-axis, and need offset on x-axis
        float[] curTickPoints = new float[TICK_COUNT * 4];
        float centerX = bounds.centerX();
        float offsetY = bounds.centerY();
        float offsetX = (minuteFloat * TICK_SPACING*4)
                + (TICK_COUNT/2 * TICK_SPACING);
        int tickCount = curTickPoints.length / 4;

        for (int i = 0; i < tickCount; i++) {
            for (int j = 0; j < 4; j++) {
                float val = 0;
                switch (j) {
                    case 0: // xStart
                    case 2: // xEnd
                        val = i*TICK_SPACING + centerX - offsetX;
                        break;
                    case 1: // yStart
                        val = 0 + offsetY;
                        break;
                    case 3: // yEnd
                        if (i%4 == 0) val = TICK_HOUR_LENGTH;
                        else if (i%2 == 0) val = TICK_HALF_HOUR_LENGTH;
                        else val = TICK_QUARTER_HOUR_LENGTH;
                        // y is inverted
                        val = -1*val + offsetY;
                        break;
                }
                curTickPoints[ i*4 + j ] = val;
            }
        }

        canvas.drawLines(curTickPoints, tickPaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return (centerY * 0.5f) + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint, Rect watchBounds) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        int textHeight = textBounds.height();
        return  watchBounds.bottom - (textHeight / 2.0f) - 10.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
        tickPaint.setAntiAlias(antiAlias);
        indicatorPaint.setAntiAlias(antiAlias);
    }

    public void setAmbientMode(boolean inAmbientMode) {
        this.inAmbientMode = inAmbientMode;
    }
}
