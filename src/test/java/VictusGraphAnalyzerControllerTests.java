import com.rasphat.VictusGraphAnalyzerController;
import org.junit.Assert;
import org.junit.Test;

public class VictusGraphAnalyzerControllerTests {
    @Test
    public void testIndex_ReturnsCorrectViewName() {
        VictusGraphAnalyzerController controller = new VictusGraphAnalyzerController();
        String viewName = controller.index();
        Assert.assertEquals("index.html", viewName);
    }
}
