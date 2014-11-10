package TrajDB.Model;

import TrajDB.Util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Created by zeyuec on 11/9/14.
 */
public class Trajectory {
    private int count_;
    private ArrayList<String> seqs_;
    private String path_, name_;

    public Trajectory(String p, String n) {
        path_ = p;
        name_ = n;
        seqs_ = new ArrayList<String>();
        load();
    }

    public ArrayList<String> getSequences() {
        return seqs_;
    }

    private void load() {
        try {
            Log.debug("Trajectory | " + path_ + "/" + name_);
            FileReader fileReader = new FileReader(path_ + name_);
            BufferedReader br = new BufferedReader(fileReader);

            String line = br.readLine();
            while (line != null) {
                seqs_.add(line);
                line = br.readLine();
            }

            br.close();
        } catch (Exception e) {
            Log.exception("Trajectory | Read Sequences Exception " + e.getMessage());
            e.printStackTrace();
        }
    }

}
