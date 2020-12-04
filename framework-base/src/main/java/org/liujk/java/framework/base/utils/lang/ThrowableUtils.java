
package org.liujk.java.framework.base.utils.lang;



import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * 说明：
 * <p>
 * 嵌套的可投抛出信息工具(异常/错误)工具。
 *
 */
public abstract class ThrowableUtils {

    /**
     * 构建嵌套可投掷信息的 message 信息。
     *
     * @param message message 内容。
     * @param cause   可嵌套的可投掷信息。
     *
     * @return 构建的结果。
     */
    public static String buildMessage (String message, Throwable cause) {
        if (cause != null) {
            StringBuilder buf = new StringBuilder ();
            if (message != null) {
                buf.append (message).append ("; ");
            }
            buf.append ("嵌套的异常是 ").append (cause);
            return buf.toString ();
        } else {
            return message;
        }
    }

    /**
     * 包装转换一个检查异常为不检查异常。
     *
     * @param e 需要包装的异常。
     *
     * @throws RuntimeException 如果 e 是不检查异常。
     */
    public static void wrapToRunTimeException (Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException (e);
    }

    /**
     * 包装转换一个 {@link Throwable} 为不检查异常。
     * <p>
     *
     * @param t 需要包装的 Throwable 。
     *
     * @throws RuntimeException 如果 e 是不检查异常。
     * @throws Error            如果 e 是 Error。
     */
    public static void wrapToRunTimeException (Throwable t) {
        if (t instanceof RuntimeException) {
            throw (RuntimeException) t;
        }
        if (t instanceof Error) {
            throw (Error) t;
        }
        throw new RuntimeException (t);
    }

    /**
     * 返回 t 的 cause；如果 cause 不存在返回 t ，否则返回 null。
     *
     * @param t 一个 Throwable。
     *
     * @return t 的 cause；如果 cause 不存在返回 t ，否则返回 null。
     */
    public static Throwable getCause (Throwable t) {
        if (t == null) {
            return null;
        }
        Throwable cause = t.getCause ();
        if (cause != null) {
            return cause;
        }
        return t;
    }

    /**
     * 返回 t 的 底层原因(栈的最底层原因)。
     *
     * @param t 一个 Throwable。
     *
     * @return e 的 底层原因(栈的最底层原因)，e为空 返回 null。
     */
    public static String getCauseMessage (Throwable t) {
        if (t == null) {
            return null;
        }
        Throwable cause = t.getCause ();
        while (cause != null && cause.getCause () != null) {
            cause = cause.getCause ();
        }
        if (cause != null) {
            return cause.toString ();
        } else {
            return t.getMessage ();
        }
    }

    /**
     * 压缩堆栈异常，输出3层行错误信息
     *
     * @param throwable
     *
     * @return
     * @see ThrowableUtils#compressStackTrace(Throwable, int)
     */
    public static String compressStackTrace (Throwable throwable) {
        return compressStackTrace (throwable, 3);
    }

    /**
     * 压缩堆栈异常
     *
     * @param throwable
     * @param stackDepth
     *
     * @return
     */
    public static String compressStackTrace (Throwable throwable, int stackDepth) {
        if (throwable == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder ();

        StringWriter writer = new StringWriter ();

        throwable.printStackTrace (new PrintWriter (writer));

        BufferedReader reader = new BufferedReader (new StringReader (writer.toString ()));
        //- 逐行读取
        int counter = 0;
        String tmp;
        try {
            while((tmp=reader.readLine ())!=null){
                if (tmp.startsWith ("\tat")) {
                    if (counter++ < stackDepth) {
                        sb.append (tmp).append ("\n");
                    }
                } else {
                    counter = 0;
                    if ("\t... 5 more".equals (tmp)) {
                        sb.append ("\t…… more\n");
                    } else {
                        sb.append (tmp).append ("\n");
                    }
                }
            }
        } catch (Exception e) {
        }

        if (counter >= stackDepth) {
            sb.append ("\t…… more");
        }

        return sb.toString ();
    }


}
