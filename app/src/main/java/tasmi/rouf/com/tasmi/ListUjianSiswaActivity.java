package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import tasmi.rouf.com.model.Soal;
import tasmi.rouf.com.model.Ujian;
import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.ItemRecyclerAdapter;
import tasmi.rouf.com.util.MyLoadingDialog;

public class ListUjianSiswaActivity extends Activity {//} AppCompatActivity {
    private static RecyclerView list_student;
    private static TextView listSiswaText;

    SiswaServiceV2 siswaServiceV2;
    List<Siswa> listSiswa = new ArrayList<>();
    private static MyLoadingDialog dialog_loading;

    private static CheckBox all_kelas, all_nama;

    private static EditText cari_input_nama;
    private static Spinner cari_kelas_spn;

    private String cari_nama = "", cari_kelas = Constant.DEFAULT_KELAS;
    private SharedPreferences sharedpreferences;
    private String session_guru = "";
    private boolean load_more = false, load_all = false;


    List<Kelas> listKelas = new ArrayList<>();


    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ujian_siswa);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        list_student = findViewById(R.id.recycleView);
        listSiswaText = findViewById(R.id.textViewSiswa);

        cari_kelas_spn = findViewById(R.id.spinner_kelas);
        cari_input_nama = findViewById(R.id.editText_cari_nama);

//        all_kelas = findViewById(R.id.checkBox_all_kelas);
//        all_nama = findViewById(R.id.checkBox_all_nama);

//        all_kelas.setChecked(true);
//        all_nama.setChecked(true);

        siswaServiceV2 = new SiswaServiceV2(this);
        dialog_loading = AlertBoy.loadingMulai(this);
        Navigate.cekLogin(this);



        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        session_guru = sharedpreferences.getString("session", null);
        getData(true);
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
                Kelas k = (Kelas) parent.getItemAtPosition(position);
                cari_kelas = k.getId().toString();
                Toast.makeText(ListUjianSiswaActivity.this, "Selected: " + k.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void cariSiswa(View v) {
        cari_nama = cari_input_nama.getText().toString();
        load_all = false;
        load_more= false;
        getData(false);
    }

    public void loadMore(View v) {
        load_more = true;
        load_all = false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public void loadAll(View v) {
        cari_nama = cari_input_nama.getText().toString();
        load_more = false;
        load_all = true;
        getData(false);

    }

    public boolean listSiswa() {
        if(!load_more) {
            listSiswa.clear();

        }
        String filter_nama = cari_nama;
        String filter_kelas = cari_kelas;
        int offset=listSiswa.size();
        int limit =10;
        if(load_all){
            offset=0;
            limit=0;
        }

        String respons=siswaServiceV2.siswaByKelasAndName(filter_nama, filter_kelas, session_guru, offset,limit);
        if(respons.equals("[]")){
            return false;
        }
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
            Kelas k = Kelas.get_NamakelasFromList(siswa.getKelas(), listKelas);
            siswa.set_Kelas(k);
            listSiswa.add(siswa);
        }
        lengkapiUjianSiswa();
        return true;

    }

    public void lengkapiUjianSiswa(){
        for(Siswa s:listSiswa){
            Ujian u = new Ujian();
            u.setIdsiswa(s.getId());
            String resp_ujian = siswaServiceV2.ujianByIdSiswa(s.getId());
            if (resp_ujian.equals("null")) {

            } else {
                List<String[]> obj = new ArrayList<String[]>();
                for (String x : MyJSON.extractObj(resp_ujian)) {
                    String[] prop = MyJSON.propVal(x);
                    obj.add(prop);
                }
                u = (Ujian) MyJSON.getObj(new Ujian(),obj);
            }
            String respons = siswaServiceV2.soalByIdUjian(u.getId());

            System.out.println(respons);
            List<String> objs = MyJSON.getObjFromArray(respons);
            List<List<String[]>> list_obj = new ArrayList<>();
            List<Soal> listSoal = new ArrayList<>();
            for (String str_obj : objs) {
                List<String[]> obj = new ArrayList<String[]>();
                for (String x : MyJSON.extractObj(str_obj)) {
                    String[] prop = MyJSON.propVal(x);
                    obj.add(prop);
                }
                Soal soal = (Soal) MyJSON.getObj(new Soal(), obj);

                list_obj.add(obj);
                listSoal.add(soal);
            }
            u.set_ListSoal(listSoal);
            s.set_Ujian(u);
        }
    }

    public void listSiswa(View v) {
        cari_nama = "";
        getData(true);
    }

    public void getData(final boolean initkelas) {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        if(initkelas) {
                            cari_kelas = Constant.DEFAULT_KELAS;
                            allKelas();
                        }
                        final boolean responseNotNull = listSiswa();

                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!responseNotNull){
                                                Toast.makeText(ListUjianSiswaActivity.this, "No result", Toast.LENGTH_SHORT).show();
                                            }
                                            populate_listView(listSiswa);
                                            if(initkelas) {
                                                init_spinner_kelas();
                                            }
                                            dialog_loading.dismiss();

                                        }
                                    });
                        }
                    }
                });
        t.start();
    }

    public void populate_listView(List<Siswa> listSiswa) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        list_student.setLayoutManager(layoutManager);
        list_student.setAdapter(new ItemRecyclerAdapter(listSiswa,this));
        list_student.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
//        list_student.setOnItemClickListener(
//                new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                        Siswa siswa = (Siswa) list_student.getItemAtPosition(position);
//                        goToUjian(siswa);
//                    }
//                }
//        );
    }

    @Override
    public void onBackPressed() {
        Navigate.navigate(this, HomeActivity.class);
    }

    public static void goToUjian(Siswa s, Context ctx) {
        Intent i = new Intent(ctx, UjianActivity.class);
        i.putExtra("id_siswa", s.getId());
        Log.i(Constant.tag, "put extra, id" + s.getId());
        ctx.startActivity(i);
    }

    public void allKelas() {
        listKelas.clear();
        String respons = siswaServiceV2.listKelas();

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
