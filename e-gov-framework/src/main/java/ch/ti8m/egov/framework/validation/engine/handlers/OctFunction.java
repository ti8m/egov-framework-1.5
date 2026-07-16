package ch.ti8m.egov.framework.validation.engine.handlers;

@FunctionalInterface
public interface OctFunction<A, B, C, D, E, F, G, H, I> {

    I apply(A a, B b, C c, D d, E e, F f, G g, H h);

}
