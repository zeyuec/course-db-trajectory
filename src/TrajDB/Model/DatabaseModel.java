package TrajDB.Model;

import TrajDB.Util.Log;

import java.util.ArrayList;

/**
 * Created by zeyuec on 11/8/14.
 */
public enum DatabaseModel {
    Instance;

    private static TFIndex tfIndex_;

    public boolean load(String path, String name) {
        tfIndex_ = TFIndex.Instance;
        return tfIndex_.load(path, name);
    }

    public boolean create(String table) {
        Log.debug("CREATE: " + table);
        if (tfIndex_.getFolderName(table) != null) {
            Log.debug("Table(Folder) exists");
            return false;
        } else {
            return tfIndex_.insert(table);
        }
    }

    public int insert(String table, String[] seqs) {
        Log.debug("INSERT: " + table);
        for (int i=0; i<seqs.length; i++) {
            Log.debug("ROW: " + seqs[i]);
        }

        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return -1;
        }
        return trIndex.insert(seqs);
    }

    public boolean delete(String table, int id) {
        Log.debug("DELETE: " + table + " " + id);

        TRIndex trIndex = getTRIndexByTable(table);
        return trIndex.delete(id);
    }

    public ArrayList<String> retrieve(String table, int id) {
        Log.debug("RETRIEVE: " + table + " " + id);
        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return null;
        } else {
            return trIndex.retrieve(id);
        }
    }

    public int retrieveSize(String table, int id) {
        Log.debug("RETRIEVE SIZE: " + table + " " + id);
        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return -1;
        } else {
            return trIndex.retrieveSize(id);
        }
    }

    public void close() {
        tfIndex_.save();
    }

    private TRIndex getTRIndexByTable(String table) {
        String folderName = tfIndex_.getFolderName(table);
        // check folder exists;
        if (folderName == null) {
            return null;
        }

        // get TRIndex
        String path = tfIndex_.getPath() + folderName + "/";
        String name = TRIndex.DEFAULT_NAME;
        Log.debug(path + " " + name);
        TRIndex trIndex = new TRIndex(path, name);
        if (trIndex.load()) {
            return trIndex;
        } else {
            return null;
        }
    }
}
