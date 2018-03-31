package com.example.android.a7_bookme;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by koszojudit on 2017. 07. 11..
 */

public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Private constructor for this class which is only meant to hold static variables and methods.
     * The class can be accessed directly from the class name QueryUtils (an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<Book> extractFeaturesFromJson(String booksJSON) {

        // If the JSON string is empty or null, return early.
        if (TextUtils.isEmpty( booksJSON )) {
            return null;
        }

        // Create an empty ArrayList to which we can start adding books to
        List<Book> books = new ArrayList<>();

        // Try to parse the JSON response string.
        // Catch JSONException exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Convert JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject( booksJSON );

            JSONArray booksArray;
            if (baseJsonResponse.has( "items" )) {
                // Extract JSONArray "items", which represents a list of books
                booksArray = baseJsonResponse.getJSONArray( "items" );

                // Create a Book object for each book in the bookArray,
                for (int i = 0; i < booksArray.length(); i++) {
                    // Get a single book at a given position within the list of books
                    JSONObject currentBook = booksArray.getJSONObject( i );

                    // For a given book, extract JSONObject "volumeInfo", which represents a list information about the book
                    JSONObject volumeInfo = currentBook.getJSONObject( "volumeInfo" );

                    // Get value for "title" key
                    String title = volumeInfo.getString( "title" );

                    /// Extract "authors" JSONArray which may represents a list of authors
                    JSONArray authorsArray;
                    String authors = "";

                    if (volumeInfo.has( "authors" )) {
                        authorsArray = volumeInfo.getJSONArray( "authors" );

                        if (authorsArray.length() > 1) {
                            authors = authorsArray.join( ", " ).replaceAll( "\"", "" );
                        } else if (authorsArray.length() == 1) {
                            authors = authorsArray.getString( 0 );
                        } else if (authorsArray.length() == 0) {
                            authors = "";
                        }
                    }

                    // Get value for "thumbnail" if the key exists
                    String thumbnailLink = "";
                    if (volumeInfo.has( "imageLinks" )) {
                        JSONObject imageLinks = volumeInfo.getJSONObject( "imageLinks" );
                        if (imageLinks.has( "thumbnail" )) {
                            thumbnailLink = imageLinks.getString( "thumbnail" );
                        } else {
                            thumbnailLink = "";
                        }
                    }

                    // Extract the URL of the book
                    String url = null;
                    if (volumeInfo.has( "infoLink" )) {
                        url = volumeInfo.getString( "infoLink" );
                    }

                    // Create a new Book object from the JSON response
                    Book booksObject = new Book( authors, title, thumbnailLink, url );

                    // Add the new Book to the list of books
                    books.add( booksObject );
                }
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block.
            // Print a log message about the exception.
            Log.e( LOG_TAG, "Problem retrieving JSON results", e );
        }

        // Return the list of books
        return books;
    }

    // Query the Google Books API and return a list of Book objects.

    public static List<Book> fetchBookData(String requestUrl) {

        // Delay the network response by 2 sec, in order to see how the progress bar works
        try {
            Thread.sleep( 2000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl( requestUrl );

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest( url );
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem with making the HTTP request.", e );
        }

        // Return the list of Books
        return extractFeaturesFromJson( jsonResponse );
    }

    // Return a new URL object from the given string URL

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e( LOG_TAG, "Problem with building the URL", e );
        }
        return url;
    }

    // Make an HTTP request to the given URL and return a String as the response.

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod( "GET" );
            urlConnection.setReadTimeout( 1000 );
            urlConnection.setConnectTimeout( 15000 );
            urlConnection.connect();

            // If the request was successful (response code 200), read the input stream and parse the response
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            } else {
                Log.e( LOG_TAG, "Error response code: " + urlConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem retrieving JSON result: ", e );
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // Convert the InputStream into a String which contains the whole JSON response from the server.

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream, Charset.forName( "UTF-8" ) );
            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();
            while (line != null) {
                result.append( line );
                line = reader.readLine();
            }
        }
        return result.toString();
    }
}
