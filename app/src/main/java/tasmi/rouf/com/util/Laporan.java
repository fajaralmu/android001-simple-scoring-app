package tasmi.rouf.com.util;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ae.java.awt.Desktop;
import tasmi.rouf.com.model.Siswa;
import tasmi.rouf.com.model.Ujian;

public class Laporan {
    public static String cetakLaporanXlsx(String namafile, Context ctx, List<Siswa> listSiswa) throws FileNotFoundException {

        String xNamaFileLaporan = namafile+".xlsx";
        XSSFWorkbook xwb;
        XSSFCellStyle styleUmum;
        XSSFCellStyle styleNamaobat;

        Integer _JmlobatSemuaOrangPerHari = 0;

        // Laporan tiap bulan
        Integer i;

        xwb = new XSSFWorkbook();
        styleNamaobat = xwb.createCellStyle();
        styleUmum = xwb.createCellStyle();
        styleNamaobat.setRotation((short) 90);
        // styleNamaobat.setShrinkToFit(true);
        styleNamaobat.setWrapText(true);
        styleNamaobat.setBorderBottom(BorderStyle.THIN);
        styleNamaobat.setBorderTop(BorderStyle.THIN);
        styleNamaobat.setBorderRight(BorderStyle.THIN);
        styleNamaobat.setBorderLeft(BorderStyle.THIN);
        // styleUmum.setShrinkToFit(true);
        styleUmum.setBorderBottom(BorderStyle.THIN);
        styleUmum.setBorderTop(BorderStyle.THIN);
        styleUmum.setBorderRight(BorderStyle.THIN);
        styleUmum.setBorderLeft(BorderStyle.THIN);
        // <editor-fold defaultstate="collapsed"
        // desc="rekap harian tgl 1 s.d 31">
        System.out.print("*");
        // JUDUL TABEL//
        XSSFSheet xsheet = xwb.createSheet("Laporan Nilai Siswa");
        XSSFRow xbarisJudulTabel = xsheet.createRow(3);
        XSSFCell[] xkolomAtas = new XSSFCell[7];
        xkolomAtas[0] = xbarisJudulTabel.createCell(2);
        xkolomAtas[0].setCellValue("No");
        xkolomAtas[1] = xbarisJudulTabel.createCell(3);
        xkolomAtas[1].setCellValue("Nama");
        xkolomAtas[2] = xbarisJudulTabel.createCell(4);
        xkolomAtas[2].setCellValue("Kelas");
        xkolomAtas[3] = xbarisJudulTabel.createCell(5);
        xkolomAtas[3].setCellValue("Nilai Tahfids");
        xkolomAtas[4] = xbarisJudulTabel.createCell(6);
        xkolomAtas[4].setCellValue("Nilai Tajwid");
        xkolomAtas[5] = xbarisJudulTabel.createCell(7);
        xkolomAtas[5].setCellValue("Keterangan");
        xkolomAtas[6] = xbarisJudulTabel.createCell(8);
        xkolomAtas[6].setCellValue("Catatan");

        for (int c = 0; c < xkolomAtas.length; c++) {
            xkolomAtas[c].setCellStyle(styleUmum);
            //    xsheet.autoSizeColumn(c);
        }

        boolean lanjut = false;
        int kolom = 5;
        // Membuat daftar obat di atas tabel//


        int barisKonten = 3;
        for (Siswa s : listSiswa) {
            barisKonten++;
            System.out.print("*");
            XSSFRow xbarisPerUser = xsheet.createRow(barisKonten);
            XSSFCell[] xkolomRincian = new XSSFCell[7];

            xkolomRincian[0] = xbarisPerUser.createCell(2);
            xkolomRincian[0].setCellValue(barisKonten - 3);

            // nama penerima

            xkolomRincian[1] = xbarisPerUser.createCell(3);
            xkolomRincian[1].setCellValue(s.getNama());
//                xsheet.autoSizeColumn(3);
            xkolomRincian[2] = xbarisPerUser.createCell(4);
            xkolomRincian[2].setCellValue(s.get_Kelas().getNamakelas());
            xkolomRincian[3] = xbarisPerUser.createCell(5);
            xkolomRincian[3].setCellValue(s.get_Ujian().getTotal());
            xkolomRincian[4] = xbarisPerUser.createCell(6);
            xkolomRincian[4].setCellValue(s.get_Ujian().getTajwid());

            xkolomRincian[5] = xbarisPerUser.createCell(7);
            String lulus = s.get_Ujian().getTotal() >= 70 ?"LULUS":"TIDAK LULUS";
            xkolomRincian[5].setCellValue(lulus);
            xkolomRincian[6] = xbarisPerUser.createCell(8);
            xkolomRincian[6].setCellValue(s.get_Ujian().getKeterangan());

        }

        try {
            File newFile = getPublicAlbumStorageDir(xNamaFileLaporan);
            FileOutputStream fo = new FileOutputStream(newFile.getAbsolutePath());
            xwb.write(fo);


            Log.i(Constant.tag, "Report done");

            Uri path = Uri.fromFile(newFile);
            Log.i(Constant.tag, "PATH:"+newFile.getPath()+" file path:"+newFile.getAbsolutePath()+" exist:"+newFile.exists());
            Intent xlsOpen = new Intent(Intent.ACTION_VIEW);

            xlsOpen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            xlsOpen.setDataAndType(path, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            try {
               ctx.getApplicationContext().startActivity(xlsOpen);
            }
            catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    public static File getPublicAlbumStorageDir(String name) {
        //  Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), name);

        try {
            if (!file.createNewFile()) {
                Log.e(Constant.tag, "File not created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
