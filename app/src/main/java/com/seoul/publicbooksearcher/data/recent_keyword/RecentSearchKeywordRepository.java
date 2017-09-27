package com.seoul.publicbooksearcher.data.recent_keyword;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seoul.publicbooksearcher.data.KeywordRepository;
import com.seoul.publicbooksearcher.domain.Book;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecentSearchKeywordRepository implements KeywordRepository{

    private final static String DB_NAME = "book_cache_db";
    private final static String TABLE_NAME = "keyword_table";

    private static SQLiteDatabase db = null;
    private static Context context;

    public RecentSearchKeywordRepository(Context context){
        this.context = context;
    }

    @Override
    public void insertKeyword(String keyword) {
        ContentValues args = new ContentValues();
        args.put("KEYWORD", keyword);
        db.insert(getTableName(), null, args);
    }

    public static SQLiteDatabase getSQLiteDatabaseInstance(){
        if(db == null)
            db = context.openOrCreateDatabase(DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        return db;
    }

    @Override
    public List<String> selectAll() {
        db = getSQLiteDatabaseInstance();

        //db.execSQL("DROP TABLE if EXISTS " + getTableName());
        if(!isTableExists(db, getTableName())) {
            db.execSQL("CREATE TABLE " + getTableName() + " (_ID INTEGER PRIMARY KEY AUTOINCREMENT, KEYWORD text not null unique)");
        }

        Cursor c = db.query(getTableName(),
                new String[] {"_ID","KEYWORD"}, //colum 명세
                null,
                null, //where 절에 전달할 데이터
                null, //group by
                null, //having
                "_ID DESC"
        );

        List<String> keywords = new ArrayList();
        while(c.moveToNext())
            keywords.add(c.getString(c.getColumnIndex("KEYWORD")));

        return keywords;
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    boolean isTableExists(SQLiteDatabase db, String tableName)
    {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
        if (!cursor.moveToFirst())
        {
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }
}
