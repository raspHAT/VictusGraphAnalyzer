import com.rasphat.zipExtractor.ZipExtractor;
import com.rasphat.zipExtractor.ZipHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

import java.io.IOException;

public class ZipExtractorTest {
    private ZipExtractor extractor;
    private ZipHandler handlerMock;

    @BeforeEach
    public void setUp() {
        handlerMock = mock(ZipHandler.class);
        extractor = new ZipExtractor();
        extractor.addHandler("mockPassword", handlerMock);
    }

    @Test
    public void testExtractZip() throws IOException {
        byte[] testBytes = new byte[] {80, 75, 3, 4, 20, 0, 1, 0, 0, 0, 70, -116, -66, 86, -120, 23, 92, 110, 29, 0, 0, 0, 17, 0, 0, 0, 21, 0, 0, 0, 83, 105, 110, 99, 101, 114, 101, 108, 121, 44, 32, 77, 97, 114, 107, 117, 115, 46, 116, 120, 116, -28, 11, -98, -126, 122, 49, -85, -12, 68, 93, 1, 124, 29, 63, 34, -26, 118, 126, -68, -41, 104, -35, 94, -87, 15, 106, 18, -126, -93, 80, 75, 1, 2, 63, 0, 20, 0, 1, 0, 0, 0, 70, -116, -66, 86, -120, 23, 92, 110, 29, 0, 0, 0, 17, 0, 0, 0, 21, 0, 36, 0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 83, 105, 110, 99, 101, 114, 101, 108, 121, 44, 32, 77, 97, 114, 107, 117, 115, 46, 116, 120, 116, 10, 0, 32, 0, 0, 0, 0, 0, 1, 0, 24, 0, 23, -117, -61, 46, 12, -109, -39, 1, -41, -78, 125, 47, 12, -109, -39, 1, 59, 63, 91, 46, 12, -109, -39, 1, 80, 75, 5, 6, 0, 0, 0, 0, 1, 0, 1, 0, 103, 0, 0, 0, 80, 0, 0, 0, 0, 0};
        String testPassword = "mockPassword";

        extractor.extractZip(testBytes, testPassword);

        verify(handlerMock, times(1)).handleZip(any(), anyString(), any());
    }
}