package ch.ti8m.egov.mdm.api.validation;

import ch.ti8m.egov.framework.validation.command.action.BaseAction;

public enum MasterDataAction implements BaseAction {

    CREATE_VOCABULARY,
    UPDATE_VOCABULARY,
    DELETE_VOCABULARY,
    GET_VOCABULARY,
    GET_VOCABULARIES,
    CREATE_MASTER_DATA_ENTRY,
    UPDATE_MASTER_DATA_ENTRY,
    DELETE_MASTER_DATA_ENTRY,
    GET_VALID_MASTER_DATA_ENTRY,
    GET_VALID_MASTER_DATA_ENTRY_WITH_HISTORY,
    GET_VALID_MASTER_DATA_ENTRIES;

    @Override
    public String getAggregateName() {
        return "Vocabularies";
    }

    @Override
    public String getAction() {
        return MasterDataApplicationService.class.getSimpleName() + "_" + this.name();
    }

}
