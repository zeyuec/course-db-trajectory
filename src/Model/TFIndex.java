package Model;

import Util.FileUtil;
import Util.Log;

import java.io.*;
import java.util.Hashtable;
import java.util.Set;

/**
 * Created by zeyuec on 11/9/14.
 */
public enum TFIndex {
    Instance;

    private String path_, name_;
    private int nextValidId_;
    private Hashtable<String, Integer> index_; // [tableName, id]

    public String getPath() {
        return path_;
    }

    public String getName() {
        return name_;
    }

    public boolean load(String p, String n) {
        Log.debug("TFIndex | Loading Data...");
        path_ = p;
        name_ = n;
        File f = new File(path_+name_);
        if (f.exists() && !f.isDirectory()) {
            try {
                FileReader fileReader = new FileReader(path_ + name_);
                BufferedReader br = new BufferedReader(fileReader);
                index_ = new Hashtable<String, Integer>();

                String line = br.readLine();

                // read max id
                nextValidId_ = Integer.valueOf(line);
                line = br.readLine();

                // read index
                Log.debug("TFIndex | Table ID - NAME: ");
                while (line != null) {
                    String lineArgs[] = line.split(",");
                    Log.debug(lineArgs[0] + " " + lineArgs[1]);
                    index_.put(lineArgs[1], Integer.valueOf(lineArgs[0]));
                    line = br.readLine();
                }
                Log.debug("TFIndex | Done");
            } catch (Exception e) {
                Log.exception("TFIndex | Load exception " + e.getMessage());
                e.printStackTrace();
            }

            return true;
        } else {
            return false;
        }
    }

    public void save() {
        File file = new File(path_+name_);
        try {
            FileWriter writer = new FileWriter(file, false); // true to append, false to rewrite

            // write max id
            writer.write(String.valueOf(nextValidId_)+"\n");

            // write indexes
            Set<String> keys = index_.keySet();
            for(String key: keys){
                writer.write(index_.get(key)+","+ key+"\n");
            }
            writer.close();
        } catch (IOException e) {
            Log.exception("TFIndex | Save exception " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean insert(String tableName) {
        if (index_.get(tableName) != null) {
            return false;
        } else {
            String folderName = String.format("%05d", nextValidId_); //use nextValidId as the folder name
            if (FileUtil.createFolder(path_ + folderName)) {
                Log.debug("TFIndex | Try to create folder " + path_+folderName);
                String newTRFileName = path_ + folderName + "/" + folderName + "_" + TRIndex.DEFAULT_NAME;
                if (FileUtil.createFile(newTRFileName)) {
                    index_.put(tableName, nextValidId_);
                    try {
                        Log.debug("TFIndex | Try to create TRIndex in the folder");
                        FileWriter newTRFileWriter = new FileWriter(newTRFileName, false);
                        newTRFileWriter.write("0\n");
                        newTRFileWriter.flush();
                        newTRFileWriter.close();
                        nextValidId_++; // increase nextValidId
                        return true;
                    } catch (Exception e) {
                        Log.debug("TFIndex | Insert Fail Exception " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public String getFolderName(String tableName) {
        Integer tableId = index_.get(tableName);
        if (tableId == null) {
            return null;
        }
        return String.format("%05d", tableId);
    }




}
