package com.seoul.publicbooksearcher.domain.async_usecase;

import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;

import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.domain.exception.BookSearchException;
import com.seoul.publicbooksearcher.domain.Library;
import com.seoul.publicbooksearcher.domain.SearchResult;
import com.seoul.publicbooksearcher.presentation.AsyncUseCaseListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchBooks implements AsyncUseCase<String> {

    private static final String TAG = SearchBooks.class.getName();
    private AsyncUseCaseListener asyncUseCaseListener;

    private Map<Library, BookRepository> bookRepositoryMap;
    private List<LibraryAsyncTask> libraryAsyncTaskList = new ArrayList();

    public SearchBooks(Map<Library, BookRepository> bookRepositoryMap){
        this.bookRepositoryMap = bookRepositoryMap;
    }

    @Override
    public void execute(String keyword, final AsyncUseCaseListener asyncUseCaseListener) {
        this.asyncUseCaseListener = asyncUseCaseListener;

        for(LibraryAsyncTask libraryAsyncTask : libraryAsyncTaskList)
            libraryAsyncTask.cancel(false);
        libraryAsyncTaskList.clear();

        for(Library library : bookRepositoryMap.keySet()){
            LibraryAsyncTask libraryAsyncTask = new LibraryAsyncTask(library.getName(), bookRepositoryMap.get(library));
            libraryAsyncTaskList.add(libraryAsyncTask);
            if (Build.VERSION.SDK_INT >= 11)
                libraryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, keyword);
            else
                libraryAsyncTask.execute(keyword);
        }

    }

    private class LibraryAsyncTask extends AsyncTask<String, Void, SearchResult> {

        private BookRepository bookRepository;
        private String libraryName;

        public LibraryAsyncTask(String libraryName, BookRepository bookRepository){
            this.bookRepository = bookRepository;
            this.libraryName = libraryName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            asyncUseCaseListener.onBefore(libraryName);
        }

        @Override
        protected SearchResult doInBackground(String... params) {
            int count = 0;
            int maxTries = 3;
            while(true) {
                try {
                    return new SearchResult(new Library(libraryName, bookRepository.selectByKeyword(params[0])));
                } catch (Exception e) {
                    if (++count == maxTries){
                        asyncUseCaseListener.onError(new BookSearchException(e.getMessage(), libraryName));
                        return null;
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(SearchResult searchResult) {
            super.onPostExecute(searchResult);
            if(searchResult != null)
                asyncUseCaseListener.onAfter(searchResult);
        }
    }
}

