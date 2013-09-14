package org.bluebike.watchlog;

import android.provider.BaseColumns;

public interface Constants extends BaseColumns {
    public static final String TABLE_NAME = "watchdata";
    // Columns in Watchlog database
    public static final String TIME = "timestamp";
    public static final String WTIME = "watchtime";
    public static final String DIFF = "difference";
    public static final String RATE = "rate";
} 
