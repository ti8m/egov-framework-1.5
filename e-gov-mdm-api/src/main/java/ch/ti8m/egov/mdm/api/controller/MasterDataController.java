package ch.ti8m.egov.mdm.api.controller;

import ch.ti8m.egov.framework.exceptionhandling.context.DataHolder;
import ch.ti8m.egov.framework.persistence.base.Parameters;
import ch.ti8m.egov.framework.validation.command.Command;
import ch.ti8m.egov.mdm.api.dto.CreateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.CreateVocabularyDto;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntriesDto;
import ch.ti8m.egov.mdm.api.dto.GetMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabulariesDto;
import ch.ti8m.egov.mdm.api.dto.GetVocabularyDto;
import ch.ti8m.egov.mdm.api.dto.UpdateMasterDataEntryDto;
import ch.ti8m.egov.mdm.api.dto.UpdateVocabularyDto;
import ch.ti8m.egov.mdm.api.validation.MasterDataAction;
import ch.ti8m.egov.mdm.api.validation.MasterDataApplicationServiceProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.ENTRY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VALID_AT;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.VOCABULARY_CODE;
import static ch.ti8m.egov.mdm.api.controller.MasterDataParameterNames.WITH_HISTORY;

@RequestMapping(value = "/master-data/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
@Slf4j
public class MasterDataController {

    private final MasterDataCommandFactory masterDataCommandFactory;
    private final MasterDataApplicationServiceProxy masterDataApplicationServiceProxy;

    @GetMapping("/vocabularies")
    public GetVocabulariesDto getVocabularies() {
        log.debug("UserId '{}' requesting vocabulary list", DataHolder.getUserId());
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.GET_VOCABULARIES);

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @GetMapping("/vocabularies/{" + VOCABULARY_CODE + "}")
    public GetVocabularyDto getVocabulary(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode) {

        log.debug("User {} requesting vocabulary {}", DataHolder.getUserId(), vocabularyCode);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.GET_VOCABULARY,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @PostMapping("/vocabularies")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createVocabulary(
            @RequestBody final CreateVocabularyDto createVocabularyDto) {

        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.CREATE_VOCABULARY,
                createVocabularyDto);

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @PutMapping("/vocabularies/{" + VOCABULARY_CODE + "}")
    @ResponseStatus(HttpStatus.OK)
    public Void updateVocabulary(@PathVariable(VOCABULARY_CODE) final String vocabularyCode,
                                 @RequestBody final UpdateVocabularyDto updateVocabularyDto) {

        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.UPDATE_VOCABULARY,
                updateVocabularyDto,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @DeleteMapping("/vocabularies/{" + VOCABULARY_CODE + "}")
    public Void deleteVocabulary(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode) {

        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.DELETE_VOCABULARY,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @GetMapping("/vocabularies/{" + VOCABULARY_CODE + "}/entries")
    public GetMasterDataEntriesDto getEntriesForVocabulary(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @RequestParam(value = VALID_AT, required = false) final LocalDateTime validAt) {

        LocalDateTime validityDate = useValidAtOrDefault(validAt);
        log.debug("User {} requesting entries for vocabulary {} valid at {}", DataHolder.getUserId(), vocabularyCode, validityDate);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.GET_VALID_MASTER_DATA_ENTRIES,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .add(VALID_AT, validityDate)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @GetMapping("/vocabularies/{" + VOCABULARY_CODE + "}/entries/{" + ENTRY_CODE + "}")
    public GetMasterDataEntryDto getValidEntry(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @PathVariable(ENTRY_CODE) final String entryCode,
            @RequestParam(value = VALID_AT, required = false) final LocalDateTime validAt) {

        LocalDateTime validityDate = useValidAtOrDefault(validAt);
        log.debug("User {} requesting details for entry {} in vocabulary {} valid at {}", DataHolder.getUserId(), entryCode, vocabularyCode, validityDate);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.GET_VALID_MASTER_DATA_ENTRY,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .add(ENTRY_CODE, entryCode)
                        .add(VALID_AT, validityDate)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @GetMapping("/vocabularies/{" + VOCABULARY_CODE + "}/entries/{" + ENTRY_CODE + "}/withHistory")
    public GetMasterDataEntriesDto getValidEntriesWithHistory(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @PathVariable(ENTRY_CODE) final String entryCode,
            @RequestParam(value = VALID_AT, required = false) final LocalDateTime validAt) {

        LocalDateTime validityDate = useValidAtOrDefault(validAt);
        log.debug("User {} requesting details for a entry {} in vocabulary {} valid at {} with it's history ",
                DataHolder.getUserId(), entryCode, vocabularyCode, validityDate);
        final Command command = masterDataCommandFactory.getCommand
                (MasterDataAction.GET_VALID_MASTER_DATA_ENTRIES,
                        Parameters.builder()
                                .add(VOCABULARY_CODE, vocabularyCode)
                                .add(ENTRY_CODE, entryCode)
                                .add(VALID_AT, validityDate)
                                .add(WITH_HISTORY, true)
                                .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @PostMapping("vocabularies/{" + VOCABULARY_CODE + "}/entries")
    @ResponseStatus(HttpStatus.CREATED)
    public Long createCodeForVocabulary(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @RequestBody final CreateMasterDataEntryDto createMasterDataEntryDto
    ) {
        log.debug("User {} creating code in vocabulary {}", DataHolder.getUserId(), vocabularyCode);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.CREATE_MASTER_DATA_ENTRY,
                createMasterDataEntryDto);

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @PutMapping("/vocabularies/{" + VOCABULARY_CODE + "}/entries/{" + ENTRY_CODE + "}")
    @ResponseStatus(HttpStatus.OK)
    public Void updateCodeForVocabulary(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @PathVariable(ENTRY_CODE) final String entryCode,
            @RequestBody final UpdateMasterDataEntryDto updateMasterDataEntryDto
    ) {
        log.debug("User {} updating code {} in vocabulary {}", DataHolder.getUserId(), entryCode, vocabularyCode);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.UPDATE_MASTER_DATA_ENTRY,
                updateMasterDataEntryDto,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .add(ENTRY_CODE, entryCode)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    @DeleteMapping("/vocabularies/{" + VOCABULARY_CODE + "}/entries/{" + ENTRY_CODE + "}")
    @ResponseStatus(HttpStatus.OK)
    public Void deleteCode(
            @PathVariable(VOCABULARY_CODE) final String vocabularyCode,
            @PathVariable(ENTRY_CODE) final String entryCode
    ) {
        log.debug("User {} deleting code {} in vocabulary {}", DataHolder.getUserId(), entryCode, vocabularyCode);
        final Command command = masterDataCommandFactory.getCommand(
                MasterDataAction.DELETE_MASTER_DATA_ENTRY,
                Parameters.builder()
                        .add(VOCABULARY_CODE, vocabularyCode)
                        .add(ENTRY_CODE, entryCode)
                        .build());

        return masterDataApplicationServiceProxy.handleCommand(command);
    }

    private static LocalDateTime useValidAtOrDefault(final LocalDateTime validAt) {
        return Objects.requireNonNullElseGet(validAt, LocalDateTime::now);
    }

}
