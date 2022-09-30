package mao.android_shopping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import mao.android_shopping.application.MainApplication;
import mao.android_shopping.dao.CartDao;
import mao.android_shopping.dao.GoodsDao;
import mao.android_shopping.entity.GoodsInfo;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping
 * Class(类名): ShoppingDetailActivity
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 21:25
 * Version(版本): 1.0
 * Description(描述)： 商品详情页
 */

public class ShoppingDetailActivity extends AppCompatActivity implements View.OnClickListener
{
    private TextView tv_title;
    private TextView tv_count;
    private TextView tv_goods_price;
    private TextView tv_goods_desc;
    private ImageView iv_goods_pic;
    private int mGoodsId;
    private GoodsDao goodsDao;
    private CartDao cartDao;

    private static final String TAG = "ShoppingDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.activity_shopping_detail);
        tv_title = findViewById(R.id.tv_title);
        tv_count = findViewById(R.id.tv_count);
        tv_goods_price = findViewById(R.id.tv_goods_price);
        tv_goods_desc = findViewById(R.id.tv_goods_desc);
        iv_goods_pic = findViewById(R.id.iv_goods_pic);
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.iv_cart).setOnClickListener(this);
        findViewById(R.id.btn_add_cart).setOnClickListener(this);

        tv_count.setText(String.valueOf(MainApplication.getInstance().count));

        goodsDao = GoodsDao.getInstance(this);
        goodsDao.openReadConnection();
        goodsDao.openWriteConnection();

        cartDao = CartDao.getInstance(this);
        cartDao.openReadConnection();
        cartDao.openWriteConnection();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showDetail();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        //goodsDao.closeConnection();
        //cartDao.closeConnection();
    }

    private void showDetail()
    {
        // 获取上一个页面传来的商品编号
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
        if (mGoodsId >= 0)
        {
            // 根据商品编号查询商品数据库中的商品记录
            GoodsInfo goodsInfo = goodsDao.queryById(mGoodsId);
            tv_title.setText(goodsInfo.getName());
            tv_goods_desc.setText(goodsInfo.getDescription());
            tv_goods_price.setText(String.valueOf((int) goodsInfo.getPrice()));
            iv_goods_pic.setImageURI(Uri.parse(goodsInfo.getPicPath()));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.iv_back:
                finish();
                break;

            case R.id.iv_cart:
                Intent intent = new Intent(this, ShoppingCartActivity.class);
                startActivity(intent);
                break;

            case R.id.btn_add_cart:
                addToCart(mGoodsId);
                break;
        }
    }

    private void addToCart(int goodsId)
    {
        // 购物车商品数量+1
        int count = ++MainApplication.getInstance().count;
        tv_count.setText(String.valueOf(count));
        cartDao.insertCartInfo(goodsId);
        toastShow("成功添加至购物车");
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
