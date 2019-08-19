package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.model.Ujian;
import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.Laporan;
import tasmi.rouf.com.util.MyLoadingDialog;

public class ListNilaiSiswaAcivity extends Activity{//AppCompatActivity {

    private static EditText cari_input_nama;
    private static Spinner cari_kelas_spn;
    private static TableLayout tabel;

    private static MyLoadingDialog dialog_loading;

    private static CheckBox all_kelas, all_nama;


    private String cari_nama = "", cari_kelas = Constant.DEFAULT_KELAS;

    SiswaServiceV2 siswaServiceV2;

    List<Kelas> listKelas = new ArrayList<>();

    List<Siswa> listSiswa = new ArrayList<>();

    private SharedPreferences sharedpreferences;
    private String session_guru = "";
    private boolean load_more = false, load_all = false;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_nilai_siswa_acivity);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        siswaServiceV2 = new SiswaServiceV2(this);

        tabel = findViewById(R.id.tabel);
        cari_kelas_spn = findViewById(R.id.spinner_kelas);
        cari_input_nama = findViewById(R.id.editText_cari_nama);

        dialog_loading = AlertBoy.loadingMulai(this);

//        all_kelas = findViewById(R.id.checkBox_all_kelas);
//        all_nama = findViewById(R.id.checkBox_all_nama);
//
//        all_kelas.setChecked(true);
//        all_nama.setChecked(true);
        Navigate.cekLogin(this);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        session_guru = sharedpreferences.getString("session", null);

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
                        final boolean responseNotNull=listSiswa();
                        lengkapiUjianSiswa();

                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!responseNotNull){
                                                Toast.makeText(ListNilaiSiswaAcivity.this, "No result", Toast.LENGTH_LONG).show();
                                            }
                                            if(initkelas) {
                                                init_spinner_kelas();
                                            }
                                            populateTable(listSiswa);
                                            dialog_loading.dismiss();

                                        }
                                    });
                        }
                    }
                });
        t.start();
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
            s.set_Ujian(u);
        }
    }

    public void populateTable(List<Siswa> listSiswa) {
        tabel.removeAllViews();
        Integer idx = 0;
        for (Siswa s : listSiswa) {
            idx++;
            Ujian u = s.get_Ujian();
            TableRow tr = new TableRow(getApplicationContext());
            TableRow tr_ket = new TableRow(getApplicationContext());

            TextView labelNama = new TextView(getApplicationContext());
            TextView labelNilaiTahfidz = new TextView(getApplicationContext());
            TextView labelNilaiTajwid = new TextView(getApplicationContext());
            TextView labelNo = new TextView(getApplicationContext());
            TextView labelKelas = new TextView(getApplicationContext());
            TextView labelKeterangan = new TextView(getApplicationContext());

            final String nilai_tj = u.getTajwid().toString();
            final String nilai_thf = u.getTotal().toString();
            labelNilaiTahfidz.setText(nilai_thf);
            labelNilaiTajwid.setText(nilai_tj);
            final boolean lulus = u.getTotal() >= 70;

            labelKeterangan.setText(lulus?"LULUS" : "BELUM LULUS");
            labelKelas.setWidth(60);
            labelNo.setWidth(50);
            labelNama.setWidth(100);
            labelNilaiTahfidz.setWidth(60);
            labelNilaiTajwid.setWidth(60);
            labelKeterangan.setWidth(100);

            labelKelas.setTextColor(Color.BLACK);
            labelNo.setTextColor(Color.BLACK);
            labelNama.setTextColor(Color.BLACK);
            labelKelas.setTextColor(Color.BLACK);
            labelNilaiTahfidz.setTextColor(Color.BLACK);
            labelNilaiTajwid.setTextColor(Color.BLACK);
            labelKeterangan.setTextColor(lulus?Color.BLUE:Color.RED);


            labelKelas.setTextSize(12);
            labelNo.setTextSize(12);
            labelNama.setTextSize(12);
            labelKelas.setTextSize(12);
            labelNilaiTahfidz.setTextSize(12);
            labelNilaiTajwid.setTextSize(12);
            labelKeterangan.setTextSize(12);

            labelNilaiTahfidz.setGravity(Gravity.CENTER_HORIZONTAL);
            labelNilaiTajwid.setGravity(Gravity.CENTER_HORIZONTAL);
            labelKeterangan.setGravity(Gravity.CENTER_HORIZONTAL);

            final String kelas = s.get_Kelas().getNamakelas();
            labelKelas.setText(kelas);
            labelNama.setText(s.getNama());
            labelNo.setText(idx.toString());


            tr.setPadding(4, 3, 4, 3);
            tr.addView(labelNo);
            tr.addView(labelNama);
            tr.addView(labelKelas);
            tr.addView(labelNilaiTahfidz);
            tr.addView(labelNilaiTajwid);
            tr.addView(labelKeterangan);

            tr.setBackgroundResource(R.drawable.row_shape);


            ViewGroup.MarginLayoutParams ml_row = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            ml_row.setMargins(5, 5, 5, 5);

            tabel.addView(tr, ml_row);
            tabel.addView(tr_ket, ml_row);
            Log.i(Constant.tag, "add view ");
        }
    }

    public void cariSiswa(View v) {
        listSiswa.clear();
        load_all = false; load_more = false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public void loadAll(View v) {
        listSiswa.clear();
        load_all = true; load_more = false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public void listSiswa(View v) {
        cari_nama = "";
        load_all = false; load_more = false;
        getData(true);

    }

    public void loadMore(View v) {
        load_more = true;
        load_all = false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public boolean listSiswa() {
        if(!load_more) {
            listSiswa.clear();
            load_more = false;
        }
        String filter_nama = cari_nama;
        String filter_kelas = cari_kelas;
        int offset=listSiswa.size();
        int limit =10;
        if(load_all){
            offset=0;
            limit=0;
        }
        String respons=siswaServiceV2.siswaByKelasAndName(filter_nama, filter_kelas, session_guru,offset,limit);
        if(respons == null || respons.equals("[]") || respons.equals("")){
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
        return true;
    }

    @Override
    public void onBackPressed() {
        Navigate.navigate(this, HomeActivity.class);
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

    public void cetak(View v) {
        Long rand = Calendar.getInstance().getTimeInMillis();
        rand = rand * 45 / 100;
        try {
            Laporan.cetakLaporanXlsx("Laporan-Tahfidz-" + rand.intValue(), ListNilaiSiswaAcivity.this, listSiswa);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void init_spinner_kelas() {
        ArrayAdapter<Kelas> adapter = new ArrayAdapter<Kelas>(ListNilaiSiswaAcivity.this,
                R.layout.spinner_item, listKelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cari_kelas_spn.setAdapter(adapter);
        cari_kelas_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelas k = (Kelas) parent.getItemAtPosition(position);
                cari_kelas = k.getId().toString();
                Toast.makeText(ListNilaiSiswaAcivity.this, "Selected: " + k.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
