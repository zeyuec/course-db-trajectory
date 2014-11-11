package Model;

import Util.FileUtil;
import Util.Log;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by zeyuec on 11/9/14.
 */
public class TRIndex {
    public static String DEFAULT_NAME = "TRIndex.index";

    private String path_;
    private String name_;

    private File file_;
    private BufferedReader fileBufferedReader_;

    private int nextValidId_;
    private Hashtable<String, ArrayList<String>> index_;

    public TRIndex(String p, String n) {
        path_ = p;
        name_ = n;
        file_ = null;
        nextValidId_ = 0;
        index_ = new Hashtable<String, ArrayList<String>>();
    }

    public boolean load() {
        file_ = new File(path_+name_);
        if (file_.exists() && !file_.isDirectory()) {
            try {
                FileReader fileReader = new FileReader(path_ + name_);
                fileBufferedReader_ = new BufferedReader(fileReader);

                String line = fileBufferedReader_.readLine();
                nextValidId_ = Integer.valueOf(line);

                fileBufferedReader_.readLine();// skip one line

                return true;
            } catch (Exception e) {
                Log.exception("TRIndex | Load exception " + e.getMessage());
                e.printStackTrace();
            }
        }
        return false;
    }

    public int insert(String[] seqs) {
        Log.debug("TRIndex | Insert");
        Log.debug("TRIndex | Next Valid Id = " + String.valueOf(nextValidId_));
        int newId = nextValidId_ +1;
        String newFilename = String.format("%05d", newId) + Trajectory.DEFAULT_EXTENSION; // use next valid id as the new file name
        String newFilePath = path_ + newFilename;
        if (FileUtil.createFile(newFilePath)) {
            try {
                // append to trFile
                FileWriter indexWriter = new FileWriter(path_ + name_, true);
                indexWriter.write(String.valueOf(newId) + "," + newFilename + "," + seqs.length + "\n");
                indexWriter.flush();
                indexWriter.close();

                // write new file
                FileWriter newTrWriter = new FileWriter(newFilePath, false);
                // TODO: write header
                for (int i=0; i<seqs.length; i++) {
                    newTrWriter.write(seqs[i] + "\n");
                }
                newTrWriter.flush();
                newTrWriter.close();

                nextValidId_++; // increase next valid id

                FileUtil.replaceFirstLine(path_+name_, String.valueOf(nextValidId_));
                return newId;
            } catch (IOException e){
                Log.exception("TRIndex | Insert writing new file exception " + e.getMessage());
                e.printStackTrace();
            }
        }
        return -1;
    }


    public boolean delete(int id) {
        try {
            String data = new String();
            data += String.valueOf(nextValidId_) + "\n";

            String line = fileBufferedReader_.readLine();

            boolean rewrite = false;
            while (line != null) {
                String[] lineArgs = line.split(",");
                if (!lineArgs[0].equals(String.valueOf(id))) {
                    data += line + "\n";
                } else {
                    rewrite = true;
                }
                line = fileBufferedReader_.readLine();
            }

            if (rewrite) {
                // TODO: could write from middle of the file, delete the real file
                FileWriter indexWriter = new FileWriter(path_ + name_, false);
                indexWriter.write(data);
                indexWriter.flush();
                indexWriter.close();
                Log.debug(data);
                return true;
            }


        } catch (Exception e) {
            Log.exception("TRIndex | Delete exception " + e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    public ArrayList<String> retrieve(int id) {
        try {
            String line = fileBufferedReader_.readLine();
            while (line != null) {
                String[] lineArgs = line.split(",");
                if (lineArgs[0].equals(String.valueOf(id))) {
                    String fileName = lineArgs[1];
                    Trajectory trajectory = new Trajectory(path_, fileName);
                    return trajectory.getSequences();
                }
                line = fileBufferedReader_.readLine();
            }
            fileBufferedReader_.close();
        } catch (Exception e) {
            Log.exception("TRIndex | Retrieve exception " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public int retrieveSize(int id) {
        try {
            String line = fileBufferedReader_.readLine();
            while (line != null) {
                String[] lineArgs = line.split(",");
                if (lineArgs[0].equals(String.valueOf(id))) {
                    fileBufferedReader_.close();
                    return Integer.valueOf(lineArgs[2]);
                }
                line = fileBufferedReader_.readLine();
            }
            fileBufferedReader_.close();
        } catch (Exception e) {
            Log.exception("TRIndex | Retrieve size exception " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }

    public void close() {
        try {
            fileBufferedReader_.close();
        } catch (IOException e) {
            Log.exception("TRIndex | Close exception " + e.getMessage());
            e.printStackTrace();
        }
    }

}
