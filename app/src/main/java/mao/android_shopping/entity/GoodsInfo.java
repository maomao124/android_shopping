package mao.android_shopping.entity;

import java.util.ArrayList;
import java.util.List;

import mao.android_shopping.R;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping.entity
 * Class(类名): GoodsInfo
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 19:48
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class GoodsInfo
{
    /**
     * id
     */
    private int id;
    /**
     * 名字
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    /**
     * 价格
     */
    private float price;
    /**
     * 大图的保存路径
     */
    private String picPath;
    /**
     * 大图的资源编号
     */
    private int pic;


    /**
     * Instantiates a new Goods info.
     */
    public GoodsInfo()
    {

    }

    /**
     * Instantiates a new Goods info.
     *
     * @param id          the id
     * @param name        the name
     * @param description the description
     * @param price       the price
     * @param picPath     the pic path
     * @param pic         the pic
     */
    public GoodsInfo(int id, String name, String description, float price, String picPath, int pic)
    {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.picPath = picPath;
        this.pic = pic;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId()
    {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     * @return the id
     */
    public GoodsInfo setId(int id)
    {
        this.id = id;
        return this;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     * @return the name
     */
    public GoodsInfo setName(String name)
    {
        this.name = name;
        return this;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     * @return the description
     */
    public GoodsInfo setDescription(String description)
    {
        this.description = description;
        return this;
    }

    /**
     * Gets price.
     *
     * @return the price
     */
    public float getPrice()
    {
        return price;
    }

    /**
     * Sets price.
     *
     * @param price the price
     * @return the price
     */
    public GoodsInfo setPrice(float price)
    {
        this.price = price;
        return this;
    }

    /**
     * Gets pic path.
     *
     * @return the pic path
     */
    public String getPicPath()
    {
        return picPath;
    }

    /**
     * Sets pic path.
     *
     * @param picPath the pic path
     * @return the pic path
     */
    public GoodsInfo setPicPath(String picPath)
    {
        this.picPath = picPath;
        return this;
    }

    /**
     * Gets pic.
     *
     * @return the pic
     */
    public int getPic()
    {
        return pic;
    }

    /**
     * Sets pic.
     *
     * @param pic the pic
     * @return the pic
     */
    public GoodsInfo setPic(int pic)
    {
        this.pic = pic;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("name：").append(name).append('\n');
        stringbuilder.append("description：").append(description).append('\n');
        stringbuilder.append("price：").append(price).append('\n');
        stringbuilder.append("picPath：").append(picPath).append('\n');
        stringbuilder.append("pic：").append(pic).append('\n');
        return stringbuilder.toString();
    }

    /**
     * 手机商品的名称数组
     */
    private static final String[] mNameArray =
            {
                    "iPhone11", "Mate30", "小米10", "OPPO Reno3", "vivo X30", "荣耀30S"
            };
    /**
     * 手机商品的描述数组
     */
    private static final String[] mDescArray =
            {
                    "Apple iPhone11 256GB 绿色 4G全网通手机",
                    "华为 HUAWEI Mate30 8GB+256GB 丹霞橙 5G全网通 全面屏手机",
                    "小米 MI10 8GB+128GB 钛银黑 5G手机 游戏拍照手机",
                    "OPPO Reno3 8GB+128GB 蓝色星夜 双模5G 拍照游戏智能手机",
                    "vivo X30 8GB+128GB 绯云 5G全网通 美颜拍照手机",
                    "荣耀30S 8GB+128GB 蝶羽红 5G芯片 自拍全面屏手机"
            };


    /**
     * 手机商品的价格数组
     */
    private static final float[] mPriceArray = {6299, 4999, 3999, 2999, 2998, 2399};
    /**
     * 手机商品的大图数组
     */
    private static final int[] mPicArray =
            {
                    R.drawable.test,
                    R.drawable.test,
                    R.drawable.test,
                    R.drawable.test,
                    R.drawable.test,
                    R.drawable.test
            };

    /**
     * 获取默认的列表
     *
     * @return {@link List}<{@link GoodsInfo}>
     */
    public static List<GoodsInfo> getDefaultList()
    {
        List<GoodsInfo> goodsList = new ArrayList<>();
        for (int i = 0; i < mNameArray.length; i++)
        {
            GoodsInfo info = new GoodsInfo();
            info.id = i;
            info.name = mNameArray[i];
            info.description = mDescArray[i];
            info.price = mPriceArray[i];
            info.pic = mPicArray[i];
            goodsList.add(info);
        }
        return goodsList;
    }

}
