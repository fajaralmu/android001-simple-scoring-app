package tasmi.rouf.com.tasmi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;

/**
 * A login screen that offers login via email/password.
 */
public class AkunEditActivity extends AppCompatActivity {

    private String username_guru, password_guru;

    SharedPreferences sharedpreferences;

    SiswaService siswaService;


    private AutoCompleteTextView txt_nama_pengguna;
    private EditText txt_katasandi, txt_nama;
    private View mProgressView;
    private View mLoginFormView;

    private Guru guru;

    public AkunEditActivity(){
        guru = new Guru();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_akun);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // Set up the login form.
        txt_nama_pengguna =  findViewById(R.id.editText_namapengguna);
        txt_katasandi = findViewById(R.id.editText_katasandi);
        txt_nama =  findViewById(R.id.editText_nama) ;
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);


        Intent intent = new Intent(this, SiswaService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.i(Constant.tag,"USERNAME:"+ username_guru);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        username_guru = sharedpreferences.getString("username",null);
        password_guru = sharedpreferences.getString("password",null);
        if(username_guru.equals(null) || password_guru.equals(null)){
            Navigate.navigate(this, LoginActivity.class);
        }
    }

    public void masuk(View v){
        if(txt_nama_pengguna.getText().toString().equals("a") && txt_katasandi.getText().toString().equals("a")){
            AlertBoy.YesAlert(AkunEditActivity.this, "Login","Login oke");
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }else{
            AlertBoy.YesAlert(AkunEditActivity.this, "Login","Login gagal");
        }
    }

    private boolean setAkun(){
        try {
            if (username_guru.equals(null))
                return false;
            guru.setNamapengguna(username_guru);
            String s = siswaService.guruByUsername(guru);
            Log.i(Constant.tag, s);
            if (s.equals(""))
                return false;
            List<String[]> obj = new ArrayList<String[]>();
            List<String> objs = MyJSON.extractObj(s);
            for (String x : objs) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            guru = (Guru) MyJSON.getObj(new Guru(), obj);
            txt_nama.setText(guru.getNama());
            txt_nama_pengguna.setText(guru.getNamapengguna());
            txt_katasandi.setText(guru.getKatasandi());
            return true;
        }catch(Exception e){
            Log.i(Constant.tag, e.toString());
            return false;
        }
    }

    public void update(View v){
        String nama = txt_nama.getText().toString();
        String namapengguna = txt_nama_pengguna.getText().toString();
        String katasandi = txt_katasandi.getText().toString();
        Integer id = this.guru.getId();

       Guru g = new Guru(id, nama, namapengguna, katasandi);
        if (siswaService.editAkunGuru(g).equals(1)) {
            Log.i(Constant.tag, "Updated Successfully");
           Toast.makeText(AkunEditActivity.this, "Updated Successfully", Toast.LENGTH_SHORT);

        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Constant.tag,"service connected");
            SiswaService.LocalBinder binder = (SiswaService.LocalBinder) service;
            siswaService = binder.getService();
            Log.i(Constant.tag, siswaService.toString());
            siswaService = new SiswaService(getApplicationContext());

           setAkun();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}

