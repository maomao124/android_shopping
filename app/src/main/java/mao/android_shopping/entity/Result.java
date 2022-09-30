package mao.android_shopping.entity;

import android.graphics.Bitmap;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping.entity
 * Class(类名): Result
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/30
 * Time(创建时间)： 13:46
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class Result
{
    /**
     * 结果
     */
    boolean result;

    /**
     * 位图
     */
    Bitmap bitmap;

    /**
     * Is result boolean.
     *
     * @return the boolean
     */
    public boolean isResult()
    {
        return result;
    }

    /**
     * Sets result.
     *
     * @param result the result
     * @return the result
     */
    public Result setResult(boolean result)
    {
        this.result = result;
        return this;
    }

    /**
     * Gets bitmap.
     *
     * @return the bitmap
     */
    public Bitmap getBitmap()
    {
        return bitmap;
    }

    /**
     * Sets bitmap.
     *
     * @param bitmap the bitmap
     * @return the bitmap
     */
    public Result setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        return this;
    }
}
