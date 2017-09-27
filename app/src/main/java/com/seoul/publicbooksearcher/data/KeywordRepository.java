package com.seoul.publicbooksearcher.data;

import java.util.List;

public interface KeywordRepository {

    void insertKeyword(String keyword);
    List<String> selectAll();

}
