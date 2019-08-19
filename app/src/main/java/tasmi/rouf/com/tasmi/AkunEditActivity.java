package tasmi.rouf.com.tasmi;

import android.app.Activity;
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
import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.MyLoadingDialog;

/**
 * A login screen that offers login via email/password.
 */
public class AkunEditActivity extends Activity {

    private String session_guru;

    SharedPreferences sharedpreferences;

    SiswaServiceV2 siswaServiceV2;
    private static MyLoadingDialog dialog_loading;

    private AutoCompleteTextView txt_nama_pengguna;
    private EditText txt_katasandi, txt_nama;
    private View mProgressView;
    private View mLoginFormView;

    private Guru guru;

    public AkunEditActivity() {
        guru = new Guru();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_akun);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        // Set up the login form.
        txt_nama_pengguna = findViewById(R.id.editText_namapengguna);
        txt_katasandi = findViewById(R.id.editText_katasandi);
        txt_nama = findViewById(R.id.editText_nama);
        dialog_loading = AlertBoy.loadingMulai(this);
        siswaServiceV2 = new SiswaServiceV2(this);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        session_guru = sharedpreferences.getString("session", null);
        if (!session_guru.equals(null)) {
            cekAkun();
        }else{
            Navigate.navigate(this, LoginActivity.class);
        }
    }

    public void masuk(View v) {
        if (txt_nama_pengguna.getText().toString().equals("a") && txt_katasandi.getText().toString().equals("a")) {
            AlertBoy.YesAlert(AkunEditActivity.this, "Login", "Login oke");
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        } else {
            AlertBoy.YesAlert(AkunEditActivity.this, "Login", "Login gagal");
        }
    }

    private boolean setAkun() {
        try {
            if (session_guru.equals(null))
                return false;
            String s = siswaServiceV2.guruBySession(session_guru);
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

            return true;
        } catch (Exception e) {
            Log.i(Constant.tag, e.toString());
            return false;
        }
    }

    public void cekAkun() {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final boolean akunValid = setAkun();
                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog_loading.dismiss();
                                            if (akunValid) {
                                                txt_nama.setText(guru.getNama());
                                                txt_nama_pengguna.setText(guru.getNamapengguna());
                                                txt_katasandi.setText(guru.getKatasandi());
                                            }

                                        }
                                    });
                        }
                    }
                });
        t.start();
    }

    public void update(View v) {
        String nama = txt_nama.getText().toString();
        String namapengguna = txt_nama_pengguna.getText().toString();
        String katasandi = txt_katasandi.getText().toString();
        Integer id = this.guru.getId();

        Guru g = new Guru(id, nama, namapengguna, katasandi);
        prosesUpdate(g);
    }

    public void prosesUpdate(final Guru g) {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        final boolean updateOK = siswaServiceV2.editAkunGuru(g, session_guru).equals(1);

                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog_loading.dismiss();
                                            if (updateOK) {
                                                Log.i(Constant.tag, "Updated Successfully");
                                                Toast.makeText(AkunEditActivity.this, "Updated Successfully", Toast.LENGTH_SHORT);

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
        Navigate.navigate(this, HomeActivity.class);
    }


}

