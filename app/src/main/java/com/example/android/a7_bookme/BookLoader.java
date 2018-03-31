package com.example.android.a7_bookme;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by koszojudit on 2017. 07. 11..
 */

public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private static final String LOG_TAG = BookLoader.class.getName();

    private String mUrl;

    // Constructs a new BookLoader object
    public BookLoader(Context context, String url) {
        super( context );
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // This is on a background thread.
    @Override
    public List<Book> loadInBackground() {

        // If there is no valid url, return early
        if (mUrl == null) {
            return null;
        }

        // Perform network request, parse the response, and extract a list of books matching search criteria
        return QueryUtils.fetchBookData( mUrl );
    }

}
