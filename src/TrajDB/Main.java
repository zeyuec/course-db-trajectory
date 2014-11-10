package TrajDB;

import TrajDB.Model.DatabaseModel;
import TrajDB.Util.Log;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static String DB_PATH = "./data/"; // TODO: get it from user input
    private static String DB_DEFAULT_INDEX = "TFIndex";

    private static DatabaseModel databaseModel_;

    public static void main(String[] args) {
        // init
        Main main = new Main();
        Log.DEBUG = true;
        databaseModel_ = DatabaseModel.Instance;
        if (!databaseModel_.load(DB_PATH, DB_DEFAULT_INDEX)) {
            Log.error("Main | Database loading error");
        }

        main.welcome();

        // read
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(";");
        String line = scanner.next();
        while (!line.trim().toLowerCase().equals("exit")) {
            boolean ret = main.parseCmd(line.trim());
            if (!ret) {
                Log.error("Unknown Command");
            }
            line = scanner.next();
        }
        databaseModel_.close();
        System.out.println("Bye!");
    }

    private boolean parseCmd(String line) {
        String[] lineArgs = line.split(" ");

        // turn all parameters to lower cases
        for (int i=0; i<lineArgs.length; i++) {
            lineArgs[i] = lineArgs[i].toLowerCase();
        }

        // try to match commands
        if (lineArgs.length < 2) {
            return false;
        } else if (lineArgs.length == 2) {
            if (lineArgs[0].equals("create")) {
                // create
                String table = lineArgs[1];
                if (databaseModel_.create(table))  {
                    Log.output("SUC");
                } else {
                    Log.output("Fail");
                }
                return true;
            }
        } else if (lineArgs.length == 5) {
            if (lineArgs[0].equals("insert") &&
                    lineArgs[1].equals("into") &&
                    lineArgs[3].equals("values")
                    ) {
                // insert
                String table = lineArgs[2];
                String[] seqs = lineArgs[4].split("\n");
                for (int i=0; i<seqs.length; i++) {
                    if (seqs[i].split(",").length != 7) {
                        // TODO: could do more check on each fields
                        return false;
                    }
                }
                int retId = databaseModel_.insert(table, seqs);
                Log.output("Id = " + String.valueOf(retId));
                return true;
            } else if (lineArgs[0].equals("delete") &&
                    lineArgs[1].equals("from") &&
                    lineArgs[3].equals("trajectory")
                    ) {
                // delete
                String table = lineArgs[2];
                String id = lineArgs[4];
                if (!isInteger(id)) {
                    return false;
                }
                boolean ret = databaseModel_.delete(table, Integer.parseInt(id));
                if (ret) {
                    Log.output("SUC");
                } else {
                    Log.output("Not Found");
                }
                return true;
            } else if (lineArgs[0].equals("retrieve") &&
                    lineArgs[1].equals("from") &&
                    lineArgs[3].equals("trajectory")
                    ) {
                // retrieve
                String table = lineArgs[2];
                String id = lineArgs[4];
                if (!isInteger(id)) {
                    return false;
                }
                ArrayList<String> ret = databaseModel_.retrieve(table, Integer.parseInt(id));
                if (ret == null) {
                    Log.output("Not Found");
                } else {
                    for (int i = 0; i < ret.size(); i++) {
                        Log.output(ret.get(i));
                    }
                }
                return true;
            } else if (lineArgs[0].equals("retrieve") &&
                    lineArgs[1].equals("from") &&
                    lineArgs[3].equals("where")) {
                // TODO: retrieve where
            }
        } else if (lineArgs.length == 6) {
            if (lineArgs[0].equals("retrieve") &&
                    lineArgs[1].equals("from") &&
                    lineArgs[3].equals("count") &&
                    lineArgs[4].equals("of")
                    ) {
                // retrieve size
                String table = lineArgs[2];
                String id = lineArgs[5];
                if (!isInteger(id)) {
                    return false;
                }
                int size = databaseModel_.retrieveSize(table, Integer.parseInt(id));
                if (size == -1) {
                    Log.output("Not Found");
                } else {
                    Log.output(String.valueOf(size));
                }
                return true;
            }
        }
        return false;
    }

    private void welcome() {
        Log.plain("Welcome to Trajectory Database!");
        Log.plain("Usage: ");
        Log.plain("CREATE <tname>;");
        Log.plain("INSERT INTO <tname> VALUES <sequence>;");
        Log.plain("DELETE FROM <tname> TRAJECTORY <id>;");
        Log.plain("RETRIEVE FROM <tname> TRAJECTORY <id>;");
        Log.plain("RETRIEVE FROM <tname> COUNT OF <id>;");
    }

    public boolean isInteger(String str) {
        str = str.trim();
        try {
            int intOut = Integer.parseInt(str);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

}
