package com.milter.www.customviewgroupforblog;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2016/8/14.
 */
public class StaggerLayout extends ViewGroup {
    public static final String TAG = "StaggerLayout" ;

    /*
    首先，定义好我们的四个构造方法，注意，ViewGroup的构造方法与上篇中的
    自定义View AnalogClock遵循相同的最佳实践。
     */

    //第一个构造方法
    public StaggerLayout(Context context) {
        this(context, null);
    }
    //第二个构造方法
    public StaggerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    //第三个构造方法
    public StaggerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }
    //第四个构造方法
    public StaggerLayout(Context context, AttributeSet attrs,
                         int defStyleAttr, int defStyleRes) {

        super(context, attrs, defStyleAttr, defStyleRes);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        /*
        maxHeight和maxWidth就是我们最后计算汇总后的ViewGroup需要的宽和高。
        用来报告给ViewGroup的parent。

        在计算maxWidth时，我们首先简单地把所有子View的宽度加起来，
        如果该ViewGroup所有的子View的宽度加起来都没有
        超过parent的宽度限制，那么我们把该ViewGroup的measured宽度设为maxWidth，
        如果最后的结果超过了parent的宽度限制，我们就设置measured宽度为parent的限制宽度，
        这是通过对maxWidth进行resolveSizeAndState处理得到的。

        对于maxHeight，在每一行中找出最高的一个子View，然后把所有行中最高的子View加起来。
        这里我们在报告maxHeight时，也进行一次resolveSizeAndState处理。


         */
        int maxHeight = 0;
        int maxWidth = 0;

        /*
            mLeftHeight表示当前行已有子View中最高的那个的高度。当需要换行时，把它的值加到maxHeight上，
            然后将新行中第一个子View的高度设置给它。
            mLeftWidth表示当前行中所有子View已经占有的宽度，当新加入一个子View导致该宽度超过parent的
            宽度限制时，增加maxHeight的值，同时将新行中第一个子View的宽度设置给它。

         */

        int mLeftHeight = 0;
        int mLeftWidth = 0;

        final int count = getChildCount();
        Log.d(TAG,"Child count is " + count);
        final int widthSize =  MeasureSpec.getSize(widthMeasureSpec);

        Log.d(TAG,"widthSize in Measure is :"+ widthSize);


        // 遍历我们的子View，并测量它们，根据它们要求的尺寸进而计算我们的StaggerLayout需要的尺寸。
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            //可见性为gone的子View，我们就当它不存在。
            if (child.getVisibility() == GONE)
                continue;

            // 测量该子View
            measureChild(child, widthMeasureSpec, heightMeasureSpec);

            //简单地把所有子View的测量宽度相加。
            maxWidth += child.getMeasuredWidth();
            mLeftWidth += child.getMeasuredWidth();

            //这里判断是否需将index 为i的子View放入下一行，如果需要，就要更新我们的maxHeight，
            //rowCount和mLeftHeight。
            if (mLeftWidth > widthSize) {
                maxHeight += mLeftHeight;
                mLeftWidth = child.getMeasuredWidth();
                mLeftHeight = child.getMeasuredHeight();

            } else {

                mLeftHeight = Math.max(mLeftHeight, child.getMeasuredHeight());
            }

        }

        //这里把最后一行的高度加上，注意不要遗漏。
        maxHeight += mLeftHeight;

        //这里将宽度和高度与Google为我们设定的建议最低宽高对比，确保我们要求的尺寸不低于建议的最低宽高。
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        //报告我们最终计算出的宽高。
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, 0),
                resolveSizeAndState(maxHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

            final int count = getChildCount();


            //childLeft和childTop代表在StaggerLayout的坐标系中，能够用来layout子View的区域的
            //左上角的顶点的坐标。
            final int childLeft = getPaddingLeft();
            final int childTop = getPaddingTop();

            //childRight代表在StaggerLayout的坐标系中，能够用来layout子View的区域的
            //右边那条边的坐标。
            final int childRight = r -  l - getPaddingRight();


        /*
          curLeft和curTop代表StaggerLayout准备用来layout子View的起点坐标，这个点的坐标随着
          子View一个一个地被layout，在不断变化，有点像数据库中的Cursor，指向下一个可用区域。
          maxHeight代表当前行中最高的子View的高度，当需要换行时，curTop要加上该值，以确保新行中
          的子View不会与上一行中的子View发生重叠。
         */
           int curLeft, curTop, maxHeight;

            maxHeight = 0;
            curLeft = childLeft;
            curTop = childTop;

            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);

                if (child.getVisibility() == GONE)
                    return;

                int curWidth, curHeight;
                curWidth = child.getMeasuredWidth();
                curHeight = child.getMeasuredHeight();
                //用来判断是否应当将该子View放到下一行
                if (curLeft + curWidth >= childRight) {
                    /*
                    需要移到下一行时，更新curLeft和curTop的值，使它们指向下一行的起点
                    同时将maxHeight清零。
                     */
                    curLeft = childLeft;
                    curTop += maxHeight;
                    maxHeight = 0;
                }
                //所有的努力只为了这一次layout
                child.layout(curLeft, curTop, curLeft + curWidth, curTop + curHeight);
                //更新maxHeight和curLeft
                if (maxHeight < curHeight)
                    maxHeight = curHeight;
                curLeft += curWidth;
            }
    }


}
