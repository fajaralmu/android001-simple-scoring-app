package tasmi.rouf.com.tasmi;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import tasmi.rouf.com.config.Konfigurasi;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.ServerAddress;

public class SettingAppActivity extends AppCompatActivity {

    private EditText txt_server;
    private TextView txt_current_server;
    private Konfigurasi konfig;// = new Konfigurasi();
    private String alamat_server;
    private ServerAddress sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_app);

        txt_server = findViewById(R.id.editText_server_ip);
        txt_current_server = findViewById(R.id.textView_output);
//        konfig = new Konfigurasi(this);
//
//
        sa = new ServerAddress(getApplicationContext());
        alamat_server = sa.Read();
        txt_server.setText(alamat_server);
        txt_current_server.setText(alamat_server);

    }


    public void simpanSetting(View v){
        final String server_ip_new = txt_server.getText().toString();
        if(sa.Save(server_ip_new)){
            Log.i(Constant.tag,"Saved");
            txt_current_server.setText(server_ip_new);
        }
    }

    public void goToLaunch(View v){
        Intent i = new Intent(this, LaunchActivity.class);
        startActivity(i);
    }

}
