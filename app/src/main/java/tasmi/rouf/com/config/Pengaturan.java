package tasmi.rouf.com.config;

public class Pengaturan {

    private String namaServer;

    public Pengaturan() {

    }

    public Pengaturan(String namaServer) {
        this.namaServer = namaServer;

    }

    public void set(String propname, String propvalue) {
        switch (propname) {

            case "alamat_server":
                setNamaServer(propvalue);
                break;

            default:
                break;
        }
    }


    public String getNamaServer() {
        return namaServer;
    }

    public void setNamaServer(String namaServer) {
        this.namaServer = namaServer;
    }

    public String get(String prop) {
        switch (prop) {
            case "nama_server":
                return getNamaServer();
            default:
                return "";
        }
    }


}

