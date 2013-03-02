package com.m6.gocook.base.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import com.m6.gocook.base.db.table.SearchList;
import com.m6.gocook.util.log.Logger;

public class GoCookProvider extends ContentProvider {
	
	private static final String TAG = "GoCookProvider";
	
	public static String AUTHORITY = "com.m6.gocook";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY + "/";
    
    public static final String PARAMETER_NOTIFY = "notify";
    public static final String PARAMETER_RAW_QUERY = "raw_query";
	
	private GoCookOpenHelper mOpenHelper;
	
	public static Uri getTableUri(String table) {
		return Uri.parse(CONTENT_URI_BASE + table);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = new GoCookOpenHelper(getContext());
		return true;
	}
	
	public void close() {
        mOpenHelper.close();
    }
	
	@Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

	@Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        SqlArguments args = new SqlArguments(uri);

        try {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final long rowId = db.insert(args.table, null, initialValues);
            if (rowId <= 0)
                return null;

            uri = ContentUris.withAppendedId(uri, rowId);
            sendNotify(uri);
        } catch (Exception e) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "insert : " + e.toString());
            }
        }
        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = null;
        try {
            db = mOpenHelper.getWritableDatabase();
            db.beginTransaction();

            int numValues = values.length;
            for (int i = 0; i < numValues; i++) {
                if (db.insert(args.table, null, values[i]) < 0)
                    return 0;
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "bulkInsert : " + e.toString());
            }
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
        sendNotify(uri);
        return values.length;
    }
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		RawSql rawSql = RawSql.createRawSql(uri);
        if (rawSql != null) {
            return queryRAW(uri, rawSql.rawsql, selectionArgs);
        }
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = null;
        Cursor result = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
            if (result != null) {
                result.setNotificationUri(getContext().getContentResolver(), uri);
            }
        } catch (Exception e) {
            if (result != null) {
                result.close();
                result = null;
            }
        }

        return result;
	}
	
	private Cursor queryRAW(Uri uri, String rawSql, String[] selectionArgs) {
        SQLiteDatabase db = null;
        Cursor result = null;
        try {
            db = mOpenHelper.getReadableDatabase();
            result = db.rawQuery(rawSql, selectionArgs);
            if (result != null) {
                result.setNotificationUri(getContext().getContentResolver(), uri);
            }
        } catch (Exception e) {
            if (result != null) {
                result.close();
                result = null;
            }
        }
        return result;
    }

	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        int count = 0;
        try {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            count = db.delete(args.table, args.where, args.args);
            // if (count > 0) sendNotify(uri);
            sendNotify(uri); // TODO 为什么对整个表操作返回count=0
        } catch (Exception e) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "delete : " + e.toString());
            }
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        int count = 0;
        try {
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            count = db.update(args.table, values, args.where, args.args);
            // if (count > 0) sendNotify(uri);
            sendNotify(uri); // TODO 为什么对整个表操作返回count=0
        } catch (Exception e) {
            if (Logger.DEBUG) {
                Logger.i(TAG, "update : " + e.toString());
            }
        }

        return count;
    }

    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }

	private static class RawSql {
        final String rawsql;
        RawSql(String rawsql) {
            this.rawsql = rawsql;
        }
        
        static RawSql createRawSql(Uri url) {
            String rawsql = url.getQueryParameter(PARAMETER_RAW_QUERY);
            if (TextUtils.isEmpty(rawsql)) {
                return null;
            } else {
                return new RawSql(rawsql);
            }
        }
    }
    
    private static class SqlArguments {
        public final String table;

        public final String where;

        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) {
            if (url.getPathSegments().size() == 1) {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (url.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + url);
            } else if (!TextUtils.isEmpty(where)) {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            } else {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);
                this.args = null;
            }
        }

        SqlArguments(Uri url) {
            if (url.getPathSegments().size() == 1) {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } else {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
	
	private static class GoCookOpenHelper extends SQLiteOpenHelper {
		
		private static final String DATABASE_NAME = "gocook";
		private static final int DATABASE_VERSION = 1;

		GoCookOpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SearchList.CREATE_TABLE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        db.execSQL("DROP TABLE IF EXISTS " + SearchList.TABLE);
	        onCreate(db);
		}
		
	}
}
