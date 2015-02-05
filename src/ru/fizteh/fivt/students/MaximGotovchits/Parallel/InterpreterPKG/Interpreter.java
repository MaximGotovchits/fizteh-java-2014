package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTable;
import java.util.*;

public class Interpreter {
    static Boolean tableIsChosen = false;
    static Integer dirNum = 16;
    static Integer fileNum = 16;
    static String dirExt = ".dir";
    static String fileExt = ".dat";
    static final String INVITATIONAL_SYMBOL = "$ ";
    private static final String SPLIT_BY_COMMA = "\\s*;\\s*";
    private static final String SPLIT_BY_SPACE = "\\s+";
    static String usingTableName;
    static ObjectTable currentTableObject;
    protected static final String DATA_BASE_NAME = System.getProperty("fizteh.db.dir");

    public void parseDataFromCmdLine(String cmdLine) throws Exception {
        String[] splittedLine = cmdLine.split(SPLIT_BY_COMMA);
        for (String line : splittedLine) {
            String[] cmd = line.split(SPLIT_BY_SPACE);
            executionFunction(cmd, true); // True means that data is from command line.
        }
    }

    public static void getCmdFromStream() throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.print(INVITATIONAL_SYMBOL);
        String[] cmd = scan.nextLine().split(SPLIT_BY_SPACE);
        executionFunction(cmd, false); // False means that data is from "System.in".
    }

    static void executionFunction(String[] cmd, boolean fromCmdLine) throws Exception { // The main interpreter
    // function.
        if (cmd[0].equals("create") && cmd.length > 2) {
            new Create().createFunction(cmd, fromCmdLine);
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("use") && cmd.length == 2) {
            String tableName = cmd[1];
            boolean changeUsingTable = false;
            try {
                if (tableIsChosen) {
                    new Use().useFunction(tableName, currentTableObject.getName());
                } else {
                    tableIsChosen = new Use().useFunction(tableName, null);
                }
            } catch (Exception e) {
                printExceptionMessage(e);
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("put") && cmd.length > 2) {
            new Put().putFunction(cmd, fromCmdLine);
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("commit") && cmd.length == 1) {
            new Commit().commitFunction();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("show") && cmd[1].equals("tables") && cmd.length == 2) {
            new ShowTables().showTablesFunction();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("get") && cmd.length == 2) {
            new Get().getFunction(cmd, fromCmdLine);
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("list") && cmd.length == 1) {
            new List().listFunction();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("remove") && cmd.length == 2) {
            new Remove().removeFunction(cmd);
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("drop") && cmd.length == 2) {
            new Drop().dropFunction(cmd);
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("rollback") && cmd.length == 1) {
            new Rollback().rollbackFunction();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("exit") && cmd.length == 1) {
            if (fromCmdLine) {
                currentTableObject.commit();
                new Exit().exitFunction();
            } else {
                if (new Exit().exitAndUseAvailable()) {
                    new Exit().exitFunction();
                }
            }
            ifContinue(fromCmdLine);
        }
        if (cmd.length != 0) {
            syntaxError();
        }
        ifContinue(fromCmdLine);
    }

    static void informToChooseTable() {
        System.err.println("table is not chosen");
    }

    static void ifContinue(boolean fromCmdLine) throws Exception {
        if (fromCmdLine) {
            return;
        } else {
            getCmdFromStream();
        }
    }

    static void printExceptionMessage(Exception e) {
        System.err.println(e);
    }

    static void syntaxError() {
        System.err.println("incorrect syntax");
    }
}
