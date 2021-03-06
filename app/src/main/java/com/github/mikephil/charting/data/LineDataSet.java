
package com.github.mikephil.charting.data;

import com.github.mikephil.charting.formatter.DefaultFillFormatter;
import com.github.mikephil.charting.formatter.FillFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

public class LineDataSet extends LineRadarDataSet<com.github.mikephil.charting.data.Entry> {

    /** List representing all colors that are used for the circles */
    private java.util.List<Integer> mCircleColors = null;

    /** the color of the inner circles */
    private int mCircleColorHole = android.graphics.Color.WHITE;

    /** the radius of the circle-shaped value indicators */
    private float mCircleSize = 8f;

    /** sets the intensity of the cubic lines */
    private float mCubicIntensity = 0.2f;

    /** the path effect of this DataSet that makes dashed lines possible */
    private android.graphics.DashPathEffect mDashPathEffect = null;

    /** formatter for customizing the position of the fill-line */
    private FillFormatter mFillFormatter = new DefaultFillFormatter();

    /** if true, drawing circles is enabled */
    private boolean mDrawCircles = true;

    /** if true, cubic lines are drawn instead of linear */
    private boolean mDrawCubic = false;

    private boolean mDrawCircleHole = true;

    public LineDataSet(java.util.List<com.github.mikephil.charting.data.Entry> yVals, String label) {
        super(yVals, label);

        // mCircleSize = Utils.convertDpToPixel(4f);
        // mLineWidth = Utils.convertDpToPixel(1f);

        mCircleColors = new java.util.ArrayList<Integer>();

        // default colors
        // mColors.add(Color.rgb(192, 255, 140));
        // mColors.add(Color.rgb(255, 247, 140));
        mCircleColors.add(android.graphics.Color.rgb(140, 234, 255));
    }

    @Override
    public com.github.mikephil.charting.data.DataSet<Entry> copy() {

        java.util.List<Entry> yVals = new java.util.ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        com.github.mikephil.charting.data.LineDataSet copied = new com.github.mikephil.charting.data.LineDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mCircleSize = mCircleSize;
        copied.mCircleColors = mCircleColors;
        copied.mDashPathEffect = mDashPathEffect;
        copied.mDrawCircles = mDrawCircles;
        copied.mDrawCubic = mDrawCubic;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }

    /**
     * Sets the intensity for cubic lines (if enabled). Max = 1f = very cubic,
     * Min = 0.05f = low cubic effect, Default: 0.2f
     * 
     * @param intensity
     */
    public void setCubicIntensity(float intensity) {

        if (intensity > 1f)
            intensity = 1f;
        if (intensity < 0.05f)
            intensity = 0.05f;

        mCubicIntensity = intensity;
    }

    /**
     * Returns the intensity of the cubic lines (the effect intensity).
     * 
     * @return
     */
    public float getCubicIntensity() {
        return mCubicIntensity;
    }

    /**
     * sets the size (radius) of the circle shpaed value indicators, default
     * size = 4f
     * 
     * @param size
     */
    public void setCircleSize(float size) {
        mCircleSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the circlesize
     */
    public float getCircleSize() {
        return mCircleSize;
    }

    /**
     * Enables the line to be drawn in dashed mode, e.g. like this
     * "- - - - - -". THIS ONLY WORKS IF HARDWARE-ACCELERATION IS TURNED OFF.
     * Keep in mind that hardware acceleration boosts performance.
     * 
     * @param lineLength the length of the line pieces
     * @param spaceLength the length of space in between the pieces
     * @param phase offset, in degrees (normally, use 0)
     */
    public void enableDashedLine(float lineLength, float spaceLength, float phase) {
        mDashPathEffect = new android.graphics.DashPathEffect(new float[] {
                lineLength, spaceLength
        }, phase);
    }

    /**
     * Disables the line to be drawn in dashed mode.
     */
    public void disableDashedLine() {
        mDashPathEffect = null;
    }

    /**
     * Returns true if the dashed-line effect is enabled, false if not.
     * 
     * @return
     */
    public boolean isDashedLineEnabled() {
        return mDashPathEffect == null ? false : true;
    }

    /**
     * returns the DashPathEffect that is set for this DataSet
     * 
     * @return
     */
    public android.graphics.DashPathEffect getDashPathEffect() {
        return mDashPathEffect;
    }

    /**
     * set this to true to enable the drawing of circle indicators for this
     * DataSet, default true
     * 
     * @param enabled
     */
    public void setDrawCircles(boolean enabled) {
        this.mDrawCircles = enabled;
    }

    /**
     * returns true if drawing circles for this DataSet is enabled, false if not
     * 
     * @return
     */
    public boolean isDrawCirclesEnabled() {
        return mDrawCircles;
    }

    /**
     * If set to true, the linechart lines are drawn in cubic-style instead of
     * linear. This affects performance! Default: false
     * 
     * @param enabled
     */
    public void setDrawCubic(boolean enabled) {
        mDrawCubic = enabled;
    }

    /**
     * returns true if drawing cubic lines is enabled, false if not.
     * 
     * @return
     */
    public boolean isDrawCubicEnabled() {
        return mDrawCubic;
    }

    /** ALL CODE BELOW RELATED TO CIRCLE-COLORS */

    /**
     * returns all colors specified for the circles
     * 
     * @return
     */
    public java.util.List<Integer> getCircleColors() {
        return mCircleColors;
    }

    /**
     * Returns the color at the given index of the DataSet's circle-color array.
     * Performs a IndexOutOfBounds check by modulus.
     * 
     * @param index
     * @return
     */
    public int getCircleColor(int index) {
        return mCircleColors.get(index % mCircleColors.size());
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     * 
     * @param colors
     */
    public void setCircleColors(java.util.List<Integer> colors) {
        mCircleColors = colors;
    }

    /**
     * Sets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. Make sure that the colors
     * are already prepared (by calling getResources().getColor(...)) before
     * adding them to the DataSet.
     * 
     * @param colors
     */
    public void setCircleColors(int[] colors) {
        this.mCircleColors = ColorTemplate.createColors(colors);
    }

    /**
     * ets the colors that should be used for the circles of this DataSet.
     * Colors are reused as soon as the number of Entries the DataSet represents
     * is higher than the size of the colors array. You can use
     * "new String[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     * 
     * @param colors
     */
    public void setCircleColors(int[] colors, android.content.Context c) {

        java.util.List<Integer> clrs = new java.util.ArrayList<Integer>();

        for (int color : colors) {
            clrs.add(c.getResources().getColor(color));
        }

        mCircleColors = clrs;
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     * 
     * @param color
     */
    public void setCircleColor(int color) {
        resetCircleColors();
        mCircleColors.add(color);
    }

    /**
     * resets the circle-colors array and creates a new one
     */
    public void resetCircleColors() {
        mCircleColors = new java.util.ArrayList<Integer>();
    }

    /**
     * Sets the color of the inner circle of the line-circles.
     * 
     * @param color
     */
    public void setCircleColorHole(int color) {
        mCircleColorHole = color;
    }

    /**
     * Returns the color of the inner circle.
     * 
     * @return
     */
    public int getCircleHoleColor() {
        return mCircleColorHole;
    }

    /**
     * Set this to true to allow drawing a hole in each data circle.
     * 
     * @param enabled
     */
    public void setDrawCircleHole(boolean enabled) {
        mDrawCircleHole = enabled;
    }

    public boolean isDrawCircleHoleEnabled() {
        return mDrawCircleHole;
    }

    /**
     * Sets a custom FillFormatter to the chart that handles the position of the
     * filled-line for each DataSet. Set this to null to use the default logic.
     *
     * @param formatter
     */
    public void setFillFormatter(FillFormatter formatter) {

        if (formatter == null)
            mFillFormatter = new DefaultFillFormatter();
        else
            mFillFormatter = formatter;
    }

    /**
     * Returns the FillFormatter that is set for this DataSet.
     * @return
     */
    public FillFormatter getFillFormatter() {
        return mFillFormatter;
    }
}
