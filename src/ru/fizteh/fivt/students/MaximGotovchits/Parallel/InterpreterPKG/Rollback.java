package ru.fizteh.fivt.students.MaximGotovchits.Parallel.InterpreterPKG;

public class Rollback {
    void rollbackFunction() {
        Interpreter.currentTableObject.rollback();
    }
}
