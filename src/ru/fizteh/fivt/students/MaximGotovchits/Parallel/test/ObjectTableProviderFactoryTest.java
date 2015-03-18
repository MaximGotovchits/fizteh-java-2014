package ru.fizteh.fivt.students.MaximGotovchits.Parallel.test;

import org.junit.Test;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProvider;
import ru.fizteh.fivt.students.MaximGotovchits.Parallel.objects.ObjectTableProviderFactory;
import static org.junit.Assert.assertEquals;

public class ObjectTableProviderFactoryTest {
    @Test
    public void createTest() {
        ObjectTableProvider toCompare = new ObjectTableProvider("TestRoot");
        ObjectTableProvider qq = (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot");
        assertEquals(toCompare, (ObjectTableProvider) new ObjectTableProviderFactory().create("TestRoot"));
    }
}