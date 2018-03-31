package com.example.android.a7_bookme;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by koszojudit on 2017. 07. 11..
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    private static Context mContext;
    // Interface type variable for BookOnClickHandler
    private final BookOnClickHandler mClickhandler;
    ArrayList<Book> books;

    // Constructor for the adapter
    public BookListAdapter(Context context, ArrayList<Book> books, BookOnClickHandler handler) {
        this.books = books;
        mContext = context;
        mClickhandler = handler;
    }

    // Inflate a new list item
    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from( parent.getContext() ).inflate( R.layout.list_item, parent, false );
        return new BookViewHolder( convertView );
    }

    // Bind the data of current Book object to the inflated list item view
    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {

        String image = "";
        Book currentBook = books.get( position );

        //Set author(s)
        holder.mBookAuthors.setText( currentBook.getAuthors() );

        //Set title
        holder.mBookTitle.setText( currentBook.getTitle() );

        // Set Image if available
        image = currentBook.getThumbnailLink();
        if (image != null && image.length() > 0) {
            Picasso.with( mContext ).load( currentBook.getThumbnailLink() ).into( holder.mBookImage );
        } else {
            Picasso.with( mContext ).load( R.drawable.book_pic ).into( holder.mBookImage );
        }

        // Set URL for book
        holder.mBookUrl = currentBook.getUrl();
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setData(ArrayList<Book> data) {
        this.books = data;
        notifyDataSetChanged();
    }

    public interface BookOnClickHandler {
        void onClick(String url);
    }

    // Class which describes how a list item is created
    public class BookViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mBookAuthors;
        public final TextView mBookTitle;
        public final ImageView mBookImage;
        public String mBookUrl;

        public BookViewHolder(View view) {
            super( view );
            mBookAuthors = (TextView) view.findViewById( R.id.book_author );
            mBookTitle = (TextView) view.findViewById( R.id.book_title );
            mBookImage = (ImageView) view.findViewById( R.id.book_image );
            view.setOnClickListener( this );
        }

        @Override
        public void onClick(View v) {
            mClickhandler.onClick( mBookUrl );
        }
    }
}

