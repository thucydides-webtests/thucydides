package annotatedstories.apples;

import net.thucydides.core.annotations.Narrative;
import org.junit.Test;

@Narrative(
        title = "Title for test 1",
        text = {"A Narrative for test 1",
                "Multiple lines"}
)
public class Test1 {
    @Test
    public void someTest() {}
}
