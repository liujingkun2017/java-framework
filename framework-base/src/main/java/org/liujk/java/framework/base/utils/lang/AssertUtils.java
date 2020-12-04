
package org.liujk.java.framework.base.utils.lang;




import org.liujk.java.framework.base.api.CommonResultCode;
import org.liujk.java.framework.base.api.response.ResultCodeable;
import org.liujk.java.framework.base.exceptions.IllegalParameterException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 说明：
 * <p>
 * 断言工具
 *
 */
public abstract class AssertUtils {
    /**
     * Assert that an object is not {@code null}.
     * <pre class="code">Assert.notNull(clazz, "The class must not be null");</pre>
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalParameterException if the object is {@code null}
     */
    public static void notNull (Object object, String message) {
        notNull (object, CommonResultCode.ARGUMENT_NOT_BE_NULL, message);
    }

    public static void notNull (Object object, ResultCodeable resultCode) {
        if (object == null) {
            throw new IllegalParameterException(resultCode);
        }
    }

    public static void notNull (Object object, ResultCodeable resultCode, String message) {
        if (object == null) {
            throw new IllegalParameterException (resultCode, message);
        }
    }


    /**
     * Assert that the given String is not empty; that is,
     * it must not be {@code null} and not the empty String.
     * <pre class="code">Assert.hasLength(name, "Name must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalParameterException if the text is empty
     * @see StringUtils#hasLength
     */
    public static void hasLength (String text, String message) {
        hasLength (text, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void hasLength (String text, ResultCodeable resultCode) {
        if (!StringUtils.hasLength (text)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void hasLength (String text, ResultCodeable resultCode, String message) {
        if (!StringUtils.hasLength (text)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }


    /**
     * Assert that the given String contains valid text content; that is, it must not
     * be {@code null} and must contain at least one non-whitespace character.
     * <pre class="code">Assert.hasText(name, "'name' must not be empty");</pre>
     *
     * @param text    the String to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalParameterException if the text does not contain valid text content
     * @see StringUtils#hasText
     */
    public static void hasText (String text, String message) {
        hasText (text, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void hasText (String text, ResultCodeable resultCode) {
        if (!StringUtils.hasText (text)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void hasText (String text, ResultCodeable resultCode, String message) {
        if (!StringUtils.hasLength (text)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }


    /**
     * Assert a boolean expression, throwing an {@code IllegalArgumentException}
     * if the expression evaluates to {@code false}.
     * <pre class="code">Assert.isTrue(i &gt; 0, "The value must be greater than zero");</pre>
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if {@code expression} is {@code false}
     */
    public static void isTrue (boolean expression, String message) {
        isTrue (expression, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void isTrue (boolean expression, ResultCodeable resultCode) {
        if (!expression) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void isTrue (boolean expression, ResultCodeable resultCode, String message) {
        if (!expression) {
            throw new IllegalParameterException (resultCode, message);
        }
    }


    /**
     * Assert that an array contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(array, "The array must contain elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the object array is {@code null} or contains no elements
     */
    public static void notEmpty (Object[] array, String message) {
        notEmpty (array, CommonResultCode.ARGUMENT_NOT_BE_NULL, message);
    }

    public static void notEmpty (Object[] array, ResultCodeable resultCode) {
        if (ObjectUtils.isEmpty (array)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void notEmpty (Object[] array, ResultCodeable resultCode, String message) {
        if (ObjectUtils.isEmpty (array)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }

    /**
     * Assert that a collection contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     */
    public static void notEmpty (Collection<?> collection, String message) {
        notEmpty (collection, CommonResultCode.ARGUMENT_NOT_BE_NULL, message);
    }

    public static void notEmpty (Collection<?> collection, ResultCodeable resultCode) {
        if (CollectionUtils.isEmpty (collection)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void notEmpty (Collection<?> collection, ResultCodeable resultCode, String message) {
        if (CollectionUtils.isEmpty (collection)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }

    /**
     * Assert that a Map contains elements; that is, it must not be
     * {@code null} and must contain at least one element.
     * <pre class="code">Assert.notEmpty(collection, "Collection must contain elements");</pre>
     *
     * @param map     the collection to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the collection is {@code null} or
     *                                  contains no elements
     */
    public static void notEmpty (Map<?, ?> map, String message) {
        notEmpty (map, CommonResultCode.ARGUMENT_NOT_BE_NULL, message);
    }

    public static void notEmpty (Map<?, ?> map, ResultCodeable resultCode) {
        if (CollectionUtils.isEmpty (map)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void notEmpty (Map<?, ?> map, ResultCodeable resultCode, String message) {
        if (CollectionUtils.isEmpty (map)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }

    /**
     * Assert that the given text does not contain the given substring.
     * <pre class="code">Assert.doesNotContain(name, "rod", "Name must not contain 'rod'");</pre>
     *
     * @param textToSearch the text to search
     * @param substring    the substring to find within the text
     * @param message      the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the text contains the substring
     */
    public static void doesNotContain (String textToSearch, String substring, String message) {
        doesNotContain (textToSearch, substring, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void doesNotContain (String textToSearch, String substring, ResultCodeable resultCode) {
        if (StringUtils.hasLength (textToSearch) && StringUtils.hasLength (substring) &&
                textToSearch.contains (substring)) {
            throw new IllegalParameterException (resultCode);
        }
    }

    public static void doesNotContain (String textToSearch, String substring, ResultCodeable resultCode,
                                       String message) {
        if (StringUtils.hasLength (textToSearch) && StringUtils.hasLength (substring) &&
                textToSearch.contains (substring)) {
            throw new IllegalParameterException (resultCode, message);
        }
    }

    /**
     * Assert that an array contains no {@code null} elements.
     * <p>Note: Does not complain if the array is empty!
     * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static void noNullElements (Object[] array, String message) {
        noNullElements (array, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void noNullElements (Object[] array, ResultCodeable resultCode) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalParameterException (resultCode);
                }
            }
        }
    }

    public static void noNullElements (Object[] array, ResultCodeable resultCode, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new IllegalParameterException (resultCode, message);
                }
            }
        }
    }

    /**
     * Assert that a collection contains no {@code null} elements.
     * <p>Note: Does not complain if the array is empty!
     * <pre class="code">Assert.noNullElements(array, "The array must contain non-null elements");</pre>
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     *
     * @throws IllegalArgumentException if the object array contains a {@code null} element
     */
    public static void noNullElements (Collection<?> collection, String message) {
        noNullElements (collection, CommonResultCode.ILLEGAL_ARGUMENT, message);
    }

    public static void noNullElements (Collection<?> collection, ResultCodeable resultCode) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new IllegalParameterException (resultCode);
                }
            }
        }
    }

    public static void noNullElements (Collection<?> collection, ResultCodeable resultCode, String message) {
        if (collection != null) {
            for (Object element : collection) {
                if (element == null) {
                    throw new IllegalParameterException (resultCode, message);
                }
            }
        }
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     * <pre class="code">Assert.instanceOf(Foo.class, foo, "Foo expected");</pre>
     *
     * @param type    the type to check against
     * @param obj     the object to check
     * @param message a message which will be prepended to provide further context.
     *                If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     *                will be appended. If it ends in a space, the name of the offending object's
     *                type will be appended. In any other case, a ":" with a space and the name
     *                of the offending object's type will be appended.
     *
     * @throws IllegalParameterException if the object is not an instance of type
     */
    public static void isInstanceOf (Class<?> type, Object obj, String message) {
        notNull (type, "Type to check against must not be null");
        if (!type.isInstance (obj)) {
            instanceCheckFailed (type, obj, CommonResultCode.ILLEGAL_ARGUMENT, message);
        }
    }

    public static void isInstanceOf (Class<?> type, Object obj, ResultCodeable resultCode) {
        notNull (type, "Type to check against must not be null");
        if (!type.isInstance (obj)) {
            instanceCheckFailed (type, obj, resultCode, null);
        }
    }
    public static void isInstanceOf (Class<?> type, Object obj, ResultCodeable resultCode, String msg) {
        notNull (type, "Type to check against must not be null");
        if (!type.isInstance (obj)) {
            instanceCheckFailed (type, obj, resultCode, msg);
        }
    }


    /**
     * Assert that {@code superType.isAssignableFrom(subType)} is {@code true}.
     * <pre class="code">Assert.isAssignable(Number.class, myClass, "Number expected");</pre>
     * @param superType the super type to check against
     * @param subType the sub type to check
     * @param message a message which will be prepended to provide further context.
     * If it is empty or ends in ":" or ";" or "," or ".", a full exception message
     * will be appended. If it ends in a space, the name of the offending sub type
     * will be appended. In any other case, a ":" with a space and the name of the
     * offending sub type will be appended.
     * @throws IllegalArgumentException if the classes are not assignable
     */
    public static void isAssignable(Class<?> superType, Class<?> subType, String message) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType,CommonResultCode.ILLEGAL_ARGUMENT,  message);
        }
    }
    public static void isAssignable(Class<?> superType, Class<?> subType, ResultCodeable resultCode) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType,resultCode, "");
        }
    }
    public static void isAssignable(Class<?> superType, Class<?> subType,ResultCodeable resultCode, String message) {
        notNull(superType, "Super type to check against must not be null");
        if (subType == null || !superType.isAssignableFrom(subType)) {
            assignableCheckFailed(superType, subType, resultCode,message);
        }
    }


    private static void instanceCheckFailed (Class<?> type, Object obj, ResultCodeable resultCode, String msg) {
        String className = (obj != null ? obj.getClass ().getName () : "null");
        String result = "";
        boolean defaultMessage = true;
        if (StringUtils.hasLength (msg)) {
            if (endsWithSeparator (msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName (msg, className);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + ("Object of class [" + className + "] must be an instance of " + type);
        }
        throw new IllegalParameterException (resultCode, result);
    }

    private static void assignableCheckFailed (Class<?> superType, Class<?> subType,ResultCodeable resultCode, String msg) {
        String result = "";
        boolean defaultMessage = true;
        if (StringUtils.hasLength (msg)) {
            if (endsWithSeparator (msg)) {
                result = msg + " ";
            } else {
                result = messageWithTypeName (msg, subType);
                defaultMessage = false;
            }
        }
        if (defaultMessage) {
            result = result + (subType + " is not assignable to " + superType);
        }
        throw new IllegalParameterException (resultCode,result);
    }

    private static boolean endsWithSeparator (String msg) {
        return (msg.endsWith (":") || msg.endsWith (";") || msg.endsWith (",") || msg.endsWith ("."));
    }

    private static String messageWithTypeName (String msg, Object typeName) {
        return msg + (msg.endsWith (" ") ? "" : ": ") + typeName;
    }
}
