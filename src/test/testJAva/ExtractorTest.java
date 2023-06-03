package testJava;

import com.rasphat.zipExtractor.Extractor;
import com.rasphat.zipExtractor.ZipHandler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ExtractorTest {
    private Extractor extractor;
    private ZipHandler handlerMock;

    @BeforeEach
    public void setUp() {
        handlerMock = mock(ZipHandler.class);
        extractor = new Extractor();
        extractor.addHandler("mockPassword", handlerMock);
    }

    @Test
    public void testExtractZip() throws IOException {
        byte[] testBytes = new byte[] {1, 2, 3, 4 ,5};
        String testPassword = "mockPassword";

        String contentType = null;
        extractor.extractZip(testBytes, testPassword, contentType);
        System.out.println(new char[] {'R', '2', 'D', '2','&', '3', 'C', 'P', 'O'});

        verify(handlerMock, times(1)).handleZip(any(File.class), anyString(), any(char[].class));
    }
}