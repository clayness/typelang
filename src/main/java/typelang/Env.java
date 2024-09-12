package typelang;

/**
 * Representation of an environment, which maps variables to values.
 *
 * @author hridesh
 */
public interface Env<T> {
    T get(String search_var);

    default Env<T> extend(String var, T val) {
        return new ExtendEnv<>(this, var, val);
    }

    boolean isEmpty();

    class LookupException extends RuntimeException {
        LookupException(String message) {
            super(message);
        }
    }

    @SuppressWarnings("unused")
    class EmptyEnv<T> implements Env<T> {

        @Override
        public T get(String search_var) {
            throw new LookupException("No binding found for name: " + search_var);
        }

        @Override
        public boolean isEmpty() {
            return true;
        }
    }

    class ExtendEnv<T> implements Env<T> {
        private final Env<T> _saved_env;
        private final String _var;
        private final T _val;

        private ExtendEnv(Env<T> saved_env, String var, T val) {
            _saved_env = saved_env;
            _var = var;
            _val = val;
        }

        public static <T> Env<T> append(Env<T> fst, Env<T> snd) {
            if (fst.isEmpty()) {
                return snd;
            } else {
                ExtendEnv<T> f = (ExtendEnv<T>) fst;
                return new ExtendEnv<>(ExtendEnv.append(f._saved_env, snd), f._var, f._val);
            }
        }

        @Override
        public synchronized T get(String search_var) {
            if (search_var.equals(_var)) {
                return _val;
            }
            return _saved_env.get(search_var);
        }

        @Override
        public boolean isEmpty() {
            return false;
        }
    }
}
