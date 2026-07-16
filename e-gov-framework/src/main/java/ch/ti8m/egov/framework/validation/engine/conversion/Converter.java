package ch.ti8m.egov.framework.validation.engine.conversion;

public abstract class Converter {

    public <T> T execute(final Object input) {
        return (T) convert(input);
    }

    protected abstract Object convert(final Object input);

}
