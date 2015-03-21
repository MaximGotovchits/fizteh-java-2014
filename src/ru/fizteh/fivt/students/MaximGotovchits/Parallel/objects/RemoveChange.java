package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

public class RemoveChange extends Change {
    String key;
    public RemoveChange(String keyParam) {
        key = keyParam;
    }

    @Override
    public void execute(ObjectTable table) {
        table.storage.get().remove(key);
    }
}
