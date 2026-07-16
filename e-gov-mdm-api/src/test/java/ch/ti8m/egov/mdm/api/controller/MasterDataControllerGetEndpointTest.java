package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.mdm.api.ApiTestApplication;
import ch.ti8m.egov.mdm.api.shared.FlywayConfiguration;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntriesDto;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabulariesDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.persistence.entity.MasterDataGenericEntity;
import ch.ti8m.egov.mdm.persistence.entity.Vocabulary;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VALID_AT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ApiTestApplication.class, FlywayConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("mdm-test")
@Slf4j
class MasterDataControllerGetEndpointTest {

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

    private List<MasterDataGenericEntity> testEntries;

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
                .build();
        masterDataGenericEntityRepository.save(entry);

        entry = MasterDataGenericEntity.builder()
                .vocabularyCode(testVocabulary.getCode())
                .code("E002")
                .validFrom(LocalDateTime.now().minusDays(2))
                .validTo(LocalDateTime.now().minusDays(1))
                .shortName("e002 historic short name")
                .longName("e002 historic long name")
                .build();
        masterDataGenericEntityRepository.save(entry);

        entry = MasterDataGenericEntity.builder()
                .vocabularyCode(testVocabulary.getCode())
                .code("E002")
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(1))
                .shortName("e002 actual short name")
                .longName("e002 actual long name")
                .build();
        masterDataGenericEntityRepository.save(entry);
    }

    @AfterEach
    void tearDown() {
        log.debug("Place your break point here to check data after each test");
    }

    @Test
    void getVocabularies_returnsListWithTestVocabularies() throws Exception {
        final String result = mockMvc.perform(get(
                        BASE_URL + "/vocabularies"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        GetVocabulariesDto resultObject = mapper.readValue(result, GetVocabulariesDto.class);
        Assertions.assertThat(resultObject.getVocabularies()).isNotEmpty().hasSize(1);
    }

    @Test
    void getVocabulary_returnsSingleVocabulary() throws Exception {
        final String result = mockMvc.perform(get(BASE_URL + "/vocabularies/" + testVocabulary.getCode()))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        GetVocabularyDto resultObject = mapper.readValue(result, GetVocabularyDto.class);
        Assertions.assertThat(resultObject.getCode()).isNotNull().isEqualTo("test-vocabulary");
    }

    @Test
    void getEntriesForVocabulary_returnsAllValidEntriesForVocabulary() throws Exception {
        LocalDateTime validAt = LocalDateTime.now();
        testEntries = masterDataGenericEntityRepository.findValidByVocabularyCode(
                testVocabulary.getCode(), LocalDateTime.now());
        final String result = mockMvc.perform(get(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries").param(VALID_AT, validAt.toString()).param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andReturn().getResponse().getContentAsString();
        GetMasterDataEntriesDto resultObject = mapper.readValue(result, GetMasterDataEntriesDto.class);
        Assertions.assertThat(resultObject.getEntries()).isNotNull().hasSize(testEntries.size());
    }

    @Test
    void getValidEntry_returnsSingleEntry() throws Exception {
        LocalDateTime validAt = LocalDateTime.now();
        testEntry = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), "E002", LocalDateTime.now()).get(0);
        final String result = mockMvc.perform(get(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode()).param(VALID_AT, validAt.toString()).param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        GetMasterDataEntryDto resultObject = mapper.readValue(result, GetMasterDataEntryDto.class);
        Assertions.assertThat(resultObject).isNotNull();
        Assertions.assertThat(resultObject.getCode()).isNotNull().isEqualTo("E002");
        Assertions.assertThat(resultObject.getShortName()).isNotNull().isEqualTo("e002 actual short name");
        Assertions.assertThat(resultObject.getLongName()).isNotNull().isEqualTo("e002 actual long name");
    }

    @Test
    void getValidEntriesWithHistory_returnsSingleEntryIncludingHistory() throws Exception {
        LocalDateTime validAt = LocalDateTime.now();
        testEntry = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), "E002", LocalDateTime.now()).get(0);
        testEntries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCodeWithHistory(
                testVocabulary.getCode(), "E002");
        final String result = mockMvc.perform(get(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries/" + testEntry.getCode() + "/withHistory").param(VALID_AT, validAt.toString()).param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        GetMasterDataEntriesDto resultObject = mapper.readValue(result, GetMasterDataEntriesDto.class);
        Assertions.assertThat(resultObject.getEntries()).isNotNull().hasSize(testEntries.size());

        GetMasterDataEntryDto one = resultObject.getEntries().get(0);
        Assertions.assertThat(one.getCode()).isNotNull().isEqualTo("E002");
        Assertions.assertThat(one.getShortName()).isNotNull().isEqualTo("e002 actual short name");
        Assertions.assertThat(one.getLongName()).isNotNull().isEqualTo("e002 actual long name");

        GetMasterDataEntryDto two = resultObject.getEntries().get(1);
        Assertions.assertThat(two.getCode()).isNotNull().isEqualTo("E002");
        Assertions.assertThat(two.getShortName()).isNotNull().isEqualTo("e002 historic short name");
        Assertions.assertThat(two.getLongName()).isNotNull().isEqualTo("e002 historic long name");
    }

}
