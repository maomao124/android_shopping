package mao.android_shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import mao.android_shopping.application.MainApplication;
import mao.android_shopping.dao.CartDao;
import mao.android_shopping.dao.GoodsDao;
import mao.android_shopping.entity.CartInfo;
import mao.android_shopping.entity.GoodsInfo;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener
{

    private TextView tv_count;
    private GridLayout gl_channel;
    private GoodsDao goodsDao;
    private CartDao cartDao;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_channel);
        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("手机商场");

        tv_count = findViewById(R.id.tv_count);
        gl_channel = findViewById(R.id.gl_channel);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);

        goodsDao = GoodsDao.getInstance(this);
        goodsDao.openReadConnection();
        goodsDao.openWriteConnection();

        cartDao = CartDao.getInstance(this);
        cartDao.openReadConnection();
        cartDao.openWriteConnection();

        // 从数据库查询出商品信息，并展示
        showGoods();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 查询购物车商品总数，并展示
        showCartInfoTotal();
    }

    // 查询购物车商品总数，并展示
    private void showCartInfoTotal()
    {
        int count = cartDao.queryAll().size();
        MainApplication.getInstance().count = count;
        tv_count.setText(String.valueOf(count));
    }

    /**
     * 展示商品
     */
    private void showGoods()
    {
        // 商品条目是一个线性布局，设置布局的宽度为屏幕的一半
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(screenWidth / 2, LinearLayout.LayoutParams.WRAP_CONTENT);
        // 查询商品数据库中的所有商品记录
        List<GoodsInfo> list = goodsDao.queryAll();

        // 移除下面的所有子视图
        gl_channel.removeAllViews();

        for (GoodsInfo goodsInfo : list)
        {
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.iten_goods, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);
            Button btn_add = view.findViewById(R.id.btn_add);

            //给控件设置值
            iv_thumb.setImageURI(Uri.parse(goodsInfo.getPicPath()));
            tv_name.setText(goodsInfo.getName());
            tv_price.setText(String.valueOf((int) goodsInfo.getPrice()));

            // 添加到购物车
            btn_add.setOnClickListener(v ->
            {
                addToCart(goodsInfo.getId(), goodsInfo.getName());
            });

            // 点击商品图片，跳转到商品详情页面
            iv_thumb.setOnClickListener(v ->
            {
                Intent intent = new Intent(ShoppingChannelActivity.this, ShoppingDetailActivity.class);
                intent.putExtra("goods_id", goodsInfo.getId());
                startActivity(intent);
            });

            // 把商品视图添加到网格布局
            gl_channel.addView(view, params);
        }
    }

    // 把指定编号的商品添加到购物车
    private void addToCart(int goodsId, String goodsName)
    {
        // 购物车商品数量+1
        int count = ++MainApplication.getInstance().count;
        tv_count.setText(String.valueOf(count));

        cartDao.insertCartInfo(goodsId);

        toastShow("已添加一部" + goodsName + "到购物车");
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        goodsDao.closeConnection();
        cartDao.closeConnection();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_back:
                // 点击了返回图标，关闭当前页面
                finish();
                break;

            case R.id.iv_cart:
                // 点击了购物车图标
                // 从商场页面跳到购物车页面
                Intent intent = new Intent(this, ShoppingCartActivity.class);
                // 设置启动标志，避免多次返回同一页面的
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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