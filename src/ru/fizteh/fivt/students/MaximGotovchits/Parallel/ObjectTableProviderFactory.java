package ru.fizteh.fivt.students.MaximGotovchits.Parallel;

import ru.fizteh.fivt.storage.structured.TableProvider;
import ru.fizteh.fivt.storage.structured.TableProviderFactory;

public class ObjectTableProviderFactory extends CommandsTools implements TableProviderFactory {
    String dirName;
    @Override
    public TableProvider create(String dir) throws IllegalArgumentException {
        dirName = dir;
        try {
            if (dir == null || dir.length() > longestName) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException s) {
            System.err.println(s);
            return null;
        }
        return new ObjectTableProvider(dir);
    }
}
