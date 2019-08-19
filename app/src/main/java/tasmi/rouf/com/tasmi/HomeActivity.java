package tasmi.rouf.com.tasmi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.MyLoadingDialog;

public class HomeActivity extends AppCompatActivity {

    private static String session_guru = "";
    private SharedPreferences sharedpreferences;
    private static MyLoadingDialog dialog_loading;
    private static SiswaServiceV2 siswaServiceV2;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dialog_loading = AlertBoy.loadingMulai(this);
        siswaServiceV2 = new SiswaServiceV2(this);

        // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        session_guru = sharedpreferences.getString("session", null);
        if (session_guru.equals(null)) {
            Navigate.navigate(this, LoginActivity.class);
        }
    }

    public void goToListSiswa(View v) {
        Navigate.navigate(this, ListSiswaActivity.class);
    }

    public void goToListNilaiSiswa(View v) {
        Navigate.navigate(this, ListNilaiSiswaAcivity.class);
    }


    public void goToListUjianSiswa(View v) {
        Navigate.navigate(this, ListUjianSiswaActivity.class);
    }

    public void goToEditAkun(View v) {
        Navigate.navigate(this, AkunEditActivity.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_option_beranda, menu);

        return super.onCreateOptionsMenu(menu);
    }

    public void keluar() {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final Integer response = siswaServiceV2.guruKeluar(session_guru);
                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog_loading.dismiss();
                                            if (response == 1) {
                                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                                editor.clear();
                                                editor.commit();
                                                Navigate.navigate(HomeActivity.this, LaunchActivity.class);
                                            }
                                        }
                                    });
                        }
                    }
                });
        t.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keluar:
                keluar();
                return true;

            case R.id.setting_server_id:
                Intent i2 = new Intent(this, SettingAppActivity.class);
                startActivity(i2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"this is the main page",Toast.LENGTH_SHORT);
    }
}
