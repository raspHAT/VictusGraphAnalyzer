import com.rasphat.VictusGraphAnalyzerController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class VictusGraphAnalyzerControllerTests {
    @Test
    public void testIndex_ReturnsCorrectViewName() {
        VictusGraphAnalyzerController controller = new VictusGraphAnalyzerController();
        String viewName = controller.index();
        assertEquals("index.html", viewName);
    }
}
