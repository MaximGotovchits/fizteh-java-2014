package ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class ObjectTableProviderFactory implements TableProviderFactory {
    private static final int LONGEST_NAME = 260;
    String dirName;

    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        dirName = dir;
        if (dir == null || dir.length() > LONGEST_NAME) {
            throw new IllegalArgumentException();
        }
        return new ObjectTableProvider(dir);
    }
}