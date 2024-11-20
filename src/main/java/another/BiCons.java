package another;

@FunctionalInterface
public interface BiCons<T1, T2> {
    void accept(T1 t1, T2 t2) throws Exception;
}
