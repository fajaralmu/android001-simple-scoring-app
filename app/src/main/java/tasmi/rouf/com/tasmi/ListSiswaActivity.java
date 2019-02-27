package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.util.Constant;

public class ListSiswaActivity extends AppCompatActivity {
    private static ListView list_student;
    private static TextView listSiswaText;
    private static EditText cari_input_nama;
    private static Spinner cari_kelas_spn;

    private static TableLayout tabel;

    private static CheckBox all_kelas, all_nama;

    private String cari_nama="all", cari_kelas="all";

    List<Kelas> listKelas = new ArrayList<>();

    Integer kelas_terpilih_edit_dialog = 0;

    SiswaService siswaService;
    List<Siswa> listSiswa = new ArrayList<>();
    List<Siswa> listSiswaFilter = new ArrayList<>();

    private String kategori_pilih = "nama";
    String[] kategori = {"nama", "kelas"};


    boolean iBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_siswa);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        list_student = findViewById(R.id.list_siswa_view);
        listSiswaText = findViewById(R.id.textViewSiswa);

        cari_kelas_spn = findViewById(R.id.spinner_kelas);
        cari_input_nama = findViewById(R.id.editText_cari_nama);

//        all_kelas = findViewById(R.id.checkBox_all_kelas);
//        all_nama = findViewById(R.id.checkBox_all_nama);
//
//        all_kelas.setChecked(true);
//        all_nama.setChecked(true);

        Intent intent = new Intent(this, SiswaService.class);
        Log.i(Constant.tag, "HELLO");
        Log.i(Constant.tag, "Binding service: " + Boolean.toString(bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)));

        Navigate.cekLogin(this);
    }

    public void init_spinner_kelas() {
        ArrayAdapter<Kelas> adapter = new ArrayAdapter<Kelas>(ListSiswaActivity.this,
                R.layout.spinner_item, listKelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cari_kelas_spn.setAdapter(adapter);
        cari_kelas_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelas k  = (Kelas) parent.getItemAtPosition(position);
                cari_kelas = k.getId().toString();
                Toast.makeText(ListSiswaActivity.this, "Selected: " + k.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void dialogTambahSiswa() {
        showDetailDialog(new Siswa(), true);
    }

    public void goHome(View v){
        Navigate.navigate(this, HomeActivity.class);
    }

    public void showDetailDialog(Siswa siswa, boolean siswaBaru) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_detail_siswa_layout);
        TextView judul = dialog.findViewById(R.id.textView_judul_dialog_detail_siswa);
        final EditText id_view = dialog.findViewById(R.id.textView_id_siswa_dialog);
        final EditText nama_view = dialog.findViewById(R.id.textView_nama_siswa_dialog);
        final EditText lahir_view = dialog.findViewById(R.id.textView_lahir_siswa_dialog);
        final Spinner kelas_spinner = dialog.findViewById(R.id.spinner_kelas);

        ImageButton btn_ok = dialog.findViewById(R.id.button_ok_detail_siswa);
        ImageButton btn_update = dialog.findViewById(R.id.btn_update);
        ImageButton btn_delete = dialog.findViewById(R.id.btn_delete_full);

        kelas_spinner.setSelection(1);
        ArrayAdapter<Kelas> adapter = new ArrayAdapter<Kelas>(ListSiswaActivity.this,
                android.R.layout.simple_spinner_item, listKelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        kelas_spinner.setAdapter(adapter);
        Log.i(Constant.tag, "HERE 2");
        kelas_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelas k = (Kelas) parent.getItemAtPosition(position);
               kelas_terpilih_edit_dialog = k.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!siswaBaru) {
            Kelas k = siswa.get_Kelas();
            int posisiSpinner = adapter.getPosition(k);
            kelas_spinner.setSelection(posisiSpinner);
            id_view.setText(siswa.getId().toString());
            id_view.setEnabled(false);

            nama_view.setText(siswa.getNama());
            lahir_view.setText(siswa.getLahir());

            btn_update.setOnClickListener(new View.OnClickListener() {
                int SUCCESS = 1;

                @Override
                public void onClick(View v) {
                    String nama = nama_view.getText().toString();
                    String lahir = lahir_view.getText().toString();
                    Integer kelas = kelas_terpilih_edit_dialog;
                    Integer id = Integer.parseInt(id_view.getText().toString());

                    Siswa siswa = new Siswa(id, nama, lahir, kelas);
                    if (siswaService.editSiswa(siswa).equals(1)) {
                        Log.i(Constant.tag, "Updated Successfully");
                        listSiswa(v);
                        dialog.dismiss();
                        Toast.makeText(ListSiswaActivity.this, "Updated Successfully", Toast.LENGTH_SHORT);

                    }
                }
            });

            btn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(ListSiswaActivity.this);
                    a_builder.setMessage("Yakin akan menghapus?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Integer id = Integer.parseInt(id_view.getText().toString());
                                    if (siswaService.hapusSiswaFull(id).equals(1)) {
                                        Log.i(Constant.tag, "Deleted Successfully");
                                        listSiswa(v);
                                        Toast.makeText(ListSiswaActivity.this, "Deleted Successfully", Toast.LENGTH_SHORT);
                                    } else {
                                        Toast.makeText(ListSiswaActivity.this, "Unsuccessfull deletion", Toast.LENGTH_SHORT);

                                    }
                                    dialog.dismiss();

                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    ;
                    AlertDialog alert = a_builder.create();
                    alert.setTitle("Hapus Siswa");
                    alert.show();

                }
            });

        } else {
            String judulStr = "Siswa Baru", btnUpdateStr = "Tambah Siswa";
            judul.setText(judulStr);
            btn_delete.setVisibility(View.INVISIBLE);
            //btn_update.setText(btnUpdateStr);
            btn_update.setOnClickListener(new View.OnClickListener() {
                int SUCCESS = 1;

                @Override
                public void onClick(View v) {
                    String nama = nama_view.getText().toString();
                    String lahir = lahir_view.getText().toString();
                    Integer kelas = kelas_terpilih_edit_dialog;

                    Siswa siswa = new Siswa(0, nama, lahir, kelas);
                    if (siswaService.tambahSiswa(siswa).equals(SUCCESS)) {
                        Log.i(Constant.tag, "Added Successfully");
                        listSiswa(v);
                        dialog.dismiss();
                        Toast.makeText(ListSiswaActivity.this, "Added Successfully", Toast.LENGTH_SHORT);
                    }
                }
            });
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void listSiswaUpdate(View v) {
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

    public void allSiswa() {
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
            Kelas k = Kelas.get_NamakelasFromList(siswa.getKelas(),listKelas);
            siswa.set_Kelas(k);
            listSiswa.add(siswa);
        }
    //    populate_listView(listSiswa);
    }

    public void listSiswa(View v) {
        getData();
    }

    public void getData(){
        allKelas();
        allSiswa();
        init_spinner_kelas();
    }

    public void populate_listView(List<Siswa> listSiswa) {
        ArrayAdapter<Siswa> adapter = new ArrayAdapter<Siswa>(this, R.layout.list_siswa_layout, listSiswa);
        list_student.setAdapter(adapter);
        list_student.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Siswa siswa = (Siswa) list_student.getItemAtPosition(position);
                        // Log.i(Constant.tag, siswa.toFullString());
                        showDetailDialog(siswa, false);
                    }
                }
        );
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_option_list_siswa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tambah_siswa:
                Toast.makeText(getApplicationContext(), "Tambah Siswa", Toast.LENGTH_SHORT).show();
                dialogTambahSiswa();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}
