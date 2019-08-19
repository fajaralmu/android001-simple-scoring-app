package tasmi.rouf.com.tasmi;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import tasmi.rouf.com.json.MyJSON;
import tasmi.rouf.com.model.Guru;
import tasmi.rouf.com.model.Kelas;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.server.SiswaServiceV2;
import tasmi.rouf.com.util.AlertBoy;
import tasmi.rouf.com.util.Constant;
import tasmi.rouf.com.util.ItemRecyclerAdapter;
import tasmi.rouf.com.util.ItemRecyclerAdapterListSiswa;
import tasmi.rouf.com.util.MyLoadingDialog;

public class ListSiswaActivity extends Activity {
    private static RecyclerView list_student;
    private static TextView listSiswaText;
    private static EditText cari_input_nama;
    private static Spinner cari_kelas_spn;

    private MyLoadingDialog dialog_loading;
    private Dialog dialog_edit_siswa;

    private static TableLayout tabel;

    private static CheckBox all_kelas, all_nama;

    private String cari_nama = "", cari_kelas =Constant.DEFAULT_KELAS;

    List<Kelas> listKelas = new ArrayList<>();

    Integer kelas_terpilih_edit_dialog = 0;

    SiswaServiceV2 siswaServiceV2;
    List<Siswa> listSiswa = new ArrayList<>();

    private String session_guru = "";
    private SharedPreferences sharedpreferences;
    private boolean load_more = false, load_all = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_siswa);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        list_student = findViewById(R.id.recycleViewListSiswa);
        listSiswaText = findViewById(R.id.textViewSiswa);

        cari_kelas_spn = findViewById(R.id.spinner_kelas);
        cari_input_nama = findViewById(R.id.editText_cari_nama);
        dialog_loading = AlertBoy.loadingMulai(this);

        dialog_loading.setCancelable(false);

        siswaServiceV2 = new SiswaServiceV2(this);

        Navigate.cekLogin(this);
        sharedpreferences = getSharedPreferences(Constant.PREF_AKUN, MODE_PRIVATE);
        session_guru = sharedpreferences.getString("session", null);
        getData(true);
    }

    public void init_spinner_kelas() {
        ArrayAdapter<Kelas> adapter = new ArrayAdapter<Kelas>(ListSiswaActivity.this,
                R.layout.spinner_item, listKelas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cari_kelas_spn.setAdapter(adapter);
        cari_kelas_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Kelas k = (Kelas) parent.getItemAtPosition(position);
                cari_kelas = k.getId().toString();
                Toast.makeText(ListSiswaActivity.this, "Selected: " + k.getId(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void showDialogTambahSiswa(View v){
        dialogTambahSiswa();
    }

    public void dialogTambahSiswa() {
        showDetailDialog(new Siswa(), true);
    }

    @Override
    public void onBackPressed() {
        Navigate.navigate(this, HomeActivity.class);
    }

    public void showDetailDialog(Siswa siswa, boolean siswaBaru) {
        dialog_edit_siswa = new Dialog(this);
        dialog_edit_siswa.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_edit_siswa.setContentView(R.layout.dialog_detail_siswa_layout);
        TextView judul = dialog_edit_siswa.findViewById(R.id.textView_judul_dialog_detail_siswa);
        final EditText id_view = dialog_edit_siswa.findViewById(R.id.textView_id_siswa_dialog);
        final EditText nama_view = dialog_edit_siswa.findViewById(R.id.textView_nama_siswa_dialog);
        final EditText lahir_view = dialog_edit_siswa.findViewById(R.id.textView_lahir_siswa_dialog);
        final Spinner kelas_spinner = dialog_edit_siswa.findViewById(R.id.spinner_kelas);

        ImageButton btn_ok = dialog_edit_siswa.findViewById(R.id.button_ok_detail_siswa);
        ImageButton btn_update = dialog_edit_siswa.findViewById(R.id.btn_update);
        ImageButton btn_delete = dialog_edit_siswa.findViewById(R.id.btn_delete_full);

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
                    Method mEdit = null;
                    try {
                        mEdit = siswaServiceV2.getClass().getMethod("editSiswa",Siswa.class, String.class);
                        updateProcess(siswa,mEdit);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
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
                                    if (siswaServiceV2.hapusSiswaFull(id).equals(1)) {
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
                    Method mAdd = null;
                    try {
                        mAdd = siswaServiceV2.getClass().getMethod("tambahSiswa",Siswa.class, String.class);
                        updateProcess(siswa,mAdd);
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_edit_siswa.dismiss();
            }
        });
        dialog_edit_siswa.show();
    }

    public void cariSiswa(View v) {
        load_all = false;
        load_more = false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public void loadMore(View v) {
        load_more = true;
        load_all=false;
        cari_nama = cari_input_nama.getText().toString();
        getData(false);

    }

    public void loadAll(View v) {
        load_more = false;
        load_all=true;
        cari_nama = cari_input_nama.getText().toString();
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
        String respons=siswaServiceV2.siswaByKelasAndName(filter_nama, filter_kelas,session_guru, offset,limit);
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
                            allKelas();
                            cari_kelas = Constant.DEFAULT_KELAS;
                        }
                       final boolean responseNotNull = listSiswa();

                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!responseNotNull){
                                                Toast.makeText(ListSiswaActivity.this, "No result", Toast.LENGTH_SHORT).show();
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

    public void updateProcess(final Siswa siswaToEdit, final Method m) {
        dialog_loading.show();
        Thread t = new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        Object editProcess = null;
                        try {
                            editProcess = m.invoke(siswaServiceV2, siswaToEdit, session_guru);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        if (((Integer) editProcess).equals(1)) {
                            listSiswa();
                        }
                        synchronized (this) {
                            runOnUiThread(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            populate_listView(listSiswa);
                                            dialog_loading.dismiss();
                                            dialog_edit_siswa.dismiss();
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
        list_student.setAdapter(new ItemRecyclerAdapterListSiswa(listSiswa,this));
        list_student.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
