package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

//import static java.util.Arrays.copyOfRange;

public class Create extends Command {
    private static final String SPLIT_BY_RIGHT_BRACKET = "\\s*\\)\\s*";
    private static final String SPLIT_BY_LEFT_BRACKET = "\\s*\\(\\s*";
    private static final String SPLIT_BY_SPACE = "\\s+";

    @Override
    public String getCmdName() {
        return "create";
    }

    @Override
    public boolean execute(String[] cmd) throws Exception {
        if (cmd.length > 2) {
            String createParameter = new String(); // (...) - type list.
            String tableName = cmd[1];
            for (int ind = 2; ind < cmd.length; ++ind) {
                createParameter += cmd[ind] + " ";
            }
            createParameter = createParameter.substring(0, createParameter.length() - 1);
            List<Class<?>> typeList;
            try {
                typeList = getTypeList(createParameter);
                if (typeList != null) {
                    if (new ObjectTableProvider().createTable(tableName, typeList) != null) {
                        System.out.println("created");
                    } else {
                        System.out.println(tableName + " exists");
                    }
                } else {
                    return false;
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            return true;
        }
        return false;
    }

    public List<Class<?>> getTypeList(String line) throws Exception {
        ObjectTable temp = new ObjectTable();
        List<Class<?>> typeList = new LinkedList<Class<?>>();
        line = line.replaceAll(SPLIT_BY_RIGHT_BRACKET, "");
        line = line.replaceAll(SPLIT_BY_LEFT_BRACKET, "");
        String[] tmp = line.split(SPLIT_BY_SPACE);
        for (String str : tmp) {
            Class<?> type = temp.getType(str);
            if (type != null) {
                typeList.add(type);
            } else {
                return null;
            }
        }
        return typeList;
    }
}
