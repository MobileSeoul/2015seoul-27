package com.seoul.publicbooksearcher.domain.usecase;

import com.seoul.publicbooksearcher.data.KeywordRepository;

import java.util.List;

public class GetRecentKeywords implements UseCase<List<String>, Void> {

    private final KeywordRepository keywordRepository;

    public GetRecentKeywords(KeywordRepository keywordRepository){
        this.keywordRepository = keywordRepository;
    }

    @Override
    public List<String> execute(Void arg) {
        return this.keywordRepository.selectAll();
    }
}
