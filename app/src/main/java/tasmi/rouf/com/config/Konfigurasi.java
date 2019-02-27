package tasmi.rouf.com.config;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//import com.sun.org.apache.bcel.internal.generic.GOTO;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * @author fajar
 */
public class Konfigurasi {

    Pengaturan pengaturan = new Pengaturan();
    String namafile = "konfigurasi.ini";
    Context c;

    public Konfigurasi(Context c) {
        this.c = c;
        try {
            this.bacaKonfigurasi(this.namafile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public Pengaturan getPengaturan() {
        return this.pengaturan;
    }

    public void setKonfig(String namaKonfig, String nilaiKonfig) throws IOException {
        InputStream in = c.getAssets().open(this.namafile);

        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        String st;
        loop:
        while ((st = br.readLine()) != null) {
            String test = namaKonfig+"="+this.pengaturan.get(namaKonfig);
            if(st.contains(test)){
                st = st.replace(test,namaKonfig+"="+nilaiKonfig );
                lines.add(st);
            }
        }

//        FileWriter fw = new FileWriter(c.getFilesDir());
//        BufferedWriter out = new BufferedWriter(fw);
//        for(String s : lines)
//            out.write(s);
//        out.flush();
//        out.close();
    }

    public void bacaKonfigurasi(String namafile) throws IOException {
        InputStream in = c.getAssets().open(namafile);

        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        pengaturan = new Pengaturan();
        String st;
        loop:
        while ((st = br.readLine()) != null) {
            String propname = "", propvalue = "";
            boolean val = false;
            boolean comment = false;
            for (int i = 0; i < st.length(); i++) {
                if (st.charAt(i) == '/' && st.charAt(i + 1) == '/') {
                    comment = true;
                }
                if (st.charAt(i) != ' ' && !comment) {
                    if (st.charAt(i) == '=') {
                        val = true;
                        continue;
                    }
                    if (!val) {
                        propname += st.charAt(i);
                    } else {
                        propvalue += st.charAt(i);
                    }
                }
            }
            pengaturan.set(propname, propvalue);
            System.out.println(propname + ": " + propvalue);

        }
        // System.out.println(pengaturan.toString());
    }

}

