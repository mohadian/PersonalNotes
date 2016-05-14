package com.zagros.personalnotes.data.contract;

import android.net.Uri;
import android.provider.BaseColumns;

import com.zagros.personalnotes.utils.AppConstant;

public class ArchivesContract {
    public interface ArchivesColumns {
        String ARCHIVES_TITLE = "archives_title";
        String ARCHIVES_DESCRIPTION = "archives_description";
        String ARCHIVES_DATE_TIME = "archives_date_time";
        String ARCHIVES_CATEGORY = "archives_category";
        String ARCHIVES_TYPE = "archives_type";
    }

    public static final Uri URI_TABLE = AppConstant.BASE_CONTENT_URI.buildUpon().appendEncodedPath(AppConstant.PATH_ARCHIVES).build();

    public static class Archives implements ArchivesColumns, BaseColumns {
        public static final String CONTENT_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_ARCHIVES;
        public static final String CONTENT_ITEM_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_ARCHIVES;

        public static Uri buildArchiveUri(String archiveId) {
            return URI_TABLE.buildUpon().appendEncodedPath(archiveId).build();
        }

        public static String getArchiveId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}

