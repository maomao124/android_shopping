package mao.android_shopping;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;

import mao.android_shopping.application.MainApplication;
import mao.android_shopping.dao.CartDao;
import mao.android_shopping.dao.GoodsDao;
import mao.android_shopping.entity.CartInfo;
import mao.android_shopping.entity.GoodsInfo;
import mao.android_shopping.entity.Result;

public class ShoppingChannelActivity extends AppCompatActivity implements View.OnClickListener
{

    private TextView tv_count;
    private GridLayout gl_channel;
    private GoodsDao goodsDao;
    private CartDao cartDao;

    private static final String TAG = "ShoppingChannelActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
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

        long count = goodsDao.getCount();
        Log.d(TAG, "onCreate: count:" + count);

        if (count == 0)
        {
            List<GoodsInfo> list = GoodsInfo.getDefaultList();
            Log.d(TAG, "onCreate: \n" + list);

            //list.forEach(goodsDao::insert);

//            for (GoodsInfo goodsInfo : list)
//            {
//                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), goodsInfo.getPic());
//                String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + goodsInfo.getId() + ".jpg";
//                saveImage(path, bitmap);
//                goodsInfo.setPicPath(path);
//                boolean insert = goodsDao.insert(goodsInfo);
//                if (insert)
//                {
//                    //toastShow("已初始化数据");
//                }
//                else
//                {
//                    toastShow("初始化数据失败");
//                }
//            }

            toastShow("正在初始化数据");
            for (GoodsInfo goodsInfo : list)
            {
                //Bitmap bitmap = BitmapFactory.decodeResource(getResources(), goodsInfo.getPic());
                //String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + goodsInfo.getId() + ".jpg";
                //saveImage(path, bitmap);
                //goodsInfo.setPicPath(path);
                boolean insert = goodsDao.insert(goodsInfo);
                if (insert)
                {
                    //toastShow("已初始化数据");
                }
                else
                {
                    //toastShow("初始化数据失败");
                }
            }


//            boolean insert = goodsDao.insert(list);
//            if (insert)
//            {
//                toastShow("已初始化数据");
//            }
//            else
//            {
//                toastShow("初始化数据失败");
//            }
        }

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
            Log.d(TAG, "showGoods: \n" + goodsInfo);
            // 获取布局文件item_goods.xml的根视图
            View view = LayoutInflater.from(this).inflate(R.layout.iten_goods, null);
            ImageView iv_thumb = view.findViewById(R.id.iv_thumb);
            TextView tv_name = view.findViewById(R.id.tv_name);
            TextView tv_price = view.findViewById(R.id.tv_price);
            Button btn_add = view.findViewById(R.id.btn_add);

            //给控件设置值
            //iv_thumb.setImageURI(Uri.parse(goodsInfo.getPicPath()));
            iv_thumb.setImageBitmap(getImageBitmap(goodsInfo));
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
        Log.d(TAG, "onDestroy: ");
        goodsDao.closeConnection();
        cartDao.closeConnection();
    }

    @SuppressLint("NonConstantResourceId")
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

    /**
     * 把位图数据保存到指定路径的图片文件
     *
     * @param path   路径
     * @param bitmap Bitmap对象
     */
    public static boolean saveImage(String path, Bitmap bitmap)
    {
        // 根据指定的文件路径构建文件输出流对象
        try (FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            // 把位图数据压缩到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 从指定路径的图片文件中读取位图数据
     *
     * @param path 路径
     * @return {@link Bitmap}
     */
    public static Bitmap openImage(String path)
    {
        // 声明一个位图对象
        Bitmap bitmap = null;
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fileInputStream = new FileInputStream(path))
        {
            // 从文件输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 从指定路径的图片文件中读取位图数据
     *
     * @param file File对象
     * @return {@link Bitmap}
     */
    public static Bitmap openImage(File file)
    {
        // 声明一个位图对象
        Bitmap bitmap = null;
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fileInputStream = new FileInputStream(file))
        {
            // 从文件输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 得到图像位图
     *
     * @param goodsInfo GoodsInfo对象
     * @return {@link Bitmap}
     */
    public Bitmap getImageBitmap(GoodsInfo goodsInfo)
    {
        if (goodsInfo.getPicPath() != null && !"".equals(goodsInfo.getPicPath()))
        {
            Log.d(TAG, "getImageBitmap: 加载图片");
            Bitmap bitmap = openImage(goodsInfo.getPicPath());
            if (bitmap != null)
            {
                //GoodsInfo里有缓存的图片路径，并且加载到了图片的路径，直接返回
                return bitmap;
            }
            Log.d(TAG, "getImageBitmap: 本地图片缓存不存在，需要重新加载：" + goodsInfo.getId());
            //GoodsInfo里有缓存的图片路径，但是没有加载到图片的路径
            //图片不存在，路径失效，需要再次从网络加载
            Log.d(TAG, "getImageBitmap: 从网络上加载图片");
            Result result = getImageBitmapByHTTP(goodsInfo);
            bitmap = result.getBitmap();
            if (!result.isResult())
            {
                //从网络上加载失败失败，直接使用
                return bitmap;
            }
            //保存
            String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + goodsInfo.getId() + ".jpg";
            Log.d(TAG, "getImageBitmap: 保存图片，位置：" + path);
            boolean b = saveImageBitmap(goodsInfo, bitmap, path);
            if (!b)
            {
                //保存失败，直接使用
                return bitmap;
            }
            //保存成功
            //更新数据库
            goodsInfo.setPicPath(path);
            goodsDao.update(goodsInfo);
            Log.d(TAG, "getImageBitmap: 更新数据库");
            return bitmap;
        }
        Log.d(TAG, "getImageBitmap: 第一次加载图片");
        //不存在，第一次加载
        Result result = getImageBitmapByHTTP(goodsInfo);
        Bitmap bitmap = result.getBitmap();
        if (!result.isResult())
        {
            //从网络上加载失败失败，直接使用
            return bitmap;
        }
        //保存
        String path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + goodsInfo.getId() + ".jpg";
        boolean b = saveImageBitmap(goodsInfo, bitmap, path);
        if (!b)
        {
            //保存失败，直接使用
            return bitmap;
        }
        //保存成功
        //更新数据库
        goodsInfo.setPicPath(path);
        goodsDao.update(goodsInfo);
        Log.d(TAG, "getImageBitmap: 更新数据库");
        return bitmap;
    }

    /**
     * 保存图像位图
     *
     * @param goodsInfo 货物信息
     */
    public boolean saveImageBitmap(GoodsInfo goodsInfo, Bitmap bitmap, String path)
    {
        return saveImage(path, bitmap);
    }


    /**
     * 模拟从网络上获取图片
     *
     * @return {@link Bitmap}
     */
    public Result getImageBitmapByHTTP(GoodsInfo goodsInfo)
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), goodsInfo.getPic());
        if (bitmap == null)
        {
            //为空，加载默认的图片
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test);
            return new Result().setResult(false).setBitmap(bitmap);
        }
        return new Result().setResult(true).setBitmap(bitmap);
    }

}