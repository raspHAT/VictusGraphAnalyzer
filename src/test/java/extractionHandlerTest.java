// No package due to missing directory depth.
// If a package is required, move the file to a location deeper in the file system.

import com.rasphat.archiveHandler.extractionHandler;
import com.rasphat.archiveHandler.ZipHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

public class extractionHandlerTest {
    private extractionHandler extractionHandler;
    private ZipHandler handlerMock;

    @BeforeEach
    public void setUp() {
        handlerMock = mock(ZipHandler.class);
        extractionHandler = new extractionHandler();
        extractionHandler.addHandler("mockPassword", handlerMock);
    }

    @Test
    public void testExtractZip() throws IOException {
        byte[] testBytes = new byte[] {1, 2, 3, 4 ,5};
        String testPassword = "mockPassword";

        String contentType = null;
        extractionHandler.extractZip(testBytes, testPassword, contentType);
        System.out.println(new char[] {'R', '2', 'D', '2','&', '3', 'C', 'P', 'O', '®'});

        verify(handlerMock, times(1)).archiveHandler(any(File.class), anyString(), any(char[].class));
    }
}