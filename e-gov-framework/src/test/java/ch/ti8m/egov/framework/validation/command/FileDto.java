package ch.ti8m.egov.framework.validation.command;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record FileDto(
        String categoryKey,
        List<MultipartFile> files
) {
}