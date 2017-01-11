
package cc.ak.sdk.db;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import cc.ak.sdk.bean.AkStatistics;

public class AKStatisticsDao {

    private final String TABLE = "statistics";
    private final String COLUMN_TIME = "time";
    private final String COLUMN_NUM = "num";
    private final String COLUMN_URL = "url";
    private final String COLUMN_TYPE = "type";
    private SQLiteDatabase mDb;

    private static AKStatisticsDao instance;

    private AKStatisticsDao(Activity activity) {
        mDb = AkDBHelper.getInstance(activity);
    }

    public static AKStatisticsDao getInstance(Activity activity) {
        if (instance == null) {
            instance = new AKStatisticsDao(activity);
        }
        return instance;
    }

    public void save(AkStatistics statistics) {

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIME, statistics.getTime());
        cv.put(COLUMN_NUM, statistics.getNum());
        cv.put(COLUMN_URL, statistics.getUrl());
        cv.put(COLUMN_TYPE, statistics.getType());
        mDb.insert(TABLE, null, cv);
    }

    public void deleteByUrl(String url) {
        mDb.delete(TABLE, COLUMN_URL + "=?", new String[] {
                url
        });
    }

    public void updateNumByUrl(int num, String url) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NUM, num);
        mDb.update(TABLE, cv, COLUMN_URL + "=?", new String[] {
                url
        });
    }

    public int getNumByUrl(String url) {
        String sql = "select status from " + TABLE + " where " + COLUMN_URL + "=?";
        Cursor cursor = mDb.rawQuery(sql, new String[] {
                url
        });
        int num = 0;
        if (cursor.moveToFirst()) {
            num = cursor.getInt(0);
        }
        cursor.close();
        return num;
    }

    public void closeDB() {
        if (mDb != null) {
            mDb.close();
        }
    }

}
