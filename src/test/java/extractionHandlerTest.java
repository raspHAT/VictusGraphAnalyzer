// No package due to missing directory depth.
// If a package is required, move the file to a location deeper in the file system.

import com.rasphat.data.legacy.ExtractionHandler;
import com.rasphat.data.legacy.ZipHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

public class extractionHandlerTest {
    private ExtractionHandler extractionHandler;
    private ZipHandler handlerMock;

    @BeforeEach
    public void setUp() {
        handlerMock = mock(ZipHandler.class);
        extractionHandler = new ExtractionHandler();
        extractionHandler.addHandler("mockPassword", handlerMock);
    }

    @Test
    public void testExtractZip() throws IOException {
        byte[] testBytes = new byte[] {'1', 2, 3, 4 ,5};
        String testPassword = "mockPassword";

        extractionHandler.extractZip(testBytes, testPassword, null);
        System.out.println(new char[] {'R', 2, 'D', '2','&', '3', 'C', 'P', 'O', '®'});

        verify(handlerMock, times(1)).archiveHandler(any(File.class), anyString(), any(char[].class));
    }
}