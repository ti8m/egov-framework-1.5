package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.mdm.api.ApiTestApplication;
import ch.ti8m.egov.mdm.api.shared.FlywayConfiguration;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.UpdateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.UpdateVocabularyDto;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
import ch.ti8m.egov.mdm.persistence.entity.enumerations.NameValidationType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ApiTestApplication.class, FlywayConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("mdm-test")
@Slf4j
class MasterDataControllerPutEndpointTest {

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

    private ObjectMapper mapper;

    private Vocabulary testVocabulary;

    private MasterDataGenericEntity testEntry;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

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
    void updateVocabulary_updatesExistingVocabulary() throws Exception {
        UpdateVocabularyDto updateDto = UpdateVocabularyDto.builder()
                .modifiable(false)
                .sortable(true)
                .nameValidationType(NameValidationType.SHORT_NAME_ONLY)
                .build();

        mockMvc.perform(put(BASE_URL + "/vocabularies/" + testVocabulary.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto))
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<Vocabulary> vocabularies = vocabularyRepository.findByVocabularyCode(testVocabulary.getCode());
        Assertions.assertThat(vocabularies).isNotEmpty().hasSize(1);

        Vocabulary updatedVocabulary = vocabularies.get(0);
        Assertions.assertThat(updatedVocabulary.isModifiable()).isFalse();
        Assertions.assertThat(updatedVocabulary.isSortable()).isTrue();
        Assertions.assertThat(updatedVocabulary.getNameValidationType()).isEqualTo(NameValidationType.SHORT_NAME_ONLY);
    }

    @Test
    void updateCodeForVocabulary_updatesExistingEntry() throws Exception {
        UpdateMasterDataEntryDto updateDto = UpdateMasterDataEntryDto.builder()
                .shortName("e001 updated short name")
                .longName("e001 updated long name")
                .weight(10)
                .validFrom(LocalDateTime.now().minusDays(2))
                .validTo(LocalDateTime.now().plusDays(2))
                .build();

        mockMvc.perform(put(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto))
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<MasterDataGenericEntity> entries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), testEntry.getCode(), LocalDateTime.now());
        Assertions.assertThat(entries).isNotEmpty().hasSize(1);

        MasterDataGenericEntity updatedEntry = entries.get(0);
        Assertions.assertThat(updatedEntry.getShortName()).isEqualTo("e001 updated short name");
        Assertions.assertThat(updatedEntry.getLongName()).isEqualTo("e001 updated long name");
        Assertions.assertThat(updatedEntry.getWeight()).isEqualTo(10);
    }

    @Test
    void updateCodeForVocabulary_withPartialData_updatesOnlyProvidedFields() throws Exception {
        UpdateMasterDataEntryDto updateDto = UpdateMasterDataEntryDto.builder()
                .shortName("e001 partially updated short name")
                .build();

        mockMvc.perform(put(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto))
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk());

        List<MasterDataGenericEntity> entries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), testEntry.getCode(), LocalDateTime.now());
        Assertions.assertThat(entries).isNotEmpty().hasSize(1);

        MasterDataGenericEntity updatedEntry = entries.get(0);
        Assertions.assertThat(updatedEntry.getShortName()).isEqualTo("e001 partially updated short name");
        // Long name sollte unverändert bleiben
        Assertions.assertThat(updatedEntry.getLongName()).isEqualTo("e001 long name");
    }

}
