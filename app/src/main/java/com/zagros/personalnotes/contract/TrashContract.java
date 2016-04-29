package com.zagros.personalnotes.contract;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrashContract {
    public interface TrashColumns {
        String TRASH_TITLE = "deleted_title";
        String TRASH_DESCRIPTION = "deleted_description";
        String TRASH_DATE_TIME = "deleted_date_time";
    }

    public static final String CONTENT_AUTHORITY = "com.zagros.personalnotes.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_DELETED = "deleted";
    public static final Uri URI_TABLE = BASE_CONTENT_URI.buildUpon().appendEncodedPath(PATH_DELETED).build();

    public static class Trash implements TrashColumns, BaseColumns {

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + CONTENT_AUTHORITY + ".deleted";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + CONTENT_AUTHORITY + ".deleted";

        public static Uri buildTrashUri(String deletedId) {
            return URI_TABLE.buildUpon().appendEncodedPath(deletedId).build();
        }

        public static String getTrashId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
