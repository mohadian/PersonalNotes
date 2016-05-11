package com.zagros.personalnotes.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.zagros.personalnotes.contract.ArchivesContract;
import com.zagros.personalnotes.contract.NotesContract;
import com.zagros.personalnotes.contract.TrashContract;
import com.zagros.personalnotes.utils.AppConstant;

public class AppDatabase extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    public interface Tables {
        String NOTES = AppConstant.TABLE_NOTES;
        String ARCHIVES = AppConstant.TABLE_ARCHIVES;
        String TRASH = AppConstant.TABLE_TRASH;
    }

    public AppDatabase(Context context) {
        super(context, AppConstant.DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.NOTES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NotesContract.NotesColumns.NOTES_TITLE + " TEXT NOT NULL,"
                + NotesContract.NotesColumns.NOTES_DESCRIPTION + " TEXT NOT NULL,"
                + NotesContract.NotesColumns.NOTES_TIME + " TEXT NOT NULL,"
                + NotesContract.NotesColumns.NOTES_TYPE + " TEXT NOT NULL,"
                + NotesContract.NotesColumns.NOTES_IMAGE_STORAGE_SELECTION + " INTEGER NOT NULL,"
                + NotesContract.NotesColumns.NOTES_IMAGE + " TEXT NOT NULL,"
                + NotesContract.NotesColumns.NOTES_DATE + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.ARCHIVES + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ArchivesContract.ArchivesColumns.ARCHIVES_TITLE + " TEXT NOT NULL,"
                + ArchivesContract.ArchivesColumns.ARCHIVES_DESCRIPTION + " TEXT NOT NULL,"
                + ArchivesContract.ArchivesColumns.ARCHIVES_CATEGORY + " TEXT NOT NULL,"
                + ArchivesContract.ArchivesColumns.ARCHIVES_TYPE + " TEXT NOT NULL,"
                + ArchivesContract.ArchivesColumns.ARCHIVES_DATE_TIME + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + Tables.TRASH + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TrashContract.TrashColumns.TRASH_TITLE + " TEXT NOT NULL,"
                + TrashContract.TrashColumns.TRASH_DESCRIPTION + " TEXT NOT NULL,"
                + TrashContract.TrashColumns.TRASH_DATE_TIME + " TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
        if (version == DATABASE_VERSION) {
            version = 2;
        }

        if (version != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + Tables.NOTES);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.ARCHIVES);
            db.execSQL("DROP TABLE IF EXISTS " + Tables.TRASH);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(AppConstant.DATABASE_NAME);
    }

    public void emptyTrash() {
        getWritableDatabase().delete(Tables.TRASH, null, null);
    }
}