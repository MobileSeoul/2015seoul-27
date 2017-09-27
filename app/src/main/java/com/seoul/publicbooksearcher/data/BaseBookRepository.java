package com.seoul.publicbooksearcher.data;

import com.seoul.publicbooksearcher.domain.Book;

import java.util.List;

public abstract class BaseBookRepository implements BookRepository {

    @Override
    public List<Book> selectByKeyword(String keyword) {throw  new RuntimeException("Not implement");}

    @Override
    public List<Book> selectByKeywordAndLibrary(String keyword, String library) {throw  new RuntimeException("Not implement");}

    @Override
    public void insertOrUpdateBooks(String keyword, String library, List<Book> books) {throw  new RuntimeException("Not implement");}
}
