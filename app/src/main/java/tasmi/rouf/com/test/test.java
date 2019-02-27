package tasmi.rouf.com.test;

import tasmi.rouf.com.server.SiswaServer;

public class test {

    public static void main(String[] arg){
        System.out.println("JSON TEST");
        String json = "{\"name\":\"fajar\", \"email\":\"gavawey\"}";
        String json_resp = SiswaServer.executePostJSON("http://localhost/tasmi/index.php/ujian/testJSON",json);
        System.out.println("JSON:"+json_resp);
    }
}
