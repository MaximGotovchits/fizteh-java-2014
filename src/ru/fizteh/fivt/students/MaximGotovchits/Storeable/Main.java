package ru.fizteh.fivt.students.MaximGotovchits.Storeable;

public class Main { // Using JSON format.
    public static void main(final String[] args) throws Exception {
        new MakeDirs().makeDirsFunction();
        if (args.length == 0) {
            new Interpreter().getInputData();
        } else {
            String cmd = new String();
            for (String arg : args) {
                cmd += arg + " ";
            }
            new Interpreter().parseInputData(cmd.substring(0, cmd.length() - 1), true); // Откусить " ".
            new Exit().exitFunction();
        }
    }
}
