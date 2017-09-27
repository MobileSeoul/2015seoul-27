package com.seoul.publicbooksearcher.data.cache.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.seoul.publicbooksearcher.data.BaseBookRepository;
import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.domain.Book;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookCache extends BaseBookRepository {

    private final static String DB_NAME = "book_cache_db";
    private final static String TABLE_NAME = "books";
    private static final String ID_FIELD = "_ID";
    private static final String BOOKS_JSON_FIELD = "BOOKS_JSON";

    private static SQLiteDatabase db = null;
    private static Context context;

    public BookCache(Context context){
        this.context = context;
        db = getSQLiteDatabaseInstance();

        //db.execSQL("DROP TABLE if EXISTS " + getTableName());
        if(!isTableExists(db, getTableName())) {
            db.execSQL("CREATE TABLE " + getTableName() + " ("+ ID_FIELD +" text primary key not null, "+ BOOKS_JSON_FIELD +" text not null)");
        }
    }

    public String getTableName(){
        return TABLE_NAME;
    }

    @Override
    public List<Book> selectByKeywordAndLibrary(String keyword, String library) {
        Cursor c = db.query(getTableName(),
                new String[] {ID_FIELD, BOOKS_JSON_FIELD}, //colum 명세
                ID_FIELD+" = ?",
                new String[] {makeId(keyword,library)}, //where 절에 전달할 데이터
                null, //group by
                null, //having
                null
        );

        List<Book> books = null;
        if(c.moveToFirst()) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Book>>() {}.getType();
            books = gson.fromJson(c.getString(c.getColumnIndex(BOOKS_JSON_FIELD)), type);
            if(books.size() == 0) return null;
        }

        return books;
    }

    public static SQLiteDatabase getSQLiteDatabaseInstance(){
        if(db == null)
            db = context.openOrCreateDatabase(DB_NAME, SQLiteDatabase.CREATE_IF_NECESSARY, null);
        return db;
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

    @Override
    public void insertOrUpdateBooks(String keyword, String library, List<Book> books) {
        ContentValues args = new ContentValues();
        args.put(ID_FIELD, makeId(keyword,library));
        Gson gson = new Gson();
        args.put(BOOKS_JSON_FIELD, gson.toJson(books));

        if(db.update(getTableName(), args, ID_FIELD+"=?", new String[]{makeId(keyword,library)}) == 0)
            db.insert(getTableName(), null, args);
    }

    private String makeId(String keyword, String library){
        return keyword + "_" + library;
    }
}
