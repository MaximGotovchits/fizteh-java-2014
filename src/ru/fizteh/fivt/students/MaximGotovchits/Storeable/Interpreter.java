package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

import java.io.File;
import java.text.ParseException;
import java.util.*;

public class Interpreter {
    final String invitationSymbol = "$ ";
    boolean tableIsChosen = false; // Determines if table is chosen.
    boolean isCommand = false;
    Scanner sc = new Scanner(System.in);
    String tableName;
    ObjectTable currentTable;
    String dataBaseName = System.getProperty("fizteh.db.dir");
    static Map<String, String> storage = new HashMap<String, String>();
    void getInputData() throws Exception {
        System.out.print(invitationSymbol);
        String cmd = sc.nextLine();
        parseInputData(cmd, false);
    }
    void parseInputData(String cmd, boolean fromCmd) throws Exception {
        String[] cmdBuffer = new String[1024];
        if (fromCmd) {
            cmd = cmd.replaceAll("\\s+" + ";" + "\\s+", ";");
            cmd = cmd.replaceAll("\\s+", " ");
            cmd = cmd.replaceAll(";", " ");
            cmd = cmd.replaceAll("\\s+", " ");
            cmdBuffer = cmd.split(" ");
        } else {
            if (cmd.equals("") || cmd.equals("\\s+")) {
                getInputData();
            } else {
                cmdBuffer = cmd.split(" ");
                if (cmdBuffer[0].equals("") && cmdBuffer.length > 0) {
                    for (int ind = 1; ind < cmdBuffer.length; ++ind) {
                        cmd = cmd + cmdBuffer[ind] + " ";
                    }
                    cmdBuffer = cmd.split(" ");
                }
            }
        }
        interpreterFunction(cmdBuffer, fromCmd);
    }
    void interpreterFunction(String[] cmdBuffer, boolean fromCmd) throws Exception {
        int ind = 0;
        while (ind < cmdBuffer.length) {
            isCommand = false;
            if (fromCmd) {
                ifContinue(ind, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("put")) {
                isCommand = true;
                if (tableIsChosen) {
                    ++ind;
                    ifContinue(ind, cmdBuffer, fromCmd, false);
                    String key = cmdBuffer[ind];
                    ++ind;
                    ifContinue(ind, cmdBuffer, fromCmd, false);
                    if (!cmdBuffer[ind].substring(0, 1).equals("[")) {
                        isCommand = false;
                        continue;
                    }
                    int startInd = ind;
                    while (!cmdBuffer[ind].substring(cmdBuffer[ind].length() - 1,
                                                     cmdBuffer[ind].length()).equals("]")) {
                        ++ind;
                        ifContinue(ind, cmdBuffer, fromCmd, false);
                    }
                    int finishInd = ind;
                    String value = new String();
                    for (int i = startInd; i <= finishInd; ++i) {
                        value += cmdBuffer[i] + " ";
                    }
                    value = value.substring(0, value.length() - 1);
                    ObjectStoreable putParam = convertToStoreable(value);
                    new ObjectTable().put(key, putParam);
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("get")) {
                isCommand = true;
                if (tableIsChosen) {
                    if (ind + 1 < cmdBuffer.length) {
                        ++ind;
                        new ObjectTable().get(cmdBuffer[ind]);
                    } else {
                        new ObjectTable().get(null);
                    }
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("remove") && ind + 1 < cmdBuffer.length) {
                isCommand = true;
                ++ind;
                if (tableIsChosen) {
                    new ObjectTable().remove(cmdBuffer[ind]);
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("list")) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().list();
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("exit")) {
                isCommand = true;
                if (tableIsChosen) {
                    if (!fromCmd) {
                        if (new Exit().exitAndUseAvailable()) {
                            new FillTable().fillTableFunction(tableName);
                        } else {
                            ++ind;
                            ifContinue(ind, cmdBuffer, fromCmd, false);
                            continue;
                        }
                    } else {
                        currentTable.commit();
                        new FillTable().fillTableFunction(tableName);
                    }
                    sc.close();
                    new Exit().exitFunction();
                } else {
                    new Exit().exitFunction();
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("create")) {
                isCommand = true;
                ++ind;
                ifContinue(ind, cmdBuffer, fromCmd, false);
                String name = cmdBuffer[ind];
                ++ind;
                ifContinue(ind, cmdBuffer, fromCmd, true);
                if (!cmdBuffer[ind].substring(0, 1).equals("(")) {
                    isCommand = false;
                    System.err.println("incorrect syntax");
                    if (fromCmd) {
                        continue;
                    } else {
                        getInputData();
                    }
                }
                int startInd = ind;
                while (!cmdBuffer[ind].substring(cmdBuffer[ind].length() - 1,
                                                 cmdBuffer[ind].length()).equals(")")) {
                    ++ind;
                    ifContinue(ind, cmdBuffer, fromCmd, false);
                }
                int finishInd = ind;
                String types = new String();
                for (int i = startInd; i <= finishInd; ++i) {
                    types += cmdBuffer[i];
                }
                types.replaceAll("\\s+", "");
                types = types.substring(1, types.length() - 1);
                String[] typeArr = types.split(",");
                List<Class<?>> list = new LinkedList<Class<?>>();
                for (String type : typeArr) {
                    list.add(getType(type));
                }
                new ObjectTableProvider().createTable(name, list);
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("drop")) {
                isCommand = true;
                ++ind;
                ifContinue(ind, cmdBuffer, fromCmd, false);
                if (new File(tableName).getName().equals(cmdBuffer[ind])) {
                    tableName = null;
                    tableIsChosen = false;
                }
                new ObjectTableProvider().removeTable(tableName);
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("use")) {
                isCommand = true;
                ++ind;
                ifContinue(ind, cmdBuffer, fromCmd, false);
                if (new Exit().exitAndUseAvailable()) {
                    if (storage.isEmpty()) {
                        new Use().useFunction(cmdBuffer[ind], tableName);
                        if (new File(dataBaseName + File.separator + cmdBuffer[ind]).exists()) {
                            tableIsChosen = true;
                            tableName = dataBaseName + File.separator + cmdBuffer[ind];
                            currentTable = new ObjectTable(tableName);
                        }
                    } else {
                        System.out.println(storage.size() + " unsaved changes");
                    }
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("show")) {
                ++ind;
                ifContinue(ind, cmdBuffer, fromCmd, false);
                if (cmdBuffer[ind].equals("tables")) {
                    isCommand = true;
                    new ObjectTableProvider().getTableNames();
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("rollback")) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().rollback();
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            if (cmdBuffer[ind].equals("commit")) {
                isCommand = true;
                if (tableIsChosen) {
                    new ObjectTable().commit();
                } else {
                    System.out.println("no table");
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, false);
            }
            ++ind;
            if (!isCommand) {
                if (fromCmd) {
                    System.exit(1);
                }
                ifContinue(ind + 1, cmdBuffer, fromCmd, true);
            }
        }
    }
    boolean isType(String typeName) {
        if (typeName.equals("int") || typeName.equals("long") || typeName.equals("byte") || typeName.equals("float")
            || typeName.equals("double") || typeName.equals("boolean") || typeName.equals("String")) {
            return true;
        }
        return false;
    }
    Class<?> getType(String typeName) {
        if (typeName.equals("int")) {
            return int.class;
        }
        if (typeName.equals("long")) {
            return long.class;
        }
        if (typeName.equals("byte")) {
            return byte.class;
        }
        if (typeName.equals("float")) {
            return float.class;
        }
        if (typeName.equals("double")) {
            return double.class;
        }
        if (typeName.equals("boolean")) {
            return boolean.class;
        }
        if (typeName.equals("String")) {
            return String.class;
        }
        return null;
    }
    ObjectStoreable convertToStoreable(String value) throws ParseException {
        ObjectStoreable storeableToReturn = new ObjectStoreable(value);
        return storeableToReturn;
    }
    void ifContinue(int ind, String[] cmdBuffer, boolean fromCmd, boolean calledByIncorrectSyntax) throws Exception {
        if (ind >= cmdBuffer.length) {
            if (fromCmd) {
                System.err.println("incorrect syntax");
                System.exit(1);
            } else {
                if (calledByIncorrectSyntax) {
                    System.err.println("incorrect syntax");
                    getInputData();
                }
            }
        }
    }
}
