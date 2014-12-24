package ru.fizteh.fivt.students.MaximGotovchits.Parallel.Test;

import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectTableProvider;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.ObjectTableProviderFactory;
import static org.junit.Assert.assertEquals;

public class ObjectTableProviderFactoryTest {
    @Test
    public void createTest() {
        ObjectTableProvider toCompare = new ObjectTableProvider("TestRoot");
        ObjectTableProvider qq = (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot");
        assertEquals(toCompare, (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot"));
    }
}
