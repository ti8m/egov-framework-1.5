package ch.ti8m.egov.framework.validation.command;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectConverterTest {

    final ObjectConverter objectConverter = new ObjectConverter();
    final static Integer length = 100;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        final Field valueLengthField = ObjectConverter.class.getDeclaredField("valueLength");
        valueLengthField.setAccessible(true);
        valueLengthField.set(objectConverter, ObjectConverterTest.length);
    }

    @Test
    void truncateToLength() {
        final String input = StringUtils.repeat("*", ObjectConverterTest.length + 1000);
        assertThat(input.length()).isGreaterThan(ObjectConverterTest.length);

        final String serializedOutput = objectConverter.convertToDatabaseColumn(input);

        assertThat(serializedOutput.length()).isEqualTo(ObjectConverterTest.length);
    }

    @Test
    void noExceptionIfLengthIsShorterThanMax() {
        final String input = StringUtils.repeat("*", ObjectConverterTest.length - 50);
        assertThat(input.length()).isEqualTo(ObjectConverterTest.length - 50);

        final String serializedOutput = objectConverter.convertToDatabaseColumn(input);

        // 48 instead of 50 as objectmapper wraps strings in quotes
        assertThat(serializedOutput.length()).isEqualTo(ObjectConverterTest.length - 48);
    }

    @Test
    void fileShouldNotBeSerialized() throws IOException {
        final MultipartFile file = new MultipartFile() {
            @Override
            public String getName() {
                return "fileName";
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return "asdf".getBytes();
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream("asdf".getBytes());
            }

            @Override
            public void transferTo(final File dest) throws IOException, IllegalStateException {

            }
        };
        final var dto = FileDto.builder().files(List.of(file)).categoryKey("category").build();
        final var serializedFile = objectConverter.convertToDatabaseColumn(dto);
        assertThat(serializedFile)
                .isNotNull()
                .doesNotContain("bytes")
                .doesNotContain("inputStream")
                .contains("fileName");
    }

    @Test
    void streamingBodyResponseShouldNotBeSerialized() {
        final StreamingResponseBody streamingResponseBody = os -> os.write("test".getBytes());

        final var dto = new StreamingResponseBodyDto("this-gets-serialized", streamingResponseBody);
        final var serializedStream = objectConverter.convertToDatabaseColumn(dto);
        assertThat(serializedStream)
                .isNotNull()
                .doesNotContain("streamingResponseBody")
                .contains("otherContent");
    }

}

record StreamingResponseBodyDto(
        String otherContent,
        StreamingResponseBody streamingResponseBody
) {
}