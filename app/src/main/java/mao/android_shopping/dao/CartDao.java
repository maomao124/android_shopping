package mao.android_shopping.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import mao.android_shopping.entity.CartInfo;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping.dao
 * Class(类名): CartDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 20:19
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class CartDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "cart.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "cart";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile CartDao cartDao = null;

    /**
     * 读数据库
     */
    private SQLiteDatabase readDatabase;
    /**
     * 写数据库
     */
    private SQLiteDatabase writeDatabase;

    /**
     * 标签
     */
    private static final String TAG = "CartDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public CartDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link CartDao}
     */
    public static CartDao getInstance(Context context)
    {
        if (cartDao == null)
        {
            synchronized (CartDao.class)
            {
                if (cartDao == null)
                {
                    cartDao = new CartDao(context);
                }
            }
        }
        return cartDao;
    }

    /**
     * 打开读连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openReadConnection()
    {
        if (readDatabase == null || !readDatabase.isOpen())
        {
            readDatabase = cartDao.getReadableDatabase();
        }
        return readDatabase;
    }

    /**
     * 打开写连接
     *
     * @return {@link SQLiteDatabase}
     */
    public SQLiteDatabase openWriteConnection()
    {
        if (writeDatabase == null || !writeDatabase.isOpen())
        {
            writeDatabase = cartDao.getWritableDatabase();
        }
        return readDatabase;
    }

    /**
     * 关闭数据库读连接和写连接
     */
    public void closeConnection()
    {
        if (readDatabase != null && readDatabase.isOpen())
        {
            readDatabase.close();
            readDatabase = null;
        }

        if (writeDatabase != null && writeDatabase.isOpen())
        {
            writeDatabase.close();
            writeDatabase = null;
        }
    }


    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                " goodsId INTEGER NOT NULL," +
                " count INTEGER NOT NULL);";
        db.execSQL(sql);
    }

    /**
     * 数据库版本更新时触发回调
     *
     * @param db         SQLiteDatabase
     * @param oldVersion 旧版本
     * @param newVersion 新版本
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }


    /**
     * 查询所有
     *
     * @return {@link List}<{@link CartInfo}>
     */
    public List<CartInfo> queryAll()
    {
        List<CartInfo> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            CartInfo cartInfo = new CartInfo();
            setCartInfo(cursor, cartInfo);
            list.add(cartInfo);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id(主键)查询
     *
     * @param id id(主键)
     * @return {@link CartInfo}
     */
    public CartInfo queryById(Serializable id)
    {
        CartInfo cartInfo = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            cartInfo = new CartInfo();
            setCartInfo(cursor, cartInfo);
        }
        cursor.close();
        return cartInfo;
    }


    /**
     * 插入一条数据
     *
     * @param cartInfo CartInfo对象
     * @return boolean
     */
    public boolean insert(CartInfo cartInfo)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartInfo, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<CartInfo> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (CartInfo cartInfo : list)
            {
                boolean insert = this.insert(cartInfo);
                if (!insert)
                {
                    throw new Exception();
                }
            }
            writeDatabase.setTransactionSuccessful();
            return true;
        }
        catch (Exception e)
        {
            writeDatabase.endTransaction();
            Log.e(TAG, "insert: ", e);
            return false;
        }
    }

    /**
     * 更新
     *
     * @param cartInfo CartInfo对象
     * @return boolean
     */
    public boolean update(CartInfo cartInfo)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(cartInfo, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{String.valueOf(cartInfo.getId())});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param cartInfo CartInfo对象
     * @return boolean
     */
    public boolean insertOrUpdate(CartInfo cartInfo)
    {
        boolean insert = insert(cartInfo);
        if (insert)
        {
            return true;
        }
        return update(cartInfo);
    }

    /**
     * 删除
     *
     * @param id id
     * @return boolean
     */
    public boolean delete(Serializable id)
    {
        int delete = writeDatabase.delete(TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        return delete > 0;
    }


    /**
     * 填充ContentValues
     *
     * @param cartInfo      CartInfo
     * @param contentValues ContentValues
     */
    private void setContentValues(CartInfo cartInfo, ContentValues contentValues)
    {
        contentValues.put("id", cartInfo.getId());
        contentValues.put("goodsId", cartInfo.getGoodsId());
        contentValues.put("count", cartInfo.getCount());
    }

    /**
     * 填充CartInfo
     *
     * @param cursor   游标
     * @param cartInfo CartInfo对象
     */
    private CartInfo setCartInfo(Cursor cursor, CartInfo cartInfo)
    {
        cartInfo.setId(cursor.getInt(0))
                .setGoodsId(cursor.getInt(1))
                .setCount(cursor.getInt(2));

        return cartInfo;
    }
}
