package ch.ti8m.egov.demo.domain.gescheaft.validation;

import ch.ti8m.egov.framework.validation.command.action.BaseAction;

public enum GescheaftAction implements BaseAction {

    CREATE_GESCHEAFT;

    @Override
    public String getAggregateName() {
        return "Gescheaft";
    }

    @Override
    public String getAction() {
        return GescheaftApplicationService.class.getSimpleName() + "_" + this.name();
    }

}
