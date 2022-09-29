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

import mao.android_shopping.entity.GoodsInfo;

/**
 * Project name(项目名称)：android_shopping
 * Package(包名): mao.android_shopping.dao
 * Class(类名): GoodsDao
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/9/29
 * Time(创建时间)： 20:10
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class GoodsDao extends SQLiteOpenHelper
{
    /**
     * 数据库名字
     */
    private static final String DB_NAME = "goods.db";

    /**
     * 表名
     */
    private static final String TABLE_NAME = "goods";

    /**
     * 数据库版本
     */
    private static final int DB_VERSION = 1;

    /**
     * 实例，单例模式，懒汉式，双重检查锁方式
     */
    private static volatile GoodsDao goodsDao = null;

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
    private static final String TAG = "GoodsDao";


    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public GoodsDao(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * 获得实例
     *
     * @param context 上下文
     * @return {@link GoodsDao}
     */
    public static GoodsDao getInstance(Context context)
    {
        if (goodsDao == null)
        {
            synchronized (GoodsDao.class)
            {
                if (goodsDao == null)
                {
                    goodsDao = new GoodsDao(context);
                }
            }
        }
        return goodsDao;
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
            readDatabase = goodsDao.getReadableDatabase();
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
            writeDatabase = goodsDao.getWritableDatabase();
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
                " name VARCHAR NOT NULL," +
                " description VARCHAR NOT NULL," +
                " price FLOAT NOT NULL," +
                " picPath VARCHAR," +
                " pic INTEGER NOT NULL)";
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
     * @return {@link List}<{@link GoodsInfo}>
     */
    public List<GoodsInfo> queryAll()
    {
        List<GoodsInfo> list = new ArrayList<>();

        Cursor cursor = readDatabase.query(TABLE_NAME, null, "1=1", new String[]{}, null, null, null);

        while (cursor.moveToNext())
        {
            GoodsInfo goodsInfo = new GoodsInfo();
            setGoodsInfo(cursor, goodsInfo);
            list.add(goodsInfo);
        }

        cursor.close();
        return list;
    }


    /**
     * 通过id(主键)查询
     *
     * @param id id(主键)
     * @return {@link GoodsInfo}
     */
    public GoodsInfo queryById(Serializable id)
    {
        GoodsInfo goodsInfo = null;
        Cursor cursor = readDatabase.query(TABLE_NAME, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            goodsInfo = new GoodsInfo();
            setGoodsInfo(cursor, goodsInfo);
        }
        cursor.close();
        return goodsInfo;
    }


    /**
     * 插入一条数据
     *
     * @param goodsInfo GoodsInfo对象
     * @return boolean
     */
    public boolean insert(GoodsInfo goodsInfo)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(goodsInfo, contentValues);
        long insert = writeDatabase.insert(TABLE_NAME, null, contentValues);
        return insert > 0;
    }

    /**
     * 插入多条数据
     *
     * @param list 列表
     * @return boolean
     */
    public boolean insert(List<GoodsInfo> list)
    {
        try
        {
            writeDatabase.beginTransaction();
            for (GoodsInfo goodsInfo : list)
            {
                Log.d(TAG, "insert: \n" + goodsInfo);
                boolean insert = this.insert(goodsInfo);
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
     * @param goodsInfo GoodsInfo对象
     * @return boolean
     */
    public boolean update(GoodsInfo goodsInfo)
    {
        ContentValues contentValues = new ContentValues();
        setContentValues(goodsInfo, contentValues);
        int update = writeDatabase.update(TABLE_NAME, contentValues, "id=?", new String[]{String.valueOf(goodsInfo.getId())});
        return update > 0;
    }

    /**
     * 插入或更新，先尝试插入，如果插入失败，更新
     *
     * @param goodsInfo GoodsInfo对象
     * @return boolean
     */
    public boolean insertOrUpdate(GoodsInfo goodsInfo)
    {
        boolean insert = insert(goodsInfo);
        if (insert)
        {
            return true;
        }
        return update(goodsInfo);
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
     * 得到总数
     *
     * @return int
     */
    public long getCount()
    {
        Cursor cursor = writeDatabase.query(TABLE_NAME, new String[]{"count(*)"}, "1=1", null, null, null, null);
        cursor.moveToNext();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }


    /**
     * 填充ContentValues
     *
     * @param goodsInfo     GoodsInfo
     * @param contentValues ContentValues
     */
    private void setContentValues(GoodsInfo goodsInfo, ContentValues contentValues)
    {
        contentValues.put("id", goodsInfo.getId());
        contentValues.put("name", goodsInfo.getName());
        contentValues.put("description", goodsInfo.getDescription());
        contentValues.put("price", goodsInfo.getPrice());
        contentValues.put("picPath", goodsInfo.getPicPath());
        contentValues.put("pic", goodsInfo.getPic());
    }

    /**
     * 填充GoodsInfo
     *
     * @param cursor    游标
     * @param goodsInfo GoodsInfo对象
     */
    private GoodsInfo setGoodsInfo(Cursor cursor, GoodsInfo goodsInfo)
    {

        goodsInfo.setId(cursor.getInt(0))
                .setName(cursor.getString(1))
                .setDescription(cursor.getString(2))
                .setPrice(cursor.getFloat(3))
                .setPicPath(cursor.getString(4))
                .setPic(cursor.getInt(5));

        return goodsInfo;
    }


}
