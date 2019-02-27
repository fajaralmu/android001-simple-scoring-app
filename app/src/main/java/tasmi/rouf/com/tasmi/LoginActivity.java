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

import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    SiswaService siswaService;

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
        mUsername = findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, Context.MODE_PRIVATE);

        Intent intent = new Intent(this, SiswaService.class);
        Boolean.toString(bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE));

    }

    public void masuk(View v) {
        final String namapengguna = mUsername.getText().toString();
        final String katasandi = mPasswordView.getText().toString();

        Guru g = new Guru();
        g.setNamapengguna(namapengguna);
        g.setKatasandi(katasandi);
        int success = 1;
        boolean login = siswaService.guruMasuk(g).equals(success);
        if (login) {
            AlertBoy.YesAlert(LoginActivity.this, "Login", "Login oke");
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("username", g.getNamapengguna());
            editor.putString("password", g.getKatasandi());

            editor.commit();

            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } else {
            AlertBoy.YesAlert(LoginActivity.this, "Login", "Login gagal");
        }
    }


    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SiswaService.LocalBinder binder = (SiswaService.LocalBinder) service;
            siswaService = binder.getService();
            Log.i(Constant.tag, siswaService.toString());
            siswaService = new SiswaService(getApplicationContext());

            iBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBound = true;
        }
    };


}

