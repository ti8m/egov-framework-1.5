package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.mdm.api.ApiTestApplication;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.shared.FlywayConfiguration;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ApiTestApplication.class, FlywayConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("mdm-test")
@Slf4j
class MasterDataControllerDeleteEndpointTest {

    /* This is only for WebMvc testing to ensure User init in DemoUserIdProviderImpl. */
    public static final String TEST_USER_ID_PARAMETER = "userId";
    /* This is only for WebMvc testing to ensure User init in DemoUserIdProviderImpl. */
    public static final String TEST_USER_ID_VALUE = "42";

    private static final String BASE_URL = "/master-data/v1";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VocabularyRepository vocabularyRepository;

    @Autowired
    private MasterDataGenericEntityRepository masterDataGenericEntityRepository;

    private Vocabulary testVocabulary;

    private MasterDataGenericEntity testEntry;

    @BeforeEach
    void setUp() {
        vocabularyRepository.deactivatePermissionsGlobally();
        masterDataGenericEntityRepository.deactivatePermissionsGlobally();

        // Vorhandene Daten löschen
        masterDataGenericEntityRepository.deleteAll();
        vocabularyRepository.deleteAll();

        // Test-Vocabulary anlegen
        Vocabulary vocabulary = Vocabulary.builder()
                .code("test-vocabulary")
                .modifiable(true)
                .sortable(false)
                .nameValidationType(NameValidationType.LONG_AND_SHORT_NAME)
                .build();
        vocabularyRepository.save(vocabulary);
        testVocabulary = vocabularyRepository.findByVocabularyCode("test-vocabulary").get(0);

        // Test-Eintrag für das Vocabulary anlegen
        MasterDataGenericEntity entry = MasterDataGenericEntity.builder()
                .vocabularyCode(testVocabulary.getCode())
                .code("E001")
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(1))
                .shortName("e001 short name")
                .longName("e001 long name")
                .weight(0)
                .build();
        masterDataGenericEntityRepository.save(entry);
        testEntry = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), "E001", LocalDateTime.now()).get(0);
    }

    @AfterEach
    void tearDown() {
        log.debug("Place your break point here to check data after each test");
    }

    @Test
    void deleteVocabulary_deletesExistingVocabulary() throws Exception {
        // Erst den Entry löschen, da sonst Foreign Key Constraint verletzt wird
        masterDataGenericEntityRepository.delete(testEntry);

        mockMvc.perform(delete(BASE_URL + "/vocabularies/" + testVocabulary.getCode())
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<Vocabulary> vocabularies = vocabularyRepository.findByVocabularyCode(testVocabulary.getCode());
        Assertions.assertThat(vocabularies).isEmpty();
    }

    @Test
    void deleteCode_deletesExistingEntry() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode())
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<MasterDataGenericEntity> entries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), testEntry.getCode(), LocalDateTime.now());
        Assertions.assertThat(entries).isEmpty();
    }

    @Test
    void deleteCode_withMultipleEntries_deletesOnlySpecifiedEntry() throws Exception {
        // Zweiten Eintrag anlegen
        MasterDataGenericEntity secondEntry = MasterDataGenericEntity.builder()
                .vocabularyCode(testVocabulary.getCode())
                .code("E002")
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(1))
                .shortName("e002 short name")
                .longName("e002 long name")
                .weight(0)
                .build();
        masterDataGenericEntityRepository.save(secondEntry);

        mockMvc.perform(delete(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode())
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<MasterDataGenericEntity> deletedEntries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), testEntry.getCode(), LocalDateTime.now());
        Assertions.assertThat(deletedEntries).isEmpty();

        List<MasterDataGenericEntity> remainingEntries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), "E002", LocalDateTime.now());
        Assertions.assertThat(remainingEntries).isNotEmpty().hasSize(1);
        Assertions.assertThat(remainingEntries.get(0).getCode()).isEqualTo("E002");
    }

}
