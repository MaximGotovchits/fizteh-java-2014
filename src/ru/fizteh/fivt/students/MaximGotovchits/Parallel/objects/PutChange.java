package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

public class PutChange extends Change {
    String key;
    ObjectStoreable value;

    public PutChange(String keyParam, ObjectStoreable valueParam) {
        key = keyParam;
        value = valueParam;
    }

    @Override
    public void execute(ObjectTable table) {
        table.storage.get().put(key, value);
    }
}
