package com.zagros.personalnotes.utils.dropbox;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;
import com.zagros.personalnotes.R;
import com.zagros.personalnotes.utils.AppConstant;

public class DropBoxDirectoryCreatorAsync extends AsyncTask<Void, Long, Boolean> {
    private DropboxAPI<?> mApi;
    private String mPath;
    private Context mContext;
    private OnDirectoryCreateFinished mListener;
    private String mName;
    private String mMessage;

    public DropBoxDirectoryCreatorAsync(Context mContext, DropboxAPI<?> mApi, String mName, String mPath, OnDirectoryCreateFinished mListener) {
        this.mContext = mContext;
        this.mApi = mApi;
        this.mName = mName;
        this.mPath = mPath;
        this.mListener = mListener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            mApi.createFolder(mPath);
            mMessage = mContext.getString(R.string.folder_created);

        } catch (DropboxException e) {
            mMessage = mContext.getString(R.string.folder_create_error);
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            mListener.onDirectoryCreateFinished(mName);
            Toast.makeText(mContext.getApplicationContext(), mMessage, Toast.LENGTH_LONG).show();
        }
    }


    public interface OnDirectoryCreateFinished {
        void onDirectoryCreateFinished(String dirName);
    }
}
