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
        byte[] testBytes = new byte[] {1, 2, 3, 4 ,5};
        String testPassword = "mockPassword";

        extractor.extractZip(testBytes, testPassword);

        verify(handlerMock, times(1)).handleZip(any(), anyString(), any());
    }
}