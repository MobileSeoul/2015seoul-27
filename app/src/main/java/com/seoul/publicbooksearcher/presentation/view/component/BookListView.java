package com.seoul.publicbooksearcher.presentation.view.component;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.seoul.publicbooksearcher.domain.Book;
import com.seoul.publicbooksearcher.domain.Location;
import com.seoul.publicbooksearcher.presentation.view.adapter.BookListViewAdapter;

import java.util.List;

public class BookListView {

    private RecyclerView bookListView = null;
    private Context context;
    private BookListViewAdapter bookListViewAdapter;

    public BookListView(Context context, RecyclerView listView){
        this.context = context;
        this.bookListView = listView;
        this.bookListViewAdapter = (BookListViewAdapter) listView.getAdapter();
    }

    public void updateLibrary(String library, List<Book> books){
        bookListViewAdapter.updateItem(library, books);
    }

    public void progressVisible(String library) {
        bookListViewAdapter.progressVisible(library);
    }

    public void progressGone(String library) {
        bookListViewAdapter.progressGone(library);
    }

    public void hideKeyboard() {
        InputMethodManager mgr = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(bookListView.getWindowToken(), 0);
    }

    public void showError(String library, String message) {
        bookListViewAdapter.showError(library, message);
    }

    public void sort(Location location) {
        bookListViewAdapter.sort(location);
    }

    public void collapseAllParents() {
        bookListViewAdapter.collapseAllParents();
    }

    public void clearLibrary(String library) {
        bookListViewAdapter.clearChildItems(library);
    }
}
