package ch.ti8m.egov.mdm.abstraction;


import ch.ti8m.egov.mdm.PermissionContext;
import ch.ti8m.egov.mdm.testbase.Ort;
import ch.ti8m.egov.mdm.testbase.OrtRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MasterDataRepositoryTest extends PermissionContext {

    @Autowired
    private OrtRepository ortRepository;

    @AfterEach
    void tearDown() {
        ortRepository.deleteAll();
    }

    @Test
    public void saveMdAndRetrieve() {
        final Ort ortDe = Ort.builder()
                .code("WABERN")
                .languageCode("de")
                .shortName("wabern")
                .longName("wabern bei bern DE")
                .plz("1324")
                .build();
        final Ort ortFr = Ort.builder()
                .code("WABERN")
                .languageCode("fr")
                .shortName("wabern")
                .longName("wabern bei bern FR")
                .plz("1324")
                .build();
        final Ort ortIt = Ort.builder()
                .code("WABERN")
                .languageCode("it")
                .shortName("wabern")
                .longName("wabern bei bern IT")
                .plz("1324")
                .build();

        ortRepository.saveWithTX(ortDe);
        ortRepository.saveWithTX(ortFr);
        ortRepository.saveWithTX(ortIt);

        Assertions.assertWith(ortRepository.findAll(), orts -> {
            Assertions.assertThat(orts).isNotEmpty();
            Assertions.assertThat(orts.size()).isEqualTo(3);
            Assertions.assertThat(orts.get(0).getCode()).isEqualTo("WABERN");
        });
    }

    @Test
    public void findMdByLanguage() {
        final Ort ortDe = Ort.builder()
                .code("WABERN")
                .languageCode("de")
                .shortName("wabern")
                .longName("wabern bei bern DE")
                .plz("1324")
                .build();
        final Ort ortFr = Ort.builder()
                .code("WABERN")
                .languageCode("fr")
                .shortName("wabern")
                .longName("wabern bei bern FR")
                .build();
        final Ort ortIt = Ort.builder()
                .code("WABERN")
                .languageCode("it")
                .shortName("wabern")
                .longName("wabern bei bern IT")
                .build();

        ortRepository.saveWithTX(ortDe);
        ortRepository.saveWithTX(ortFr);
        ortRepository.saveWithTX(ortIt);

        Assertions.assertWith(ortRepository.findForLanguage("fr"), orts -> {
            Assertions.assertThat(orts).isNotEmpty();
            Assertions.assertThat(orts.size()).isEqualTo(1);
            Assertions.assertThat(orts.get(0).getLongName()).isEqualTo("wabern bei bern FR");
        });
    }

}
