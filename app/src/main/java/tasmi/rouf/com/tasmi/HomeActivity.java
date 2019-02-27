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

import tasmi.rouf.com.util.Constant;

public class HomeActivity extends AppCompatActivity {

    private String username_guru = "", password_guru = "";
    SharedPreferences sharedpreferences;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

       // getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        username_guru = sharedpreferences.getString("username",null);
        password_guru = sharedpreferences.getString("password",null);
        if(username_guru.equals(null) || password_guru.equals(null)){
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




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.keluar:
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Navigate.navigate(this, LoginActivity.class);
                return true;

            case R.id.setting_server_id:
                Intent i2  = new Intent(this, SettingAppActivity.class);
                startActivity(i2);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
