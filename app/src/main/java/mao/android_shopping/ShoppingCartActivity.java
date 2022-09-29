package mao.android_shopping;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mao.android_shopping.application.MainApplication;
import mao.android_shopping.dao.CartDao;
import mao.android_shopping.dao.GoodsDao;
import mao.android_shopping.entity.CartInfo;
import mao.android_shopping.entity.GoodsInfo;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping
 * Class(类名): ShoppingCartActivity
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 20:58
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class ShoppingCartActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView tv_count;
    private LinearLayout ll_cart;

    // 声明一个购物车中的商品信息列表
    private List<CartInfo> mCartList;
    // 声明一个根据商品编号查找商品信息的映射，把商品信息缓存起来，这样不用每一次都去查询数据库
    private final Map<Integer, GoodsInfo> mGoodsMap = new HashMap<>();
    private TextView tv_total_price;
    private LinearLayout ll_empty;
    private LinearLayout ll_content;
    private CartDao cartDao;
    private GoodsDao goodsDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("购物车");
        ll_cart = findViewById(R.id.ll_cart);
        tv_total_price = findViewById(R.id.tv_total_price);

        tv_count = findViewById(R.id.tv_count);
        tv_count.setText(String.valueOf(MainApplication.getInstance().count));

        cartDao = CartDao.getInstance(this);
        cartDao.openReadConnection();
        cartDao.openWriteConnection();

        goodsDao = GoodsDao.getInstance(this);
        goodsDao.openReadConnection();
        goodsDao.openWriteConnection();

        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.btn_shopping_channel).setOnClickListener(this);
        findViewById(R.id.btn_clear).setOnClickListener(this);
        findViewById(R.id.btn_settle).setOnClickListener(this);
        ll_empty = findViewById(R.id.ll_empty);
        ll_content = findViewById(R.id.ll_content);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        cartDao.closeConnection();
        goodsDao.closeConnection();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showCart();
    }

    /**
     * 展示购物车中的商品列表
     */
    private void showCart()
    {
        // 移除下面的所有子视图
        ll_cart.removeAllViews();
        // 查询购物车数据库中所有的商品记录
        mCartList = cartDao.queryAll();
        if (mCartList.size() == 0)
        {
            return;
        }

        for (CartInfo cartInfo : mCartList)
        {
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo goods = goodsDao.queryById(cartInfo.getGoodsId());
            mGoodsMap.put(cartInfo.getGoodsId(), goods);

            // 获取布局文件item_cart.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.item_cart, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_desc = view.findViewById(R.id.tv_desc);
            TextView tv_count = view.findViewById(R.id.tv_count);
            TextView tv_price = view.findViewById(R.id.tv_price);
            TextView tv_sum = view.findViewById(R.id.tv_sum);

            iv_thumb.setImageURI(Uri.parse(goods.getPicPath()));
            tv_name.setText(goods.getName());
            tv_desc.setText(goods.getDescription());
            tv_count.setText(String.valueOf(cartInfo.getCount()));
            tv_price.setText(String.valueOf((int) goods.getPrice()));
            // 设置商品总价
            tv_sum.setText(String.valueOf((int) (cartInfo.getCount() * goods.getPrice())));

            // 给商品行添加长按事件。长按商品行就删除该商品
            view.setOnLongClickListener(v ->
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingCartActivity.this);
                builder.setMessage("是否从购物车删除" + goods.getName() + "？");
                builder.setPositiveButton("是", (dialog, which) ->
                {
                    // 移除当前视图
                    ll_cart.removeView(v);
                    // 删除该商品
                    deleteGoods(cartInfo);
                });
                builder.setNegativeButton("否", null);
                builder.create().show();
                return true;
            });

            // 给商品行添加点击事件。点击商品行跳到商品的详情页
            view.setOnClickListener(v ->
            {
                Intent intent = new Intent(ShoppingCartActivity.this, ShoppingDetailActivity.class);
                intent.putExtra("goods_id", goods.getId());
                startActivity(intent);
            });

            // 往购物车列表添加该商品行
            ll_cart.addView(view);
        }

        // 重新计算购物车中的商品总金额
        refreshTotalPrice();
    }

    /**
     * 删除商品
     *
     * @param info 信息
     */
    private void deleteGoods(CartInfo info)
    {
        MainApplication.getInstance().count -= info.getCount();
        // 从购物车的数据库中删除商品
        cartDao.deleteByGoodsId(info.getGoodsId());
        // 从购物车的列表中删除商品
        CartInfo removed = null;
        for (CartInfo cartInfo : mCartList)
        {
            if (cartInfo.getGoodsId() == info.getGoodsId())
            {
                removed = cartInfo;
                break;
            }
        }
        mCartList.remove(removed);
        // 显示最新的商品数量
        showCount();
        toastShow("已从购物车删除" + mGoodsMap.get(info.getGoodsId()).getName());
        mGoodsMap.remove(info.getGoodsId());
        // 刷新购物车中所有商品的总金额
        refreshTotalPrice();
    }

    // 显示购物车图标中的商品数量
    private void showCount()
    {
        tv_count.setText(String.valueOf(MainApplication.getInstance().count));
        // 购物车中没有商品，显示“空空如也”
        if (MainApplication.getInstance().count == 0)
        {
            ll_empty.setVisibility(View.VISIBLE);
            ll_content.setVisibility(View.GONE);
            ll_cart.removeAllViews();
        }
        else
        {
            ll_content.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        }
    }

    // 重新计算购物车中的商品总金额
    private void refreshTotalPrice()
    {
        int totalPrice = 0;
        for (CartInfo cartInfo : mCartList)
        {
            GoodsInfo goods = mGoodsMap.get(cartInfo.getGoodsId());
            totalPrice += goods.getPrice() * cartInfo.getCount();
        }
        tv_total_price.setText(String.valueOf(totalPrice));
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_back:
                // 点击了返回图标
                // 关闭当前页面
                finish();
                break;

            case R.id.btn_shopping_channel:
                // 从购物车页面跳到商场页面
                Intent intent = new Intent(this, ShoppingChannelActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.btn_clear:
                // 清空购物车数据库
                cartDao.deleteAll();
                MainApplication.getInstance().count = 0;
                // 显示最新的商品数量
                showCount();
                toastShow( "购物车已清空");
                break;

            case R.id.btn_settle:
                // 点击了“结算”按钮
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("结算商品");
                builder.setMessage("请求发送失败");
                builder.setPositiveButton("我知道了", null);
                builder.create().show();
                break;
        }
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
