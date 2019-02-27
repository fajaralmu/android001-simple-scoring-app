package tasmi.rouf.com.model;

import java.util.List;

public class Siswa {

    private Integer id;
    private String nama;
    private String lahir;
    private Integer kelas;
    private Ujian u;
    private Kelas kelasClass;

    public Siswa(){

    }



    public Siswa(Integer id, String nama, String lahir, Integer kelas) {
        super();
        this.id = id;
        this.nama = nama;
        this.lahir = lahir;
        this.kelas = kelas;
    }



    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getLahir() {
        return lahir;
    }
    public void setLahir(String lahir) {
        this.lahir = lahir;
    }
    public Integer getKelas() {
        return kelas;
    }
    public void setKelas(Integer kelas) {
        this.kelas = kelas;
    }

    public void set_Kelas(Kelas k){
        this.kelasClass = k;
    }

    public Kelas get_Kelas(){
        return this.kelasClass;
    }

    public Ujian get_Ujian() {
        return u;
    }

    public void set_Ujian(Ujian u) {
        this.u = u;
    }

    public String toFullString() {
        return "{\"id\":" + id + ",\"nama\":\"" + nama + "\",\"lahir\":\"" + lahir
                + "\",\"kelas\":" + kelas + "}";
    }

    @Override
    public String toString() {
        return nama;
    }


}
