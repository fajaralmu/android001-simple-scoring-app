package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.util.Constant;

public class ListUjianSiswaActivity extends Activity{//} AppCompatActivity {
    private static ListView list_student;
    private static TextView listSiswaText;

    SiswaService siswaService;
    List<Siswa> listSiswa = new ArrayList<>();
    List<Siswa> listSiswaFilter = new ArrayList<>();

    boolean iBound = false;

    private static CheckBox all_kelas, all_nama;

    private static EditText cari_input_nama;
    private static Spinner cari_kelas_spn;

    private String cari_nama="all", cari_kelas="all";

    List<Kelas> listKelas = new ArrayList<>();


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ujian_siswa);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        list_student = findViewById(R.id.list_siswa_view);
        listSiswaText = findViewById(R.id.textViewSiswa);

        cari_kelas_spn = findViewById(R.id.spinner_kelas);
        cari_input_nama = findViewById(R.id.editText_cari_nama);

//        all_kelas = findViewById(R.id.checkBox_all_kelas);
//        all_nama = findViewById(R.id.checkBox_all_nama);

//        all_kelas.setChecked(true);
//        all_nama.setChecked(true);

        Intent intent = new Intent(this, SiswaService.class);
        Log.i(Constant.tag, "Binding service: " + Boolean.toString(bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)));

        Navigate.cekLogin(this);
    //    init_spinner();
    }

    public void init_spinner_kelas() {
        ArrayAdapter<Kelas> adapter = new ArrayAdapter<Kelas>(ListUjianSiswaActivity.this,
                R.layout.spinner_item, listKelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cari_kelas_spn.setAdapter(adapter);
        cari_kelas_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelas k  = (Kelas) parent.getItemAtPosition(position);
                cari_kelas = k.getId().toString();
                Toast.makeText(ListUjianSiswaActivity.this, "Selected: " + k.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void listSiswaUpdate(View v){
        listSiswaFilter.clear();
        cari_nama = cari_input_nama.getText().toString();
        for(Siswa s:listSiswa){
             if(!s.getKelas().toString().equals(cari_kelas)){
                    continue;
            }
            if(!cari_nama.equals("")){
                if(!s.getNama().toLowerCase().contains(cari_nama.toLowerCase()))
                    continue;
            }

            listSiswaFilter.add(s);
        }
        populate_listView(listSiswaFilter);
    }

    public void allSiswa(){
        listSiswa.clear();
        String respons = siswaService.listSiswa();

        System.out.println(respons);
        List<String> objs = MyJSON.getObjFromArray(respons);
        List<List<String[]>> list_obj = new ArrayList<>();

        for (String s : objs) {
            List<String[]> obj = new ArrayList<String[]>();
            for (String x : MyJSON.extractObj(s)) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            Siswa siswa = (Siswa) MyJSON.getObj(new Siswa(), obj);
            System.out.println(siswa);
            list_obj.add(obj);
            listSiswa.add(siswa);
            Log.i(Constant.tag, siswa.toFullString());
        }
    //    populate_listView(listSiswa);
    }

    public void listSiswa(View v) {
        getData();
    }

    private void getData(){
        allKelas();
        allSiswa();
        init_spinner_kelas();
    }

    public void populate_listView(List<Siswa> listSiswa){
        ArrayAdapter<Siswa> adapter = new ArrayAdapter<Siswa>(this, R.layout.list_siswa_layout,listSiswa);
        list_student.setAdapter(adapter);
        list_student.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Siswa siswa = (Siswa)list_student.getItemAtPosition(position);
                        goToUjian(siswa);
                    }
                }
        );
    }

    public void goHome(View v){
        Navigate.navigate(this, HomeActivity.class);
    }

    public void goToUjian(Siswa s){
        Intent i = new Intent(this, UjianActivity.class);
        i.putExtra("id_siswa", s.getId());
        Log.i(Constant.tag,"put extra, id"+s.getId());
        startActivity(i);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SiswaService.LocalBinder binder = (SiswaService.LocalBinder) service;
            siswaService = binder.getService();
            Log.i(Constant.tag, siswaService.toString());
            siswaService = new SiswaService(getApplicationContext());
            getData();
            iBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            iBound = false;
        }
    };

    public void allKelas() {
        listKelas.clear();
        String respons = siswaService.listKelas();

        System.out.println(respons);
        List<String> objs = MyJSON.getObjFromArray(respons);
        List<List<String[]>> list_obj = new ArrayList<>();
        for (String s : objs) {
            List<String[]> obj = new ArrayList<String[]>();
            for (String x : MyJSON.extractObj(s)) {
                String[] prop = MyJSON.propVal(x);
                obj.add(prop);
            }
            Kelas k = (Kelas) MyJSON.getObj(new Kelas(), obj);
            System.out.println(k);
            list_obj.add(obj);
            listKelas.add(k);
        }
    }
}
