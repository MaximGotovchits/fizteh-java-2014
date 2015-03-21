package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

public abstract class Change {
    String key;
    String value;

    public abstract void execute(ObjectTable table);
}
