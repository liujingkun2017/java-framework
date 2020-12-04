package org.liujk.java.framework.base.utils.lang.beans.reference;



import java.lang.reflect.*;

/**
 * 说明：
 * <p>
 * 字段类型扫描器。
 * <p>
 * 该扫描器用于扫描类字段中泛型的类型信息。
 * <p>
 * 注意：该类的实例不是线程安全的。
 *
 */
public class FieldTypeScanner {
    private Mode mode;

    private Member member;

    private Type type;

    private int index;

    private boolean current;

    private boolean next;

    private Class<?> currentType;

    private Class<?> defaultClass;

    /**
     * 构造一个空的FieldTypeScanner。
     */
    public FieldTypeScanner () {

    }

    /**
     * 构造一个FieldTypeScanner。
     *
     * @param mode   使用的模式。
     * @param member 需要扫描字段的信息。
     */
    public FieldTypeScanner (Mode mode, Member member) {
        this (mode, member, null);
    }

    /**
     * 使用指定的默认类型构造一个FieldTypeScanner。
     *
     * @param mode         使用的模式。
     * @param member       需要扫描字段的信息。
     * @param defaultClass 如果没有扫描到类型信息则使用的默认类型。
     */
    public FieldTypeScanner (Mode mode, Member member, Class<?> defaultClass) {
        rest (mode, member, defaultClass);
    }

    /**
     * 置当前扫描器为最初调用 {@link #getNextType()} （包括重载方法）方法之前的状态。
     */
    public void restNext () {
        rest (this.mode, this.member, this.defaultClass);
    }

    /**
     * 置当前扫描器为指定状态。
     *
     * @param mode         使用的模式。
     * @param member       需要扫描字段的信息。
     * @param defaultClass 如果没有扫描到类型信息则使用的默认类型。
     */
    public void rest (Mode mode, Member member, Class<?> defaultClass) {
        this.mode = mode;
        this.member = member;
        this.defaultClass = defaultClass;
        this.type = null;
        this.next = true;
        this.current = false;
        this.currentType = null;
        this.index = 0;
    }

    private void rest (Type type, Class<?> currentType, int index, boolean next, boolean current) {
        this.type = type;
        this.currentType = currentType;
        this.index = index;
        this.next = next;
        this.current = current;
    }

    /**
     * 得到 默认类型的 Class 。
     *
     * @return 默认类型的 Class 。
     */
    public Class<?> getDefaultClass () {
        return defaultClass;
    }

    public Class<?> getCurrentType () {
        return this.currentType;
    }

    /**
     * 判断在下一次调用 {@link #getNextType()} （包括重载方法）是否能扫描出的类型信息，如果存在多个泛型类型，则取第一个。
     *
     * @return 如果在下一次调用 {@link #getNextType()} （包括重载方法）能扫描出的类型信息返回 true ，否则返回 false 。
     */
    public boolean hasNext () {
        return hasNext0 (null, 0);
    }

    /**
     * 判断在下一次调用 {@link #getNextType()} （包括重载方法）是否能扫描出的类型信息。
     *
     * @param i 如果存在多个泛型类型，则取值的下标（0开始）。
     *
     * @return 如果在下一次调用 {@link #getNextType()} （包括重载方法）能扫描出的类型信息返回 true ，否则返回 false 。
     */
    public boolean hasNext (int i) {
        return hasNext0 (null, i);
    }

    /**
     * 判断在下一次调用 {@link #getNextType()} （包括重载方法）是否能扫描出的类型信息。
     * <p>
     * 如果 type 不为 null 则扫描分两个阶段。在阶段一仍然使用扫描器内部的状态信息进行扫描，如果扫描到类型信息则返回true，否则进入第二阶段。第二阶段使用 type
     * 指定的类型信息进行扫描，如果扫描到类型信息则返回true，否则返回false。在整个过程中，只要 type 不为 null ，即便Member扫描不到信息也会使用 type
     * 指定的类型进行扫描。
     *
     * @param type 指定的类型的 {@link Class} 对象。
     * @param i    如果存在多个泛型类型，则取值的下标（0开始）。
     *
     * @return 如果在下一次调用 {@link #getNextType()} （包括重载方法）能扫描出的类型信息返回 true ，否则返回 false 。
     */
    public boolean hasNext (Type type, int i) {
        return hasNext0 (type, i);
    }

    private boolean hasNext0 (Type type, int i) {
        Type oType = this.type;
        Class<?> currentType = this.currentType;
        int index = this.index;
        boolean next = this.next;
        boolean current = this.current;
        getNextType0 (type, i);
        boolean c = this.current;
        rest (oType, currentType, index, next, current);
        return c;
    }

    /**
     * 执行下一个扫描。如果存在多个泛型类型，则取第一个。
     *
     * @return 扫描出的类型信息或者默认类型信息。
     */
    public Class<?> getNextType () {
        return this.currentType = getNextType0 (null, 0);
    }

    /**
     * 执行下一个扫描。
     *
     * @param i 如果存在多个泛型类型，则取值的下标（0开始）。
     *
     * @return 扫描出的类型信息或者默认类型信息。
     */
    public Class<?> getNextType (int i) {
        return this.currentType = getNextType0 (null, i);
    }

    /**
     * 执行下一个扫描，在当前扫描没有扫描到类型信息时使用 type 指定的类型完成扫描。 如果 type 为 Class 类型，则直接返回 type 的值。
     * <p>
     * 如果 type 不为 null 则扫描分两个阶段。在阶段一仍然使用扫描器内部的状态信息进行扫描，如果扫描到类型信息则返回扫描到的类型信息，否则进入第二阶段。第二阶段使用 type
     * 指定的类型信息进行扫描，如果扫描到类型信息则返回扫描到的类型信息，否则返回默认类型。在整个过程中，只要 type 不为 null ，即便Member扫描不到信息也会使用 type
     * 指定的类型进行扫描。
     *
     * @param type 指定的类型的 {@link Class} 对象。
     * @param i    如果存在多个泛型类型，则取值的下标（0开始）。
     *
     * @return 扫描出的类型信息或者默认类型信息。
     */
    public Class<?> getNextType (Type type, int i) {
        return getNextType0 (type, i);
    }

    private Class<?> getNextType0 (Type type, int i) {
        if (type instanceof Class<?>) {
            this.index++;
            this.current = true;
            return (Class<?>) type;
        }
        if (!this.next && type == null) {
            this.current = false;
            return this.defaultClass;
        }
        if (this.index == 0) {
            // 如果是第一次解析，那么直接从方法的参数中取
            this.type = get ();
        }
        this.index++;
        if (this.type instanceof ParameterizedType) {
            // 参数化泛型信息
            this.type = ((ParameterizedType) this.type).getActualTypeArguments ()[i];
            Class<?> returnValue = findType (this.type);
            if (returnValue != null) {
                return returnValue;
            }
        } else if (this.type instanceof GenericArrayType) {
            // 数组泛型信息
            this.type = ((GenericArrayType) this.type).getGenericComponentType ();
            Class<?> returnValue = findType (this.type);
            if (returnValue != null) {
                return returnValue;
            }
        }
        // 如果执行到这里，则表示原有的泛型信息不足以取出正确的类型，那么就从方法传入的 Type 中取
        if (type == null) {
            this.next = false;
            this.current = false;
            return this.defaultClass;
        }
        this.type = type;
        return getNextType0 (null, 0);
    }

    private Type get () {
        switch (this.mode) {
            case FIELD:
                return ((Field) this.member).getGenericType ();
            case GETTER_METHOD:
                return ((Method) this.member).getGenericReturnType ();
            case SETTER_METHOD:
                return ((Method) this.member).getGenericParameterTypes ()[0];
            default:
                return null;
        }
    }

    private Class<?> findType (Type type) {
        if (type instanceof Class<?>) {
            // 如果为 Class ，则表示没有更多的泛型信息
            this.next = false;
            this.current = true;
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return returnValue (((ParameterizedType) type).getRawType ());
        } else if (type instanceof GenericArrayType) {
            return returnValue (((GenericArrayType) type).getGenericComponentType ());
        }
        return null;
    }

    private Class<?> returnValue (Type type) {
        if (type instanceof Class<?>) {
            this.current = true;
            return (Class<?>) type;
        } else {
            this.current = false;
            return this.defaultClass;
        }
    }

    /**
     * 扫描使用的模式。
     */
    public static enum Mode {

        /**
         * 设置器方法
         */
        SETTER_METHOD,

        /**
         * 访问器方法
         */
        GETTER_METHOD,

        /**
         * 字段
         */
        FIELD;

    }
}
