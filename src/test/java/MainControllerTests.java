import com.rasphat.controller.MainController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

public class MainControllerTests {
    @Test
    public void testIndex_ReturnsCorrectViewName() {
        MainController controller = new MainController();
        String viewName = controller.index();
        assertEquals("index.html", viewName);
    }
}
