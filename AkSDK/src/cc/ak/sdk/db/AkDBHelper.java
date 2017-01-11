/**
 * Copyright (c) www.bugull.com
 */

package cc.ak.sdk.db;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import cc.ak.sdk.util.AKLogUtil;

public class AkDBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 100;
    private static final String DB_NAME = "ak.db";
    public static final String TAG = AkDBHelper.class.getSimpleName();

    private volatile static SQLiteDatabase mDb = null;

    public static SQLiteDatabase getInstance(Activity activity) {
        if (mDb == null) {
            synchronized (SQLiteOpenHelper.class) {
                if (mDb == null) {
                    mDb = new AkDBHelper(activity).getWritableDatabase();
                }
            }
        }
        return mDb;
    }

    public AkDBHelper(Context context) {
        this(context, DB_NAME, null, DB_VERSION);
    }

    public AkDBHelper(Context context, String name,
            CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table statistics(_id integer primary key autoincrement, "
                + "time long, num integer, url text, type integer)";
        db.execSQL(sql);
        AKLogUtil.d(TAG, "数据统计表建表成功");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
