package org.liujk.java.framework.base.exceptions;



/**
 * 说明：
 * <p>
 *
 */
public class Exceptions {
    private static Clause TRUE_CLAUSE = new Clause (true);
    private static Clause FALSE_CLAUSE = new Clause (false);

    public static RuntimeException newRuntimeException (String message) {
        return new AppException (message);
    }

    public static RuntimeException newRuntimeException (String message, Throwable cause) {
        return new AppException (message, cause);
    }

    public static RuntimeException newRuntimeException (Throwable cause) {
        return new AppException (cause.getMessage (), cause);
    }

    public static RuntimeException newRuntimeExceptionWithoutStackTrace (String message,
                                                                         Throwable cause) {
        return new AppException (message, cause, false, false);
    }

    public static RuntimeException newRuntimeExceptionWithoutStackTrace (Throwable cause) {
        return new AppException (cause.getMessage (), cause, false, false);
    }

    public static RuntimeException newRuntimeExceptionWithoutStackTrace (String message) {
        return new AppException (message, null, false, false);
    }

    public static RuntimeException rethrowCause (Throwable throwable) {
        Throwable cause = throwable;
        if (cause.getCause () != null) {
            cause = cause.getCause ();
        }
        return rethrow (cause);
    }

    public static RuntimeException rethrow (Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else if (throwable instanceof Error) {
            throw (Error) throwable;
        } else {
            throw new AppException (throwable.getMessage (), throwable, true, false);
        }
    }

    public static Clause when (boolean condition) {
        return condition ? TRUE_CLAUSE : FALSE_CLAUSE;
    }

    public static Clause whenNot (boolean condition) {
        return when (!condition);
    }

    public static Clause whenNull (Object object) {
        return when (object == null);
    }

    public static class Clause {

        private final boolean condition;

        public Clause (boolean condition) {
            this.condition = condition;
        }

        public void throwIllegalState (String message) {
            if (condition) {
                throw new IllegalStateException (message);
            }
        }

        public void throwIllegalArgument (String message) {
            if (condition) {
                throw new IllegalArgumentException (message);
            }
        }

    }
}
