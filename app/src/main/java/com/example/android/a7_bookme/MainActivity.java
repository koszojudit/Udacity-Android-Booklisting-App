package com.example.android.a7_bookme;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BookListAdapter.BookOnClickHandler, LoaderCallbacks<List<Book>> {

    // Constant value for the BookLoader ID
    private static final int BOOK_LOADER_ID = 1;

    // Tag for log messages
    private static final String LOG_TAG = MainActivity.class.getName();

    // Base URL from Google Books API
    private static final String BOOK_URL_BASE =
            "https://www.googleapis.com/books/v1/volumes?q=";
    // Google Books URL with the given key Android for the query result
    private static final String BOOK_URL_ANDROID =
            "https://www.googleapis.com/books/v1/volumes?q=android&maxResults=12";
    private static final String BOOK_QUERY_KEY = "query";
    private static final String SEARCH_WORD_KEY = "SEARCH_WORD_KEY";
    // Add a maximum results of 20 to the search query
    private static final String MAX_RESULTS = "&maxResults=10";
    final Context mContext = this;
    ArrayList<Book> books = new ArrayList<>();
    // Adapter for the Books list
    private BookListAdapter bookListAdapter;
    // Recycler view for the Books list
    private RecyclerView bookListView;
    // Search field where the user can add search input
    private EditText searchInput;
    //
    private String searchWord;
    // TextView visible only when the app starts
    private TextView startWithText;
    // TextView that is visible when there is a problem with the connection or the query
    private TextView mEmptyView;
    // ProgressBar that is visible when search request is performed and results are loading
    private View progressIndicator;
    // Boolean for internet connection status
    private boolean isInternetConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        // Set Layout Manager to the recycler view
        bookListView = (RecyclerView) findViewById( R.id.book_list );
        RecyclerView.LayoutManager lm = new LinearLayoutManager( this );
        bookListView.setLayoutManager( lm );

        // Set the adapter to the recycler view to take the list of books as input
        bookListAdapter = new BookListAdapter( this, books, this );
        bookListView.setAdapter( bookListAdapter );

        // Find reference to UI elements of the layout
        searchInput = (EditText) findViewById( R.id.search_bar );
        ImageButton searchButton = (ImageButton) findViewById( R.id.search_button );
        mEmptyView = (TextView) findViewById( R.id.no_books_view );
        progressIndicator = findViewById( R.id.progress_bar );
        startWithText = (TextView) findViewById( R.id.start_with_view );
        startWithText.setText( R.string.start_with );

        // Set isInternetConnected to true or false by calling our custom method
        isInternetConnected = checkInternetConnection();

        // Hide the keyboard when the app starts
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN );
        Log.e( LOG_TAG, "Keyboard hidden" );

        // Check if there is Internet connection
        if (!isInternetConnected) {
            Log.e( LOG_TAG, "This is called when there is NO Internet connection." );
            startWithText.setVisibility( View.GONE );
            progressIndicator.setVisibility( View.GONE );
            mEmptyView.setVisibility( View.VISIBLE );
            mEmptyView.setText( R.string.no_internet );
        } else {
            Log.e( LOG_TAG, "This is called when there IS Internet connection." );

            searchWord = searchInput.getText().toString().replaceAll( "\\s+", "" )
                    .toLowerCase();
            startWithText.setVisibility( searchWord.isEmpty() ? View.VISIBLE : View.GONE );

            startWithText.setText( R.string.start_with );
            // Get the LoaderManager
            LoaderManager loaderManager = getLoaderManager();
            // Initialize a Loader
            Bundle args = new Bundle();
            args.putString( BOOK_QUERY_KEY, BOOK_URL_ANDROID );
            getLoaderManager().initLoader( BOOK_LOADER_ID, args, this );
        }

        // Set a click listener to the ImageButton Search which sends query to the URL
        // base on the user input.
        searchButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Hide the keyboard after the Search button is clicked
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService( Context.INPUT_METHOD_SERVICE );
                inputMethodManager.hideSoftInputFromWindow( getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS );

                // Set isInternetConnected to true or false by calling our custom method
                isInternetConnected = checkInternetConnection();

                if (isInternetConnected) {
                    Log.e( LOG_TAG, "This is called when there IS Internet connection for SEARCH." );

                    // Search input: escape spaces, all lowercase
                    searchWord = searchInput.getText().toString().replaceAll( "\\s+", "" )
                            .toLowerCase();
                    if (searchWord.isEmpty()) {
                        Toast.makeText( MainActivity.this, getString( R.string.toast_enter_word ),
                                Toast.LENGTH_SHORT ).show();
                    } else {
                        Log.e( LOG_TAG, "This is called when there IS an Internet connection and a new search word." );

                        books.clear();
                        bookListAdapter.setData( books );
                        bookListAdapter.notifyDataSetChanged();

                        // Get the LoaderManager
                        LoaderManager loaderManager = getLoaderManager();
                        // Initialize a Loader
                        Bundle args = new Bundle();
                        args.putString( BOOK_QUERY_KEY, BOOK_URL_BASE + searchWord + MAX_RESULTS );
                        getLoaderManager().restartLoader( BOOK_LOADER_ID, args, MainActivity.this );
                        progressIndicator.setVisibility( View.VISIBLE );
                        startWithText.setVisibility( View.GONE );
                    }
                } else {
                    books.clear();
                    bookListAdapter.setData( books );
                    bookListAdapter.notifyDataSetChanged();

                    startWithText.setVisibility( View.GONE );
                    progressIndicator.setVisibility( View.GONE );
                    mEmptyView.setVisibility( View.VISIBLE );
                    mEmptyView.setText( R.string.no_internet );
                }
            }
        } );
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState( outState );
        outState.putString( SEARCH_WORD_KEY, searchWord );
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState( savedInstanceState );
        searchWord = savedInstanceState.getString( SEARCH_WORD_KEY );
        startWithText.setVisibility( searchWord.isEmpty() ? View.VISIBLE : View.GONE );
    }

    // Check the Internet Connection.
    private boolean checkInternetConnection() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService( CONNECTIVITY_SERVICE );

        // Get details on the currently active default network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Create intent for the parsed url of a book item on the list.
    // When clicking an item, web view of the book opens in a browser.
    @Override
    public void onClick(String url) {
        Uri bookUri = Uri.parse( url );
        Intent webIntent = new Intent( Intent.ACTION_VIEW, bookUri );
        if (webIntent.resolveActivity( getPackageManager() ) != null) {
            startActivity( webIntent );
        }
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {

        Uri baseUri = Uri.parse( args.getString( BOOK_QUERY_KEY ) );
        Uri.Builder uriBuilder = baseUri.buildUpon();
        return new BookLoader( mContext, uriBuilder.toString() );
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {

        // Hide progress indicator because the data has been loaded
        progressIndicator.setVisibility( View.GONE );

        // Clear the adapter of previous book data
        books.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            books.addAll( data );
            bookListAdapter.setData( books );
            bookListAdapter.notifyDataSetChanged();
        } else {
            // Set empty state text when no books found
            mEmptyView.setText( R.string.no_books );
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        books.clear();
    }
}
