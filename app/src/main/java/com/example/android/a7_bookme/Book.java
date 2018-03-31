package com.example.android.a7_bookme;

/**
 * Created by koszojudit on 2017. 07. 10..
 */

public class Book {

    /**
     * Private variables for this class
     **/

    //Author of the book
    private String mAuthors;

    //Title of the book
    private String mTitle;

    //Link of book's thumbnail image
    private String mThumbnailLink;

    //URL of the book
    private String mUrl;

    /**
     * Constructor for the Book object
     **/

    public Book(String authors, String title, String thumbnailLink, String url) {
        mAuthors = authors;
        mTitle = title;
        mThumbnailLink = thumbnailLink;
        mUrl = url;
    }

    /**
     * Public getter methods to make the class member variables available for other classes
     **/

    // Get the author of the book
    public String getAuthors() {
        return mAuthors;
    }

    // Get the title of the book
    public String getTitle() {
        return mTitle;
    }

    // Get the link of the book's thumbnail image
    public String getThumbnailLink() {
        return mThumbnailLink;
    }

    // Get the url of the book
    public String getUrl() {
        return mUrl;
    }
}
