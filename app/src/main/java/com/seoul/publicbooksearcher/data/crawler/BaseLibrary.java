package com.seoul.publicbooksearcher.data.crawler;

import android.util.Log;

import com.seoul.publicbooksearcher.data.BaseBookRepository;
import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.domain.Book;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseLibrary extends BaseBookRepository {

    private static final String TAG = BaseLibrary.class.getName();
    protected BookRepository cache;
    private String libraryId;

    public BaseLibrary(BookRepository cache, String libraryId){
        this.cache = cache;
        this.libraryId = libraryId;
    }

    public String getLibraryId(){
        return libraryId;
    }

    @Override
    public List<Book> selectByKeyword(String keyword) {
        List<Book> books = cache.selectByKeywordAndLibrary(keyword, libraryId);
        try {
            if(books == null) {
                books = getBooks(keyword);
                cache.insertOrUpdateBooks(keyword, getLibraryId(), books);
            }
            else {
                for (Book book : books)
                    book.setStatusCode(getBookState(book.getBookId()));
            }
        } catch (IOException e) {
            throw new RuntimeException(TAG+" : IOException");
        }

        return books;
    }

    private List<Book> getBooks(String keyword) throws IOException {
        List<Book> books = new ArrayList();
        Document doc = Jsoup.connect("http://gdllc.sen.go.kr/wsearch-total.do")
                .data("site_id", "GO")
                .data("startCount", "0")
                .data("sort", "RANK")
                .data("collection", "sen_library")
                .data("range", "A")
                .data("searchField", "ALL")
                .data("locExquery", getLibraryId())
                .data("exquery_field", "MAT_CODE_NUM")
                .data("realQuery", keyword + "|" + keyword)
                .data("detailView", "0")
                .data("query", keyword)
                .post();

        Elements elements = doc.select("ul.resultsty1");
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String library = element.child(0).text();
            Log.i(TAG, library);

            Element dt = element.child(1).child(0).child(0);
            String title = dt.child(0).child(0).text().replace("\n", "").replace("\r", "").trim();
            Log.i(TAG, title);

            String writerAndPublicationAndYear = dt.parent().select("dd.summary").get(0).text();

            WriterAndPublicationAndYearParse writerAndPublicationAndYearParse = new WriterAndPublicationAndYearParse(writerAndPublicationAndYear);
            String writer = writerAndPublicationAndYearParse.getWriter();
            String publication = writerAndPublicationAndYearParse.getPublication();

            String callNumberAndLocation = dt.parent().select("dd.root").get(0).text();
            Log.i(TAG, callNumberAndLocation);
            CallNumberAndLocationParse callNumberAndLocationParse = new CallNumberAndLocationParse(callNumberAndLocation);

            String location = callNumberAndLocationParse.getLocation();
            Log.i(TAG, location);
            String callNumber = callNumberAndLocationParse.getCallNumber();
            Log.i(TAG, callNumber);

            String bookId = dt.child(1).child(1).attr("href").replaceAll(" ", "").replaceAll("'", "").split(",")[1];
            Log.i(TAG, bookId);
            int statusCode = getBookState(bookId);
            Log.i(TAG, ""+statusCode);
            books.add(new Book(title, library, publication, writer, statusCode, bookId, callNumber, location));
        }

        return books;
    }

    private String removeBack(String str){
        int start = str.lastIndexOf(":");
        return str.substring(start+1).trim();
    }

    private int getBookState(String bookId) throws IOException {
        int stateCode = Integer.parseInt(Jsoup.connect("http://gdllc.sen.go.kr/wsearch-haveBook.do?book_key=" + bookId).get().text());
        if(stateCode == 4) // 예약불가 (5인 이상) => 대출중
            stateCode = Book.BOOK_STATE_LOAN_ING;
        Log.i(TAG, "getBookState result : "+stateCode);
        return stateCode;
    }


    private class WriterAndPublicationAndYearParse {
        private String writer = "저자 정보없음";
        private String publication = "발행사 정보없음";
        private String year = "";

        public WriterAndPublicationAndYearParse(String str){
            String[] splits = str.split("[|]");
            for (String split : splits) {
                split = split.trim();
                if(split.startsWith("저자")){
                    writer = removeBack(split);
                }else if (split.startsWith("발행사")){
                    publication = removeBack(split);
                }else if (split.startsWith("발행년도")){
                    year = removeBack(split);
                }
            }
        }

        public String getWriter() {
            return writer;
        }
        public String getPublication() {
            return publication;
        }
        public String getYear() {
            return year;
        }
    }

    private class CallNumberAndLocationParse{
        private String callNumber = "청구번호 정보없음";
        private String location = "위치 정보없음";

        public CallNumberAndLocationParse(String str){
            String[] splits = str.split("[|]");
            for (String split : splits) {
                split = split.trim();
                if(split.startsWith("청구번호")){
                    callNumber = removeBack(split);
                }else if (split.startsWith("위치")){
                    location = removeBack(split);
                }
            }
        }

        public String getCallNumber() {
            return callNumber;
        }

        public String getLocation() {
            return location;
        }
    }

}
