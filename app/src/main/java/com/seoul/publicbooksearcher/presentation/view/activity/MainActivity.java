package com.seoul.publicbooksearcher.presentation.view.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.seoul.publicbooksearcher.R;
import com.seoul.publicbooksearcher.data.BookRepository;
import com.seoul.publicbooksearcher.data.recent_keyword.RecentSearchKeywordRepository;
import com.seoul.publicbooksearcher.data.cache.book.BookCache;
import com.seoul.publicbooksearcher.data.crawler.BandinlunisAutoCompleteCrawler;
import com.seoul.publicbooksearcher.data.crawler.BaseLibrary;
import com.seoul.publicbooksearcher.data.crawler.SeoulLibrary;
import com.seoul.publicbooksearcher.domain.Library;
import com.seoul.publicbooksearcher.domain.async_usecase.AsyncUseCase;
import com.seoul.publicbooksearcher.domain.async_usecase.SearchBooks;
import com.seoul.publicbooksearcher.domain.async_usecase.SearchTitles;
import com.seoul.publicbooksearcher.domain.async_usecase.SortLibraries;
import com.seoul.publicbooksearcher.domain.usecase.AddRecentKeyword;
import com.seoul.publicbooksearcher.domain.usecase.GetRecentKeywords;
import com.seoul.publicbooksearcher.domain.usecase.IsOnline;
import com.seoul.publicbooksearcher.domain.usecase.UseCase;
import com.seoul.publicbooksearcher.presentation.presenter.BookPresenter;
import com.seoul.publicbooksearcher.presentation.view.adapter.BookListViewAdapter;
import com.seoul.publicbooksearcher.presentation.view.adapter.BookListViewItem;
import com.seoul.publicbooksearcher.presentation.view.component.ActionBarProgressBarView;
import com.seoul.publicbooksearcher.presentation.view.component.BookListView;
import com.seoul.publicbooksearcher.presentation.view.component.BookTitleAutoCompleteTextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity{

    private final static String TAG = MainActivity.class.getName();
    private RecyclerView listView;
    private BookPresenter bookPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Log.i(TAG, "=================================== Create UseCase ======================================");
        UseCase isOnline = new IsOnline(this);
        UseCase getRecentKeywords = new GetRecentKeywords(new RecentSearchKeywordRepository(this));
        UseCase addRecentKeyword = new AddRecentKeyword(new RecentSearchKeywordRepository(this));
        Map<Library, BookRepository> libraries = createLibraries();
        List<BookListViewItem> bookListViewItems = new ArrayList();
        for(Library library : libraries.keySet()) {
            Log.i(TAG, library.getName());
            bookListViewItems.add(new BookListViewItem(library));
        }
        AsyncUseCase searchBooks = new SearchBooks(libraries);
        AsyncUseCase searchTitles = new SearchTitles(new BandinlunisAutoCompleteCrawler());
        AsyncUseCase sortLibraries = new SortLibraries(this);

        Log.i(TAG, "=================================== Create View ==========================================");
        BookTitleAutoCompleteTextView bookTitleAutoCompleteTextView = new BookTitleAutoCompleteTextView(this, (AutoCompleteTextView) findViewById(R.id.auto_edit), (Button) findViewById(R.id.calc_clear_txt_Prise));

        listView = (RecyclerView) findViewById(R.id.book_list);
        listView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listView.setLayoutManager(layoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        //listView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        BookListViewAdapter bookListViewAdapter = new BookListViewAdapter(this, bookListViewItems);
        if(savedInstanceState != null)
            bookListViewAdapter.onRestoreInstanceState(savedInstanceState);
        listView.setAdapter(bookListViewAdapter);

        final BookListView bookListView = new BookListView(this, listView);

        Log.i(TAG, "=================================== Create Presenter =====================================");

        bookPresenter = new BookPresenter(this, isOnline, getRecentKeywords, addRecentKeyword, searchBooks, searchTitles, sortLibraries,bookTitleAutoCompleteTextView, bookListView);

        bookTitleAutoCompleteTextView.setBookPresenter(bookPresenter);

        AutoCompleteTextView autoedit = (AutoCompleteTextView) findViewById(R.id.auto_edit);
        Drawable img = getResources().getDrawable(R.mipmap.searchbar_icon);
        img.setBounds(0, 0, (int) (0.5 * img.getIntrinsicWidth()), (int) (0.5 * img.getIntrinsicHeight()));
        autoedit.setCompoundDrawables(img, null, null, null);
    }

    private Map createLibraries(){
        Map<Library, BookRepository> bookRepositoryMap = new HashMap<>();
        bookRepositoryMap.put(new Library("서울도서관", 37.5662020, 126.9778190, getResources().getColor(R.color.서울도서관)), new SeoulLibrary(new BookCache(this)));

        bookRepositoryMap.put(new Library("강남도서관", 37.5136910, 127.0470990, getResources().getColor(R.color.강남도서관)), new BaseLibrary(new BookCache(this), "111003"));
        bookRepositoryMap.put(new Library("강동도서관", 37.5385690, 127.1437600, getResources().getColor(R.color.강동도서관)), new BaseLibrary(new BookCache(this), "111004"));
        bookRepositoryMap.put(new Library("강서도서관", 37.5478818, 126.8599269, getResources().getColor(R.color.강서도서관)), new BaseLibrary(new BookCache(this), "111005"));
        bookRepositoryMap.put(new Library("고덕평생학습관", 37.5600000,127.1600000, getResources().getColor(R.color.고덕평생학습관)), new BaseLibrary(new BookCache(this), "111007"));


        bookRepositoryMap.put(new Library("고척도서관", 37.5100000,126.8500000, getResources().getColor(R.color.고척도서관)), new BaseLibrary(new BookCache(this), "111008"));
        bookRepositoryMap.put(new Library("구로도서관", 37.5000000,126.8900000, getResources().getColor(R.color.구로도서관)), new BaseLibrary(new BookCache(this), "111009"));
        bookRepositoryMap.put(new Library("개포도서관", 37.4800000,127.0600000, getResources().getColor(R.color.개포도서관)), new BaseLibrary(new BookCache(this), "111006"));
        bookRepositoryMap.put(new Library("남산도서관", 37.5500000, 126.9800000, getResources().getColor(R.color.남산도서관)), new BaseLibrary(new BookCache(this), "111010"));

        bookRepositoryMap.put(new Library("노원평생학습관", 37.6400000,127.0700000, getResources().getColor(R.color.노원평생학습관)), new BaseLibrary(new BookCache(this), "111022"));
        bookRepositoryMap.put(new Library("동대문도서관", 37.5700000, 127.0200000, getResources().getColor(R.color.동대문도서관)), new BaseLibrary(new BookCache(this), "111012"));
        bookRepositoryMap.put(new Library("도봉도서관", 37.6500000, 127.0100000, getResources().getColor(R.color.도봉도서관)), new BaseLibrary(new BookCache(this), "111011"));
        bookRepositoryMap.put(new Library("동작도서관", 37.5100000, 126.9400000, getResources().getColor(R.color.동작도서관)), new BaseLibrary(new BookCache(this), "111013"));

        bookRepositoryMap.put(new Library("마포평생학습관", 37.5500000, 126.9200000, getResources().getColor(R.color.마포평생학습관)), new BaseLibrary(new BookCache(this), "111014"));
        bookRepositoryMap.put(new Library("마포평생아현분관", 37.5500000,126.9600000, getResources().getColor(R.color.마포평생아현분관)), new BaseLibrary(new BookCache(this), "111031"));
        bookRepositoryMap.put(new Library("서대문도서관", 37.5800000,126.9400000, getResources().getColor(R.color.서대문도서관)), new BaseLibrary(new BookCache(this), "111016"));
        bookRepositoryMap.put(new Library("송파도서관", 37.5000000, 127.1300000, getResources().getColor(R.color.송파도서관)), new BaseLibrary(new BookCache(this), "111030"));

        bookRepositoryMap.put(new Library("양천도서관", 37.5300000, 126.8800000, getResources().getColor(R.color.양천도서관)), new BaseLibrary(new BookCache(this), "111015"));
        bookRepositoryMap.put(new Library("서울시립어린이도서관", 37.5800000, 126.9700000, getResources().getColor(R.color.서울시립어린이도서관)), new BaseLibrary(new BookCache(this), "111017"));
        bookRepositoryMap.put(new Library("영등포평생학습관", 37.5260740, 126.9073520, getResources().getColor(R.color.영등포평생학습관)), new BaseLibrary(new BookCache(this), "111018"));
        bookRepositoryMap.put(new Library("용산도서관", 37.5500000, 126.9800000, getResources().getColor(R.color.용산도서관)), new BaseLibrary(new BookCache(this), "111019"));

        bookRepositoryMap.put(new Library("정독도서관", 37.5800000, 126.9800000, getResources().getColor(R.color.정독도서관)), new BaseLibrary(new BookCache(this), "111020"));
        bookRepositoryMap.put(new Library("종로도서관", 37.5800000, 126.9700000, getResources().getColor(R.color.종로도서관)), new BaseLibrary(new BookCache(this), "111021"));



        ValueComparator valueComparator = new ValueComparator(bookRepositoryMap);
        TreeMap sortedBookRepositoryMap = new TreeMap(valueComparator);

        sortedBookRepositoryMap.putAll(bookRepositoryMap);

        return sortedBookRepositoryMap;
    }

    class ValueComparator implements Comparator<Library> {
        Map base;

        public ValueComparator(Map base) {
            this.base = base;
        }

        @Override
        public int compare(Library lhs, Library rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        bookPresenter.setActionBarProgressBarView(new ActionBarProgressBarView(menu));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sort) {
            bookPresenter.sortLibraries();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ((BookListViewAdapter) listView.getAdapter()).onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    private BackPressCloseHandler backPressCloseHandler = new BackPressCloseHandler(this);

    public class BackPressCloseHandler {

        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
