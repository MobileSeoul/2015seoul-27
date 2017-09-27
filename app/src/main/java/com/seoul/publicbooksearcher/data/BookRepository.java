package com.seoul.publicbooksearcher.data;

import com.seoul.publicbooksearcher.domain.Book;

import java.util.List;

public interface BookRepository {

    List<Book> selectByKeyword(String keyword);
    List<Book> selectByKeywordAndLibrary(String keyword, String library);
    void insertOrUpdateBooks(String keyword,  String library, List<Book> books);

}
