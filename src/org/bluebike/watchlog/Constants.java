package org.bluebike.watchlog;

import android.provider.BaseColumns;
import android.net.Uri;

public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = "watchdata";
    // Columns in Watchlog database
    public static final String LOGNAME = "name";
    public static final String TIME = "timestamp";
    public static final String WTIME = "watchtime";
    public static final String DIFF = "difference";
    public static final String RATE = "rate";
    public static final String AUTHORITY = "org.bluebike.watchlog";
    public static final Uri CONTENT_URI = Uri.parse("content://" +
            AUTHORITY + "/" + TABLE_NAME);
    public static final Uri CONTENT_LOG_URI = Uri.parse("content://" +
            AUTHORITY + "/watchlogs");
    public static final String WATCHLOG_LOGNAME = "logname";
    public static final String TAG = "WatchLog";
}
