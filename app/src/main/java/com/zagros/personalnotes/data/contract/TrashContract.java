package com.zagros.personalnotes.data.contract;

import android.net.Uri;
import android.provider.BaseColumns;

import com.zagros.personalnotes.utils.AppConstant;

public class TrashContract {
    public interface TrashColumns {
        String TRASH_TITLE = "deleted_title";
        String TRASH_DESCRIPTION = "deleted_description";
        String TRASH_DATE_TIME = "deleted_date_time";
    }

    public static final Uri URI_TABLE = AppConstant.BASE_CONTENT_URI.buildUpon().appendEncodedPath(AppConstant.TABLE_TRASH).build();

    public static class Trash implements TrashColumns, BaseColumns {

        public static final String CONTENT_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_TRASH;
        public static final String CONTENT_ITEM_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_TRASH;

        public static Uri buildTrashUri(String deletedId) {
            return URI_TABLE.buildUpon().appendEncodedPath(deletedId).build();
        }

        public static String getTrashId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
