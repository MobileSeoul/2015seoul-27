package com.seoul.publicbooksearcher.presentation.view.component;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.seoul.publicbooksearcher.R;
import com.seoul.publicbooksearcher.presentation.presenter.BookPresenter;
import com.seoul.publicbooksearcher.presentation.view.adapter.BookTitleAutoCompleteTextViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookTitleAutoCompleteTextView {

    private BookPresenter bookPresenter;
    private final AutoCompleteTextView autoCompleteTextView;
    private final Button clearButton;

    private BookTitleAutoCompleteTextViewAdapter bookTitleAutoCompleteTextViewAdapter;
    private final static String TAG = BookTitleAutoCompleteTextView.class.getName();

    public BookTitleAutoCompleteTextView(Context context, final AutoCompleteTextView autoCompleteTextView, final Button clearButton) {
        this.autoCompleteTextView = autoCompleteTextView;
        this.clearButton = clearButton;
        this.autoCompleteTextView.setDropDownBackgroundResource(R.color.white);
        this.bookTitleAutoCompleteTextViewAdapter = new BookTitleAutoCompleteTextViewAdapter(context, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());

        autoCompleteTextView.setAdapter(bookTitleAutoCompleteTextViewAdapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.i(TAG, "================================== afterTextChanged ======================================");
                if (s.toString().equals("")) {
                    Log.i(TAG, "===========afterTextChanged getRecentKeywords=============");
                    bookPresenter.getRecentKeywords();
                    clearButton.setVisibility(View.GONE);
                } else {
                    bookPresenter.searchTitles(s.toString().trim());
                    clearButton.setVisibility(View.VISIBLE);
                }
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selection = parent.getItemAtPosition(position).toString();
                bookPresenter.searchBooks(selection);
            }

        });

        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && !"".equals(getText().trim())) {
                    String keyword = getText();
                    bookPresenter.searchBooks(keyword);
                    return true;
                }

                return false;
            }
        });

        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    bookPresenter.getRecentKeywords();
                }
            }
        });
    }

    public void setTitles(List titles){ this.bookTitleAutoCompleteTextViewAdapter.setTitles(titles); }
    public void dismissDropDown() { autoCompleteTextView.dismissDropDown(); }
    public void clearFocus(){ autoCompleteTextView.clearFocus(); }
    private String getText() {
        return autoCompleteTextView.getText().toString();
    }
    public void setBookPresenter(BookPresenter bookPresenter) {this.bookPresenter = bookPresenter;}
}
