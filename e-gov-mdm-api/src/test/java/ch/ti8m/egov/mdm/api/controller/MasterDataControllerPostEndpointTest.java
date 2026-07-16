package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.mdm.api.ApiTestApplication;
import ch.ti8m.egov.mdm.api.dto.LanguageDefinitionDto;
import ch.ti8m.egov.mdm.api.dto.VocabularyLnDto;
import ch.ti8m.egov.mdm.api.shared.FlywayConfiguration;
import ch.ti8m.egov.mdm.api.direct.MasterDataGenericEntityRepository;
import ch.ti8m.egov.mdm.api.direct.VocabularyRepository;
import ch.ti8m.egov.mdm.api.dto.CreateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.CreateVocabularyDto;
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
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {ApiTestApplication.class, FlywayConfiguration.class})
@AutoConfigureMockMvc
@ActiveProfiles("mdm-test")
@Slf4j
class MasterDataControllerPostEndpointTest {

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
    }

    @AfterEach
    void tearDown() {
        log.debug("Place your break point here to check data after each test");
    }

    @Test
    void createVocabulary_createsNewVocabulary() throws Exception {
        CreateVocabularyDto createDto = CreateVocabularyDto.builder()
                .code("new-vocabulary")
                .modifiable(true)
                .sortable(false)
                .nameValidationType(NameValidationType.LONG_AND_SHORT_NAME)
                .languages(List.of(
                        LanguageDefinitionDto.builder().languageCode("de").build(),
                        LanguageDefinitionDto.builder().languageCode("fr").build(),
                        LanguageDefinitionDto.builder().languageCode("it").build()))
                .localizations(List.of(
                        VocabularyLnDto.builder().languageCode("de").name("neu").description("neues Vokabular").build(),
                        VocabularyLnDto.builder().languageCode("fr").name("nouveau").description("nouveau vocabulaire").build(),
                        VocabularyLnDto.builder().languageCode("it").name("nuovo").description("nuovo vocabolario").build()))
                .build();

        final String result = mockMvc.perform(post(BASE_URL + "/vocabularies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto))
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long vocabularyId = mapper.readValue(result, Long.class);
        Assertions.assertThat(vocabularyId).isNotNull().isPositive();

        List<Vocabulary> vocabularies = vocabularyRepository.findByVocabularyCode("new-vocabulary");
        Assertions.assertThat(vocabularies).isNotEmpty().hasSize(1);
        Assertions.assertThat(vocabularies.get(0).getCode()).isEqualTo("new-vocabulary");
        Assertions.assertThat(vocabularies.get(0).isModifiable()).isTrue();
        Assertions.assertThat(vocabularies.get(0).isSortable()).isFalse();
        Assertions.assertThat(vocabularies.get(0).getNameValidationType()).isEqualTo(NameValidationType.LONG_AND_SHORT_NAME);
    }

    @Test
    void createCodeForVocabulary_createsNewEntry() throws Exception {
        CreateMasterDataEntryDto createDto = CreateMasterDataEntryDto.builder()
                .code("E001")
                .vocabularyCode(testVocabulary.getCode())
                .validFrom(LocalDateTime.now().minusDays(1))
                .validTo(LocalDateTime.now().plusDays(1))
                .shortName("e001 short name")
                .longName("e001 long name")
                .weight(0)
                .languageCode("de")
                .additionalContent(Map.of("key", Map.of("subkey", "subvalue")))
                .build();

        final String result = mockMvc.perform(post(BASE_URL + "/vocabularies/" + testVocabulary.getCode() + "/entries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto))
                        .param(TEST_USER_ID_PARAMETER, TEST_USER_ID_VALUE))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long entryId = mapper.readValue(result, Long.class);
        Assertions.assertThat(entryId).isNotNull().isPositive();

        List<MasterDataGenericEntity> entries = masterDataGenericEntityRepository.findValidByVocabularyCodeAndEntryCode(
                testVocabulary.getCode(), "E001", LocalDateTime.now());
        Assertions.assertThat(entries).isNotEmpty().hasSize(1);

        MasterDataGenericEntity entry = entries.get(0);
        Assertions.assertThat(entry.getCode()).isEqualTo("E001");
        Assertions.assertThat(entry.getVocabularyCode()).isEqualTo(testVocabulary.getCode());
        Assertions.assertThat(entry.getShortName()).isEqualTo("e001 short name");
        Assertions.assertThat(entry.getLongName()).isEqualTo("e001 long name");
        Assertions.assertThat(entry.getValidFrom()).isNotNull();
        Assertions.assertThat(entry.getValidTo()).isNotNull();
    }

}
