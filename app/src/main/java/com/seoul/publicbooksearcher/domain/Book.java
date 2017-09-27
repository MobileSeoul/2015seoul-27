package com.seoul.publicbooksearcher.domain;

public class Book {

    private String bookId;
    private String title;
    private String library;
    private int statusCode;
    private String callNumber;
    private String location;
    private String publication;
    private String writer;

    public final static int BOOK_STATE_LOAN_POSSIBLE = 1;
    public final static int BOOK_STATE_LOAN_IMPOSSIBLE = 2;
    public final static int BOOK_STATE_LOAN_ING = 3;

    // 서울시용
    private String locationCode;

    public Book(String title) {
        this.title = title;
    }

    public Book(String title, String library, int statusCode, String bookId) {
        this.bookId = bookId;
        this.title = title;
        this.library = library;
        this.statusCode = statusCode;
    }

    public Book(String title, String library, String publication, String writer, int statusCode, String bookId, String callNumber, String location) {
        this.bookId = bookId;
        this.title = title;
        this.library = library;
        this.publication = publication;
        this.writer = writer;
        this.statusCode = statusCode;
        this.callNumber = callNumber;
        this.location = location;
    }

    public Book(String title, String library, String publication, String writer, int statusCode, String bookId, String callNumber, String location, String locationCode) {
        this.bookId = bookId;
        this.title = title;
        this.library = library;
        this.publication = publication;
        this.writer = writer;
        this.statusCode = statusCode;
        this.callNumber = callNumber;
        this.location = location;
        this.locationCode = locationCode;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getLibrary() {
        return library;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode){
        this.statusCode = statusCode;
    }
    public String getBookId() {return bookId;}
    public void setBookId(String bookId) {this.bookId = bookId;}
    public String getLocationCode() {return locationCode;}
    public void setLocationCode(String locationCode) {this.locationCode = locationCode;}
    public String getCallNumber() {return callNumber;}
    public void setCallNumber(String callNumber) {this.callNumber = callNumber;}
    public String getLocation() {
        return location;
    }

    public String getPublication() {
        return publication;
    }

    public String getWriter() {
        return writer;
    }
}
