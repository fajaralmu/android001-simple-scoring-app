package tasmi.rouf.com.tasmi;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import tasmi.rouf.com.config.Konfigurasi;
import tasmi.rouf.com.util.ServerAddress;

public class LaunchActivity extends AppCompatActivity {

    TextView server_ip;
    Konfigurasi konfig;
    ServerAddress sa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        konfig = new Konfigurasi(this);
        server_ip = findViewById(R.id.textview_server);
        sa= new ServerAddress(getApplicationContext());
        server_ip.setText("Server: "+sa.Read());
   }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_option, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.info_id:
                Toast.makeText(getApplicationContext(),"Info icon selected",Toast.LENGTH_SHORT).show();

            case R.id.setting_id:
                Toast.makeText(getApplicationContext(),"Setting icon selected",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, SettingAppActivity.class);
                startActivity(i);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void goToLogin(View v){
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
