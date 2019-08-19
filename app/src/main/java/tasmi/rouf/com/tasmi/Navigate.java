package tasmi.rouf.com.tasmi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import tasmi.rouf.com.util.Constant;

public class Navigate {

    public static void navigate(Context c, Class className) {
        Intent i = new Intent(c, className);
        c.startActivity(i);
    }

    public static void cekLogin(Context c) {
        SharedPreferences sharedpreferences = c.getSharedPreferences(Constant.PREF_AKUN, c.MODE_PRIVATE);
        String session_guru = sharedpreferences.getString("session", null);
        if (session_guru.equals(null) || session_guru.equals("")) {
            Navigate.navigate(c, LoginActivity.class);
        }
    }
}
