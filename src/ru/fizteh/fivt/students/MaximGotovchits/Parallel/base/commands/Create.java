package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Create extends Command {
    private static final String SPLIT_BY_RIGHT_BRACKET = "\\s*\\)\\s*";
    private static final String SPLIT_BY_LEFT_BRACKET = "\\s*\\(\\s*";
    private static final String SPLIT_BY_SPACE = "\\s+";

    @Override
    public String getCmdName() {
        return "create";
    }

    @Override
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIsMoreThan(2, cmd)) {
            String createParameter; // (...) - type list.
            String tableName = cmd[1];
            createParameter = String.join(" ", Arrays.copyOfRange(cmd, 2, cmd.length));
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

    public List<Class<?>> getTypeList(String line) {
        ObjectTable temp = new ObjectTable();
        List<Class<?>> typeList = new LinkedList<>();
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
