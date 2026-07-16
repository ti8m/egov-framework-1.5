package ch.ti8m.egov.framework.persistence.base;

public interface DeepCopyable<T extends Cloneable> {
    T deepCopy();
}
