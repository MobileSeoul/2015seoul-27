package com.seoul.publicbooksearcher.domain.async_usecase;

import android.os.AsyncTask;

import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.domain.Book;
import com.seoul.publicbooksearcher.presentation.AsyncUseCaseListener;

import java.util.ArrayList;
import java.util.List;

public class SearchTitles implements AsyncUseCase<String> {

    private final static String TAG = SearchTitles.class.getName();

    private final BookRepository bookRepository;
    private AsyncUseCaseListener asyncUseCaseListener;
    private BandiAsyncTask naverAsyncTask;

    public SearchTitles(BookRepository bookRepository){
        this.bookRepository = bookRepository;
    }

    @Override
    public void execute(String keyword, AsyncUseCaseListener asyncUseCaseListener) {
        this.asyncUseCaseListener = asyncUseCaseListener;
        try {
            if(naverAsyncTask != null)
                naverAsyncTask.cancel(true);
            naverAsyncTask = new BandiAsyncTask();
            naverAsyncTask.execute(keyword);
        }catch (Exception e){
            asyncUseCaseListener.onError(e);
        }
    }

    private class BandiAsyncTask extends AsyncTask<String, Void, List<Book>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<Book> doInBackground(String... params) {
            try {
                return SearchTitles.this.bookRepository.selectByKeyword(params[0]);
            }catch (Exception e){
                asyncUseCaseListener.onError(e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            super.onPostExecute(books);

            if(books != null) {
                List<String> titles = new ArrayList();
                for (Book book : books) {
                    // 중복제거
                    if(!titles.contains(book.getTitle()))
                        titles.add(book.getTitle());
                }

                asyncUseCaseListener.onAfter(titles);
            }
        }
    }
}
