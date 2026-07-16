package ch.ti8m.egov.framework.validation.engine;

public final class ArrayExpander {

    private ArrayExpander() {
    }

    public static <T> T[] append(final T[] array, final T nextElement) {
        final int length = array.length;
        final T[] newArray = java.util.Arrays.copyOf(array, length + 1);
        newArray[length] = nextElement;
        return newArray;
    }

}
