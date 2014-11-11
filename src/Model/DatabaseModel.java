package Model;

import Util.Log;

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
        Log.debug("DB | CREATE: " + table);
        if (tfIndex_.getFolderName(table) != null) {
            Log.debug("DB | Table(Folder) exists");
            return false;
        } else {
            Log.debug("DB | Try to create table");
            return tfIndex_.insert(table);
        }
    }

    public int insert(String table, String[] seqs) {
        Log.debug("DB | INSERT: " + table);
        for (int i=0; i<seqs.length; i++) {
            Log.debug("ROW: " + seqs[i]);
        }

        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return -1;
        }

        int retId = trIndex.insert(seqs);
        trIndex.close();

        return retId;
    }

    public boolean delete(String table, int id) {
        Log.debug("DB | DELETE: " + table + " " + id);

        TRIndex trIndex = getTRIndexByTable(table);
        boolean ret = trIndex.delete(id);
        trIndex.close();

        return ret;
    }

    public ArrayList<String> retrieve(String table, int id) {
        Log.debug("DB | RETRIEVE: " + table + " " + id);
        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return null;
        }

        ArrayList<String> retSeqs = trIndex.retrieve(id);
        trIndex.close();

        return retSeqs;
    }

    public int retrieveSize(String table, int id) {
        Log.debug("DB | RETRIEVE SIZE: " + table + " " + id);

        TRIndex trIndex = getTRIndexByTable(table);
        if (trIndex == null) {
            return -1;
        }

        int retSize = trIndex.retrieveSize(id);
        trIndex.close();;

        return retSize;
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
        String name = folderName + "_" + TRIndex.DEFAULT_NAME;
        Log.debug("DB | Found TRIndex at " + path + name);
        TRIndex trIndex = new TRIndex(path, name);
        if (trIndex.load()) {
            return trIndex;
        } else {
            return null;
        }
    }
}
