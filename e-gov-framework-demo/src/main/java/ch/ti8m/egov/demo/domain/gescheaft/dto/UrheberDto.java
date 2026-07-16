package ch.ti8m.egov.demo.domain.gescheaft.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UrheberDto {
    private long id;
    private String name;
    private String vorname;
}
