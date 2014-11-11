package Util;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by zeyuec on 11/9/14.
 */
public class FileUtil {

    public static boolean createFolder(String folderPath) {
        try {
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdir();
                return true;
            }
        } catch (Exception e) {
            Log.exception("Error Create Folder");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createFile(String filePath){
        try{
            File myFile = new File(filePath);
            if (!myFile.exists()) {
                myFile.createNewFile();
                return true;
            }
        } catch (Exception e) {
            Log.exception("Error Create File");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean replaceFirstLine(String filePath, String data) {
        try {
            RandomAccessFile raInputFile = new RandomAccessFile(filePath, "rw");
            raInputFile.seek(0);
            String newLine = data + "\n";
            raInputFile.write(newLine.getBytes());

            raInputFile.close();
            return true;
        } catch (Exception e) {
            Log.exception("Replace Exception");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteFile(String filePath){
        try {
            File file = new File(filePath);
            file.delete();
            return true;
        } catch (Exception e) {
            Log.exception("Error Del File");
            e.printStackTrace();
        }
        return false;
    }
}
