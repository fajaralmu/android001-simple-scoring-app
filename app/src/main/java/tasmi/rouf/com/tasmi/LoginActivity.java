package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.MyLoadingDialog;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    SiswaServiceV2 siswaServiceV2;

    private MyLoadingDialog dialog_loading;
    private EditText mUsername;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    boolean iBound = false;

    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // Set up the login form.
        dialog_loading = AlertBoy.loadingMulai(this);
        mUsername = findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        siswaServiceV2 = new SiswaServiceV2(this);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, Context.MODE_PRIVATE);
        String session_guru =  sharedpreferences.getString("session",null);
        if(session_guru!=null){
            prosesValidasi(session_guru);
        }

    }

    public void masuk(View v) {
        final String namapengguna = mUsername.getText().toString();
        final String katasandi = mPasswordView.getText().toString();

        preLogin(namapengguna, katasandi);
    }

    private boolean validateSession(String session_guru) {
        try {
            if (session_guru.equals(null))
                return false;
            String s = siswaServiceV2.guruBySession(session_guru);
            Log.i(Constant.tag, s);
            if (s.equals(null) || s.equals(""))
                return false;
            List<String[]> obj = new ArrayList<String[]>();
            List<String> objs = MyJSON.extractObj(s);
            for (String x : objs) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            Guru guru = (Guru) MyJSON.getObj(new Guru(), obj);
            prosesLogin(guru);
            return true;
        } catch (Exception e) {
            Log.i(Constant.tag, e.toString());
            return false;
        }
    }

    private void preLogin(String namapengguna, String katasandi){
        Guru g = new Guru();
        g.setNamapengguna(namapengguna);
        g.setKatasandi(katasandi);
        prosesLogin(g);
    }

    private void prosesLogin(final Guru g) {
        dialog_loading.show();
        final Context ctx = getApplication();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final String responseLogin = siswaServiceV2.guruMasuk(g);
                        final boolean login = !responseLogin.equals(null) && !responseLogin.equals("");

                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog_loading.dismiss();
                                            String[] fullResponse = responseLogin.split("\\~");

                                            if (login && fullResponse.length == 2) {
                                                g.set_session(fullResponse[1]);
                                                g.setId(Integer.parseInt(fullResponse[0]));
                                                //AlertBoy.YesAlert(LoginActivity.this, "Login", "Login oke");
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.putInt("id", g.getId());
                                                editor.putString("session", g.get_session());
                                                editor.commit();
                                                Intent i = new Intent(ctx, HomeActivity.class);
                                                startActivity(i);
                                            } else {
                                                AlertBoy.YesAlert(LoginActivity.this, "Login", "Login gagal");
                                            }
                                        }
                                    });
                        }
                    }
                });
        t.start();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, LaunchActivity.class);
        startActivity(i);
    }

    private void prosesValidasi(final String session_guru) {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                       final boolean sessionValid =  validateSession(session_guru);
                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!sessionValid)
                                                dialog_loading.dismiss();
                                        }
                                    });
                        }
                    }
                });
        t.start();
    }


}

