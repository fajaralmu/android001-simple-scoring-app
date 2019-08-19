package tasmi.rouf.com.model;

public class Guru {
    private Integer id;
    private String nama, namapengguna,katasandi;
    private String session="";

    public Guru(){

    }

    public Guru(Integer id, String nama, String namapengguna, String katasandi) {
        this.id = id;
        this.nama = nama;
        this.namapengguna = namapengguna;
        this.katasandi = katasandi;
    }

    public void set_session(String session){
        this.session = session;
    }

    public String get_session(){
        return session;
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

    public String getNamapengguna() {
        return namapengguna;
    }

    public void setNamapengguna(String namapengguna) {
        this.namapengguna = namapengguna;
    }

    public String getKatasandi() {
        return katasandi;
    }

    public void setKatasandi(String katasandi) {
        this.katasandi = katasandi;
    }


    public String toFullString() {
        return "{" +
                "\"id\":" + id +
                ", \"nama\":\"" + nama + '\"' +
                ", \"namapengguna\":\"" + namapengguna + '\"' +
                ", \"katasandi\":\"" + katasandi + "\"}";
    }
}
