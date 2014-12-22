package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import java.util.*;

public class Interpreter extends CommandsTools {
    private final String invitationalSymbol = "$ ";

    void parseDataFromCmdLine(String cmdLine) throws Exception {
        String[] splittedLine = cmdLine.split("\\s*;\\s*");
        for (String line : splittedLine) {
            String[] cmd = line.split("\\s+");
            executionFunction(cmd, true); // True means that data is from command line.
        }
    }

    void getCmdFromStream() throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.print(invitationalSymbol);
        String[] cmd = scan.nextLine().split("\\s+");
        executionFunction(cmd, false); // False means that data is from "System.in".
    }

    private void executionFunction(String[] cmd, boolean fromCmdLine) throws Exception { // The main interpreter
    // function.
        if (cmd[0].equals("create") && cmd.length > 2) {
            String createParameter = new String(); // (...) - type list.
            String tableName = cmd[1];
            for (int ind = 2; ind < cmd.length; ++ind) {
                createParameter += cmd[ind] + " ";
            }
            createParameter = createParameter.substring(0, createParameter.length() - 1);
            List<Class<?>> typeList = new LinkedList<>();
            try {
                typeList = getTypeList(createParameter, fromCmdLine);
                new ObjectTableProvider().createTable(tableName, typeList);
            } catch (Exception e) {
                System.err.println(e);
            }
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
            if (tableIsChosen) {
                String putParameter = new String();
                String key = cmd[1];
                for (int ind = 2; ind < cmd.length; ++ind) {
                    putParameter += cmd[ind] + " ";
                }
                putParameter = putParameter.substring(0, putParameter.length() - 1);
                try {
                    ObjectStoreable value = (ObjectStoreable) new ObjectTableProvider().
                            deserialize(currentTableObject, putParameter);
                    currentTableObject.put(key, value);
                } catch (Exception e) {
                    System.err.println(e);
                }
            } else {
                informToChooseTable();
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("commit") && cmd.length == 1) {
            currentTableObject.commit();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("show") && cmd[1].equals("tables") && cmd.length == 2) {
            new ObjectTableProvider().getTableNames();
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("get") && cmd.length == 2) {
            try {
                if (tableIsChosen) {
                    currentTableObject.get(cmd[1]);
                } else {
                    informToChooseTable();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("list") && cmd.length == 1) {
            if (tableIsChosen) {
                currentTableObject.list();
            } else {
                informToChooseTable();
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("remove") && cmd.length == 2) {
            if (tableIsChosen) {
                try {
                    currentTableObject.remove(cmd[1]);
                } catch (Exception e) {
                    System.err.println(e);
                }
            } else {
                informToChooseTable();
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("drop") && cmd.length == 2) {
            try {
                new ObjectTableProvider().removeTable(cmd[1]);
            } catch (Exception e) {
                System.err.println(e);
            }
            ifContinue(fromCmdLine);
        }

        if (cmd[0].equals("rollback") && cmd.length == 1) {
            currentTableObject.rollback();
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

    void informToChooseTable() {
        System.err.println("table is not chosen");
    }

    void ifContinue(boolean fromCmdLine) throws Exception {
        if (fromCmdLine) {
            return;
        } else {
            getCmdFromStream();
        }
    }

    private void printExceptionMessage(Exception e) {
        System.err.println(e);
    }

    private List<Class<?>> getTypeList(String line, boolean fromCmdLine) throws Exception {
        List<Class<?>> typeList = new LinkedList<Class<?>>();
        line = line.replaceAll("\\s*\\)\\s*", "");
        line = line.replaceAll("\\s*\\(\\s*", "");
        String[] tmp = line.split("\\s+");
        for (String str : tmp) {
            Class<?> type = getType(str);
            if (type != null) {
                typeList.add(type);
            } else {
                syntaxError();
                if (fromCmdLine) {
                    System.exit(1);
                } else {
                    getCmdFromStream();
                }
            }
        }
        return typeList;
    }

    private Class<?> getType(String str) {
        if (str.equals("int")) {
            return int.class;
        }
        if (str.equals("long")) {
            return long.class;
        }
        if (str.equals("double")) {
            return double.class;
        }
        if (str.equals("float")) {
            return float.class;
        }
        if (str.equals("double")) {
            return double.class;
        }
        if (str.equals("boolean")) {
            return boolean.class;
        }
        if (str.equals("byte")) {
            return byte.class;
        }
        if (str.equals("String")) {
            return String.class;
        }
        return null;
    }

    void syntaxError() {
        System.err.println("incorrect syntax");
    }
}
