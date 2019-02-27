package tasmi.rouf.com.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerAddress {
    Context c;
    public ServerAddress(Context c){
        this.c = c;
    }

    public String Read(){
        try  {
            FileInputStream fi = this.c.openFileInput("server.txt");
            InputStreamReader in = new InputStreamReader(fi);

            BufferedReader bf = new BufferedReader(in);
            StringBuffer sb = new StringBuffer();
            String line;
            while((line=bf.readLine()) !=null){
                sb.append(line);//+"\n");
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "0";
        }catch (IOException e) {
            e.printStackTrace();
            return "0";
        }
    }

    public boolean Save(String addr){
        try {
            FileOutputStream fo = this.c.openFileOutput("server.txt", this.c.MODE_PRIVATE);
            fo.write(addr.getBytes());
            Log.i(Constant.tag,"Updated");
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
