package mao.android_shopping.entity;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping.entity
 * Class(类名): CartInfo
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 19:46
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class CartInfo
{
    /**
     * id
     */
    private int id;

    /**
     * 商品编号
     */
    private int goodsId;
    /**
     * 商品数量
     */
    private int count;

    /**
     * Instantiates a new Cart info.
     */
    public CartInfo()
    {
    }

    /**
     * Instantiates a new Cart info.
     *
     * @param id      the id
     * @param goodsId the goods id
     * @param count   the count
     */
    public CartInfo(int id, int goodsId, int count)
    {
        this.id = id;
        this.goodsId = goodsId;
        this.count = count;
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
    public CartInfo setId(int id)
    {
        this.id = id;
        return this;
    }

    /**
     * Gets goods id.
     *
     * @return the goods id
     */
    public int getGoodsId()
    {
        return goodsId;
    }

    /**
     * Sets goods id.
     *
     * @param goodsId the goods id
     * @return the goods id
     */
    public CartInfo setGoodsId(int goodsId)
    {
        this.goodsId = goodsId;
        return this;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Sets count.
     *
     * @param count the count
     * @return the count
     */
    public CartInfo setCount(int count)
    {
        this.count = count;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("id：").append(id).append('\n');
        stringbuilder.append("goodsId：").append(goodsId).append('\n');
        stringbuilder.append("count：").append(count).append('\n');
        return stringbuilder.toString();
    }
}
