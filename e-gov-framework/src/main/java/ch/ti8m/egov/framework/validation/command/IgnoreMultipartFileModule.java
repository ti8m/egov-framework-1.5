package ch.ti8m.egov.framework.validation.command;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class IgnoreMultipartFileModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public IgnoreMultipartFileModule() {
        super("IgnoreMultipartFileModule");
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addBeanSerializerModifier(new IgnoreMultipartFileSerializerModifier());
    }
}