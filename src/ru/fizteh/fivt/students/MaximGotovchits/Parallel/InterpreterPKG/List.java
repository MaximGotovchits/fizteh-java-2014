package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

public class List {
    void listFunction() {
        if (Interpreter.tableIsChosen) {
            Interpreter.currentTableObject.list();
        } else {
            Interpreter.informToChooseTable();
        }
    }
}
