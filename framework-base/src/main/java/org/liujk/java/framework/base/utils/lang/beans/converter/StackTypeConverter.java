//
//package org.liujk.java.framework.base.utils.lang.beans.converter;
//
//
//import org.liujk.java.framework.base.utils.lang.CollectionUtils;
//import org.liujk.java.framework.base.utils.lang.ReflectionUtils;
//import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.TypeConversionException;
//import org.liujk.java.framework.base.utils.lang.beans.reference.InvocationTargetRunTimeException;
//import org.liujk.java.framework.base.utils.lang.collection.LinkedStack;
//import org.liujk.java.framework.base.utils.lang.collection.Stack;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.List;
//
///**
// * 说明：
// * <p>
// * {@link Stack}的类型转换器。
// *
// */
//public class StackTypeConverter extends AbstractTypeConverter<Stack<?>> {
//
//    @SuppressWarnings("unchecked")
//    public static <E extends Stack<?>> E stackValue (Object value,
//                                                     Class<? extends E> stackClassType) {
//        Stack<Object> stack = null;
//        if ((Class<?>) stackClassType == Stack.class) {
//            // 使用 LinkedStack 作为实现
//            stack = new LinkedStack<Object> ();
//        } else { // 如果是具体的类则使用该类的类型
//            try {
//                stack = (Stack<Object>) ReflectionUtils.createObject (stackClassType);
//            } catch (InvocationTargetRunTimeException e) {
//                throw new TypeConversionException(e.getTargetException ());
//            } catch (Exception e) {
//                throw new TypeConversionException (e);
//            }
//        }
//        CollectionUtils.push (stack, value);
//        return (E) stack;
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public Class<Stack<?>> getTargetType () {
//        Class<?> stackClass = Stack.class;
//        return (Class<Stack<?>>) stackClass;
//    }
//
//    @Override
//    public List<Class<?>> getSupportedSourceTypes () {
//        return Arrays.asList (Collection.class, Object[].class);
//    }
//
//    @Override
//    public Stack<?> convert (Object value, Class<? extends Stack<?>> toType) {
//        return stackValue (value, toType);
//    }
//}
