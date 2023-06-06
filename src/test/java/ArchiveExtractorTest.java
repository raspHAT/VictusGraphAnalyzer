// No package due to missing directory depth.
// If a package is required, move the file to a location deeper in the file system.

import com.rasphat.archiveExtractor.ArchiveExtractor;
import com.rasphat.archiveExtractor.ZipHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

public class ArchiveExtractorTest {
    private ArchiveExtractor archiveExtractor;
    private ZipHandler handlerMock;

    @BeforeEach
    public void setUp() {
        handlerMock = mock(ZipHandler.class);
        archiveExtractor = new ArchiveExtractor();
        archiveExtractor.addHandler("mockPassword", handlerMock);
    }

    @Test
    public void testExtractZip() throws IOException {
        byte[] testBytes = new byte[] {1, 2, 3, 4 ,5};
        String testPassword = "mockPassword";

        String contentType = null;
        archiveExtractor.extractZip(testBytes, testPassword, contentType);
        System.out.println(new char[] {'R', '2', 'D', '2','&', '3', 'C', 'P', 'O', '®'});

        verify(handlerMock, times(1)).handleZip(any(File.class), anyString(), any(char[].class));
    }
}