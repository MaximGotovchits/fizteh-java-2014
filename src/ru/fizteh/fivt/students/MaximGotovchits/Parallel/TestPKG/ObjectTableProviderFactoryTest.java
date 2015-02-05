package ru.fizteh.fivt.students.MaximGotovchits.Parallel.TestPKG;

import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProvider;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectsPKG.ObjectTableProviderFactory;
import static org.junit.Assert.assertEquals;

public class ObjectTableProviderFactoryTest {
    @Test
    public void createTest() {
        ObjectTableProvider toCompare = new ObjectTableProvider("TestRoot");
        ObjectTableProvider qq = (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot");
        assertEquals(toCompare, (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot"));
    }
}
