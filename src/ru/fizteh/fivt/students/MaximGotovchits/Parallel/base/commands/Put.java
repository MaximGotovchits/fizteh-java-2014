package ru.fizteh.fivt.students.MaximGotovchits.Parallel.base.commands;

import ru.fizteh.fivt.students.MaximGotovchits.Parallel.interpreter.Command;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectStoreable;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;

import java.util.Arrays;

public class Put extends Command {
    public boolean execute(String[] cmd) {
        if (CommandTools.amountOfArgumentsIsMoreThan(2, cmd)) {
            if (CommandTools.tableIsChosen) {
                String putParameter;
                String key = cmd[1];
                putParameter = String.join(" " , Arrays.copyOfRange(cmd, 2, cmd.length));
                try {
                    ObjectStoreable value = (ObjectStoreable) new ObjectTableProvider().
                            deserialize(CommandTools.currentTableProvider.getCurrentTableObject(), putParameter);
                    ObjectStoreable temp = (ObjectStoreable) CommandTools.currentTableProvider.getCurrentTableObject()
                            .put(key, value);
                    if (temp == null) {
                        System.out.println("new");
                    } else {
                        System.out.println("overwrite");
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            } else {
                CommandTools.informToChooseTable();
            }
            return true;
        }
        return false;
    }

    @Override
    public String getCmdName() {
        return "put";
    }
}
