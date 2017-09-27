package com.seoul.publicbooksearcher.presentation.view.adapter;


import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.seoul.publicbooksearcher.R;
import com.seoul.publicbooksearcher.domain.Book;
import com.seoul.publicbooksearcher.domain.Location;
import com.seoul.publicbooksearcher.presentation.view.component.CircleView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookListViewAdapter extends ExpandableRecyclerAdapter<BookListViewAdapter.BookParentViewHolder, BookListViewAdapter.BookChildViewHolder> {

    private final Context context;
    private LayoutInflater mInflater;
    private final static String TAG = BookListViewAdapter.class.getName();
    private static final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    private static final float INITIAL_POSITION = 0.0f;
    private static final float ROTATED_POSITION = 180f;
    private List<BookListViewItem> bookListViewItemList;
    private Map<String, Integer> libraryPositionMap = new HashMap();


    public BookListViewAdapter(Context context, final List<BookListViewItem> bookListViewItemList) {
        super(bookListViewItemList);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        this.bookListViewItemList = bookListViewItemList;

        updateLibraryPosition();
    }

    private void updateLibraryPosition(){
        int position = 0;
        for(BookListViewItem bookListViewItem : bookListViewItemList) {
            libraryPositionMap.put(bookListViewItem.getLibraryName(), position);
            position++;
        }
    }

    @Override
    public BookParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.book_listview_parent_item, viewGroup, false);
        final BookParentViewHolder bookParentViewHolder = new BookParentViewHolder(view);
        
        return bookParentViewHolder;
    }

    @Override
    public BookChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.book_listview_child_item, viewGroup, false);
        return new BookChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(BookParentViewHolder bookParentViewHolder, int i, ParentListItem parentListItem) {
        BookListViewItem bookListViewItem = (BookListViewItem) parentListItem;

        bookParentViewHolder.library2.setText(bookListViewItem.getLibraryName());
        bookParentViewHolder.distance.setText(String.format("%.2fkm", bookListViewItem.getDistance()));
        Log.i(TAG, "========================" + bookListViewItem.getLibraryIconColor() + "==========================");
        bookParentViewHolder.libraryIcon.setBackgroundColor(bookListViewItem.getLibraryIconColor());
        bookParentViewHolder.libraryIcon.setTitleText(bookListViewItem.getLibraryName().charAt(0)+"");

        switch (bookListViewItem.getSearchState()){
            case BookListViewItem.SEARCH_COMPLETE:
                bookParentViewHolder.arrowUpImageView.setImageResource(R.mipmap.ic_keyboard_arrow_down_black_18dp);
                bookParentViewHolder.bookState.setText(
                        context.getText(R.string.loan_available)+" : " + bookListViewItem.getLoanPossibleSize() +
                                " / "+context.getText(R.string.loan_ing)+": " + bookListViewItem.getLoaningSize() +
                                " / "+context.getText(R.string.loan_unavailable)+" : " + bookListViewItem.getLoanImpossibleSize());
                bookParentViewHolder.progressBar.setVisibility(View.GONE);
                bookParentViewHolder.errorLayout.setVisibility(View.GONE);
                bookParentViewHolder.resultLayout.setVisibility(View.VISIBLE);
                if(bookListViewItem.noChild()) {
                    bookParentViewHolder.whiteLayerLayout.setVisibility(View.VISIBLE);
                    bookParentViewHolder.arrowUpImageView.setVisibility(View.GONE);
                }else{
                    bookParentViewHolder.whiteLayerLayout.setVisibility(View.INVISIBLE);
                    bookParentViewHolder.arrowUpImageView.setVisibility(View.VISIBLE);
                }
                break;
            case BookListViewItem.SEARCH_BEFORE:
                bookParentViewHolder.resultLayout.setVisibility(View.GONE);
                bookParentViewHolder.errorLayout.setVisibility(View.GONE);
                bookParentViewHolder.progressBar.setVisibility(View.VISIBLE);
                bookParentViewHolder.whiteLayerLayout.setVisibility(View.INVISIBLE);
                break;
            case BookListViewItem.ERROR:
                bookParentViewHolder.resultLayout.setVisibility(View.GONE);
                bookParentViewHolder.progressBar.setVisibility(View.GONE);
                bookParentViewHolder.errorLayout.setVisibility(View.VISIBLE);
                break;
        }

        switch (bookListViewItem.getSortState()){
            case BookListViewItem.SORT_COMPLETE:
                bookParentViewHolder.distance.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onBindChildViewHolder(BookChildViewHolder bookChildViewHolder, int i, Object o) {
        Book book = (Book)o;

        bookChildViewHolder.title.setText(book.getTitle());
        bookChildViewHolder.writerAndPublication.setText(book.getWriter() + " | " + book.getPublication());
        bookChildViewHolder.locationAndCallNumber.setText(book.getLocation() + " | " + book.getCallNumber());

        switch (book.getStatusCode()){
            case Book.BOOK_STATE_LOAN_POSSIBLE:
                bookChildViewHolder.state.setText(context.getText(R.string.loan_available));
                bookChildViewHolder.state.setTextColor(context.getResources().getColor(R.color.flatGreen));
                break;
            case Book.BOOK_STATE_LOAN_ING:
                bookChildViewHolder.state.setText(context.getText(R.string.loan_ing));
                bookChildViewHolder.state.setTextColor(context.getResources().getColor(R.color.flatYellow));
                break;
            case Book.BOOK_STATE_LOAN_IMPOSSIBLE:
                bookChildViewHolder.state.setText(context.getText(R.string.loan_unavailable));
                bookChildViewHolder.state.setTextColor(context.getResources().getColor(R.color.flatRed));
                break;
        }
    }


    public class BookParentViewHolder extends ParentViewHolder {
        public FrameLayout whiteLayerLayout;

        public TextView library2;
        public RelativeLayout progressBar;

        public CircleView libraryIcon;
        public TextView distance;
        public LinearLayout resultLayout;
        public LinearLayout errorLayout;

        public TextView bookState;
        public ImageView arrowUpImageView;

        public BookParentViewHolder(View v) {
            super(v);
            whiteLayerLayout = (FrameLayout) v.findViewById(R.id.white_layer_layout);
            library2 = (TextView) v.findViewById(R.id.book_library2);
            distance = (TextView) v.findViewById(R.id.library_distance);
            libraryIcon = (CircleView) v.findViewById(R.id.library_icon);
            progressBar = (RelativeLayout) v.findViewById(R.id.progress_layout);
            resultLayout = (LinearLayout) v.findViewById(R.id.result_layout);
            errorLayout = (LinearLayout) v.findViewById(R.id.error_layout);
            arrowUpImageView = (ImageView) v.findViewById(R.id.arrow);
            bookState = (TextView) v.findViewById(R.id.library_state);
        }

        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (!HONEYCOMB_AND_ABOVE) {return;}
            if (expanded) {
                arrowUpImageView.setRotation(ROTATED_POSITION);
            } else {
                arrowUpImageView.setRotation(INITIAL_POSITION);
            }
        }
    }

    public class BookChildViewHolder extends ChildViewHolder {
        public TextView title;
        public TextView writerAndPublication;
        public TextView locationAndCallNumber;
        public TextView state;

        public BookChildViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.book_title2);
            writerAndPublication = (TextView) v.findViewById(R.id.book_writer_publication);
            locationAndCallNumber = (TextView) v.findViewById(R.id.book_location_callNumber);
            state = (TextView) v.findViewById(R.id.book_state);
        }
    }

    public void clearChildItems(String library){
        int position = getPosition(library);
        if(position != -1){
            int childSize = bookListViewItemList.get(position).childSize();
            bookListViewItemList.get(position).clearBooks();
            for(int i = 0; i < childSize; i++)
                notifyChildItemRemoved(position, i);
        }
    }


    public void updateItem(String library, List<Book> books) {
        int position = getPosition(library);
        if(position != -1) {
            Log.i(TAG, "updateItem : " + library + "(library), " + position + "(position)");

            bookListViewItemList.get(position).arrange(books);
            int childSize = bookListViewItemList.get(position).childSize();
            for(int j=0; j< childSize; j++)
                notifyChildItemInserted(position, j);

            notifyParentItemChangedByHandler(position);
        }
    }

    private int getPosition(String library){
        return libraryPositionMap.get(library);
    }

    public void progressVisible(String library) {
        int position = getPosition(library);
        bookListViewItemList.get(position).setSearchState(BookListViewItem.SEARCH_BEFORE);
        notifyParentItemChangedByHandler(position);
    }

    public void progressGone(String library) {
        int position = getPosition(library);
        bookListViewItemList.get(position).setSearchState(BookListViewItem.SEARCH_COMPLETE);
        notifyParentItemChangedByHandler(position);
    }

    public void showError(String library, String message) {
        int position = getPosition(library);
        bookListViewItemList.get(position).setSearchState(BookListViewItem.ERROR);
        notifyParentItemChangedByHandler(position);
    }

    public void sort(Location currentLocation) {
        for(BookListViewItem bookListViewItem : bookListViewItemList) {
            bookListViewItem.calcDistance(currentLocation);
            bookListViewItem.setSortState(BookListViewItem.SORT_COMPLETE);
        }

        Collections.sort(bookListViewItemList, new Comparator<BookListViewItem>() {
            @Override
            public int compare(BookListViewItem lhs, BookListViewItem rhs) {
                if(lhs.getDistance() < rhs.getDistance()) return -1;
                else return 1;
            }
        });

        for(int i=0; i<bookListViewItemList.size(); i++)
            notifyParentItemChangedByHandler(i);
        updateLibraryPosition();
    }

    private Handler handler = new Handler();
    private void notifyParentItemChangedByHandler(final int position) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyParentItemChanged(position);
            }
        });
    }

}