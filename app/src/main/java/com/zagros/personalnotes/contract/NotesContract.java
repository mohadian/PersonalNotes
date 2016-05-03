package com.zagros.personalnotes.contract;

import android.net.Uri;
import android.provider.BaseColumns;

import com.zagros.personalnotes.utils.AppConstant;

public class NotesContract {

    public interface NotesColumns {
        String NOTES_TITLE = "notes_title";
        String NOTES_DESCRIPTION = "notes_description";
        String NOTES_DATE = "note_date";
        String NOTES_TIME = "notes_time";
        String NOTES_IMAGE = "notes_image";
        String NOTES_TYPE = "notes_type";
        String NOTES_IMAGE_STORAGE_SELECTION = "notes_image_storage_selection";
    }

    public static final Uri URI_TABLE = AppConstant.BASE_CONTENT_URI.buildUpon().appendEncodedPath(AppConstant.TABLE_NOTES).build();

    public static class Notes implements  NotesColumns, BaseColumns {
        public static final String CONTENT_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_NOTES;
        public static final String CONTENT_ITEM_TYPE = AppConstant.VND_ANDROID_CURSOR_DIR_VND + AppConstant.CONTENT_AUTHORITY + "." + AppConstant.TABLE_NOTES;

        public static Uri buildNoteUri(String noteId) {
            return URI_TABLE.buildUpon().appendEncodedPath(noteId).build();
        }

        public static String getNoteId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
