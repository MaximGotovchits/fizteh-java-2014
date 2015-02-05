package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;

import java.util.LinkedList;
import java.util.List;

public class Create {
    private static final String SPLIT_BY_RIGHT_BRACKET = "\\s*\\)\\s*";
    private static final String SPLIT_BY_LEFT_BRACKET = "\\s*\\(\\s*";
    private static final String SPLIT_BY_SPACE = "\\s+";

    void createFunction(String[] cmd, boolean fromCmdLine) {
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
    }

    private List<Class<?>> getTypeList(String line, boolean fromCmdLine) throws Exception {
        List<Class<?>> typeList = new LinkedList<Class<?>>();
        line = line.replaceAll(SPLIT_BY_RIGHT_BRACKET, "");
        line = line.replaceAll(SPLIT_BY_LEFT_BRACKET, "");
        String[] tmp = line.split(SPLIT_BY_SPACE);
        for (String str : tmp) {
            Class<?> type = getType(str);
            if (type != null) {
                typeList.add(type);
            } else {
                Interpreter.syntaxError();
                if (fromCmdLine) {
                    System.exit(1);
                } else {
                    Interpreter.getCmdFromStream();
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
}
