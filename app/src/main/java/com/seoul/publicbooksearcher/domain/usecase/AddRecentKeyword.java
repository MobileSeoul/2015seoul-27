package com.seoul.publicbooksearcher.domain.usecase;


import com.seoul.publicbooksearcher.data.KeywordRepository;

public class AddRecentKeyword implements UseCase<Void, String> {

    private final KeywordRepository keywordRepository;

    public AddRecentKeyword(KeywordRepository keywordRepository){
        this.keywordRepository = keywordRepository;
    }

    @Override
    public Void execute(String keyword) {
        this.keywordRepository.insertKeyword(keyword);
        return null;
    }
}
