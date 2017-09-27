package com.seoul.publicbooksearcher.domain.exception;


public class BookSearchException extends RuntimeException{

    private String library;

    public BookSearchException(String detailMessage, String library) {
        super(detailMessage);
        this.library = library;
    }


    public String getLibrary() {
        return library;
    }
}
