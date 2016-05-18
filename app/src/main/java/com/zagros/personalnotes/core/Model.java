package com.zagros.personalnotes.core;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;

/**
 * A Model is a class used to manipulate stored data, as well as provide getters for the data. It
 * provides the {@link Presenter} with an interface through which to load and update the data (MVP
 * architectural pattern).
 */
public interface Model {

    /**
     * @return an array of {@link QueryEnum} that can be processed by the model
     */
    QueryEnum[] getQueries();

    /**
     * Updates the data saved in the model from the {@code cursor} and associated {@code query}.
     *
     * @return true if the data could be read properly from cursor.
     */
    boolean readDataFromCursor(Cursor cursor, QueryEnum query);

    /**
     * Creates the cursor loader for the given loader id and data source {@code uri}.
     * <p/>
     * The {@code loaderId} corresponds to the id of the query, as defined in {@link QueryEnum}. The
     * {@code args} may contain extra arguments required to create the query.
     * <p/>
     * The returned cursor loader is managed by the {@link android.app.LoaderManager}, as part
     * of the {@link android.app.Fragment}
     *
     * @return the cursor loader.
     */
    Loader<Cursor> createCursorLoader(int loaderId, Uri uri, Bundle args);

    /**
     * Updates this Model according to a user {@code action} and {@code args}.
     * <p/>
     * Add the constants used to store values in the bundle to the Model implementation class as
     * final static protected strings.
     *
     * @return true if successful.
     */
    boolean requestModelUpdate(UserActionEnum action, @Nullable Bundle args);
}
