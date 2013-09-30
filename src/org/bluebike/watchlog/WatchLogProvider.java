package org.bluebike.watchlog;

import static android.provider.BaseColumns._ID;
import static org.bluebike.watchlog.Constants.AUTHORITY;
import static org.bluebike.watchlog.Constants.CONTENT_URI;
import static org.bluebike.watchlog.Constants.TABLE_NAME;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class WatchLogProvider extends ContentProvider {
    private static final int ENTRIES = 1;
    private static final int ENTRY_ID = 2;

    // mime type of a directory of watch log entries
    private static final String CONTENT_TYPE
        = "vnd.android.cursor.dir/vnd.bluebike.watchdata";

    // mime type of a single event
    private static final String CONTENT_ITEM_TYPE
        = "vnd.android.cursor.item/vnd.bluebike.watchdata";

    private WatchData watchData;
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "watchdata", ENTRIES);
        uriMatcher.addURI(AUTHORITY, "watchdata/#", ENTRY_ID);
        watchData = new WatchData(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
            String selection, String[] selectionArgs, String orderBy) {
        if (uriMatcher.match(uri) == ENTRY_ID) {
            long id = Long.parseLong(uri.getPathSegments().get(1));
            selection = appendRowId(selection, id);
        }

        // Get database and run query
        SQLiteDatabase db = watchData.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, projection, selection,
                selectionArgs, null, null, orderBy);
        // Tell the cursor what uri to watch, so it knows when its
        // source data changes
        cursor.setNotificationUri(getContext().getContentResolver(),
                uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                return CONTENT_TYPE;
            case ENTRY_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknow URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = watchData.getWritableDatabase();

        // Validate the requested uri
        if (uriMatcher.match(uri) != ENTRIES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Insert into database
        long id = db.insertOrThrow(TABLE_NAME, null, values);

        // Notify any watchers of the change
        Uri newUri = ContentUris.withAppendedId(CONTENT_URI, id);
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection,
            String[] selectionArgs) {
        SQLiteDatabase db = watchData.getWritableDatabase();
        int count;

        switch (uriMatcher.match(uri)) {
            case ENTRIES:
                count = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case ENTRY_ID:
                long id = Long.parseLong(uri.getPathSegments().get(1));
                count = db.delete(TABLE_NAME, appendRowId(selection, id),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Notify any watchers of the change
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values,
            String selection, String[] selectionArgs) {
        SQLiteDatabase db = watchData.getWritableDatabase();
        int count;
        switch(uriMatcher.match(uri)) {
            case ENTRIES:
                count = db.update(TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ENTRY_ID:
                long id = Long.parseLong(uri.getPathSegments().get(1));
                count = db.update(TABLE_NAME, values, appendRowId(
                            selection, id), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Notify any watchers of the change
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /** Append an id test to a SQL selection expression */
    private String appendRowId(String selection, long id) {
        return _ID + "=" + id + (!TextUtils.isEmpty(selection)
                ? " AND (" + selection + ')'
                : "");
    }
}

       
