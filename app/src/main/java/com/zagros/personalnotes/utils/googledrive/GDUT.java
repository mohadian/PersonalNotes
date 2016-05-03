package com.zagros.personalnotes.utils.googledrive;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.zagros.personalnotes.utils.AppConstant;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final public class GDUT {

    private GDUT() {
    }

    private static final String L_TAG = "_";

    public static final String MYROOT = "PersonalNotes";
    static final String TMP_FILENM = "temp";
    public static final String JPEG_EXT = AppConstant.JPG;
    public static final String MIME_JPEG = "image/jpeg";
    public static final String MIME_FLDR = "application/vnd.google-apps.folder";

    static final String TITL_FMT = "yyMMdd-HHmmss";

    static final int BUF_SZ = 2048;

    private static GDUT mInst;
    static SharedPreferences pfs;
    static Context acx;

    public static GDUT init(Context ctx) {
        if (mInst == null) {
            acx = ctx.getApplicationContext();
            pfs = PreferenceManager.getDefaultSharedPreferences(acx);
            mInst = new GDUT();
        }
        return mInst;
    }

    public final static class AM {
        private AM() {
        }

        private static final String ACC_NAME = "account_name";
        public static final int FAIL = -1;
        public static final int UNCHANGED = 0;
        public static final int CHANGED = +1;

        private static String mCurrEmail = null;  // cache locally

        public static Account[] getAllAccnts() {
            return AccountManager.get(GDUT.acx).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        }

        public static Account getPrimaryAccnt(boolean bOneOnly) {
            Account[] accts = getAllAccnts();
            if (bOneOnly)
                return accts == null || accts.length != 1 ? null : accts[0];
            return accts == null || accts.length == 0 ? null : accts[0];
        }

        public static Account getActiveAccnt() {
            return emil2Accnt(getActiveEmil());
        }

        public static String getActiveEmil() {
            if (mCurrEmail != null) {
                return mCurrEmail;
            }
            return GDUT.pfs.getString(ACC_NAME, null);
        }

        public static Account emil2Accnt(String emil) {
            if (emil != null) {
                Account[] accounts =
                        AccountManager.get(GDUT.acx).getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
                for (Account account : accounts) {
                    if (emil.equalsIgnoreCase(account.name)) {
                        return account;
                    }
                }
            }
            return null;
        }

        public static int setEmil(String newEmil) {
            int result = FAIL;

            String prevEmil = getActiveEmil();
            if ((prevEmil == null) && (newEmil != null)) {
                result = CHANGED;
            } else if ((prevEmil != null) && (newEmil == null)) {
                result = UNCHANGED;
            } else if (prevEmil != null) {
                result = prevEmil.equalsIgnoreCase(newEmil) ? UNCHANGED : CHANGED;
            }
            if (result == CHANGED) {
                mCurrEmail = newEmil;
                GDUT.pfs.edit().putString(ACC_NAME, newEmil).apply();
            }
            return result;
        }

        public static void removeActiveAccnt() {
            mCurrEmail = null;
            GDUT.pfs.edit().remove(ACC_NAME).apply();
        }
    }

    public static File bytes2File(byte[] buf, File fl) {
        if (buf == null || fl == null) return null;
        BufferedOutputStream bs = null;
        try {
            bs = new BufferedOutputStream(new FileOutputStream(fl));
            bs.write(buf);
        } catch (Exception e) {
            le(e);
        } finally {
            if (bs != null) try {
                bs.close();
            } catch (Exception e) {
                le(e);
            }
        }
        return fl;
    }

    public static byte[] file2Bytes(File file) {
        if (file != null) try {
            return is2Bytes(new FileInputStream(file));
        } catch (Exception e) {
            le(e);
        }
        return null;
    }

    static byte[] is2Bytes(InputStream is) {
        byte[] buf = null;
        BufferedInputStream bufIS = null;
        if (is != null) try {
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            bufIS = new BufferedInputStream(is);
            buf = new byte[BUF_SZ];
            int cnt;
            while ((cnt = bufIS.read(buf)) >= 0) {
                byteBuffer.write(buf, 0, cnt);
            }
            buf = byteBuffer.size() > 0 ? byteBuffer.toByteArray() : null;
        } catch (Exception e) {
            le(e);
        } finally {
            try {
                if (bufIS != null) bufIS.close();
            } catch (Exception e) {
                le(e);
            }
        }
        return buf;
    }

    public static String time2Titl(Long milis) {
        Date dt = (milis == null) ? new Date() : (milis >= 0) ? new Date(milis) : null;
        return (dt == null) ? null : new SimpleDateFormat(TITL_FMT, Locale.US).format(dt);
    }

    static String titl2Month(String titl) {
        return titl == null ? null : ("20" + titl.substring(0, 2) + "-" + titl.substring(2, 4));
    }

    private static String stack2String(Throwable ex) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        try {
            ex.printStackTrace(printWriter);
            return result.toString();
        } finally {
            printWriter.close();
        }
    }

    static void le(Throwable ex) {
        String msg = (ex == null || ex.getMessage() == null) ? "" : ex.getMessage() + "\n";
        try {
            Log.e(L_TAG, msg + stack2String(ex));
        } catch (Exception e) {
        }
    }
}
