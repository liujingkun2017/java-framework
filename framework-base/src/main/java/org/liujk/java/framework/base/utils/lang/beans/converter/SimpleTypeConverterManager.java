package org.liujk.java.framework.base.utils.lang.beans.converter;

import com.google.common.collect.Sets;
import org.liujk.java.framework.base.utils.lang.beans.converter.exceptions.ConverterNotFoundException;
import org.liujk.java.framework.base.utils.lang.beans.reference.ConcurrentReferenceMap;
import org.liujk.java.framework.base.utils.lang.beans.reference.ReferenceKeyType;
import org.liujk.java.framework.base.utils.lang.beans.reference.ReferenceValueType;
import org.springframework.util.Assert;

import java.util.*;
import java.util.concurrent.ConcurrentMap;

public class SimpleTypeConverterManager implements TypeConverterManager {


    private final TargetTypeConverterMap typeConverterMap = new TargetTypeConverterMap(32);

    public SimpleTypeConverterManager() {

    }

    public static void registerDefaultConverter(TypeConverterManager converterManager) {
        if (converterManager == null) {
            return;
        }
    }

    @Override
    public <T> Collection<TypeConverter<T>> getTypeConverter(Class<T> targetType) {
        if (targetType == null) {
            return Collections.emptyList();
        }
        ConcurrentMap<Class<?>, TypeConverter<?>> m = getSourceTypeConverterMap(targetType);
        return (Collection<TypeConverter<T>>) (m == null ?
                Collections.emptyList() : Collections.unmodifiableCollection(m.values()));
    }

    @Override
    public <S, T> TypeConverter<T> getTypeConverter(Class<S> sourceType, Class<T> targetType) {
        if (sourceType == null || targetType == null) {
            return null;
        }
        SourceTypeConverterMap m = getSourceTypeConverterMap(targetType);
        if (m == null) {
            if (targetType.isEnum()) {
                m = getSourceTypeConverterMap(Enum.class);
            } else if (Date.class.isAssignableFrom(targetType)) {
                m = getSourceTypeConverterMap(Date.class);
            }
            //todo money class
            //else if()
            if (m == null) {
                return null;
            }
        }
        TypeConverter<?> typeConverter = m.get(sourceType);
        if (typeConverter == null) {
            if (typeConverter == null && !m.notSourceTypeConverter(sourceType)) {
                typeConverter = m.getCache(sourceType);
            }
            if (typeConverter == null) {
                if (sourceType.isEnum()) {
                    typeConverter = m.getCache(Enum.class);
                } else if (Date.class.isAssignableFrom(targetType)) {
                    typeConverter = m.getCache(Date.class);
                }
                //todo money class
                //else if()
            }
            if (typeConverter == null) {
                typeConverter = m.get(ALL_SOURCE_TYPE_CLASS);
                if (typeConverter != null) {
                    m.putCache(ALL_SOURCE_TYPE_CLASS, typeConverter);
                }
            }

            if (typeConverter == null) {
                for (Map.Entry<Class<?>, TypeConverter<?>> entry : m.entrySet()) {
                    if (entry.getKey().isAssignableFrom(sourceType)) {
                        typeConverter = entry.getValue();
                        m.putCache(sourceType, typeConverter);
                        return (TypeConverter<T>) typeConverter;
                    }
                }
            }

            if (typeConverter == null) {
                m.addNotSourceTypeConverter(sourceType);
            }
        }

        return (TypeConverter<T>) typeConverter;
    }

    @Override
    public <S, T> void register(Class<? extends S> sourceType, Class<T> targetType, TypeConverter<? extends T> typeConverter) {
        Assert.notNull(typeConverter, "{typeConverter} can not be null");
        doRegister(sourceType, targetType, typeConverter);
    }

    @Override
    public void register(TypeConverter<?> typeConverter) {
        Assert.notNull(typeConverter, "{typeConverter} can not be null");
        List<Class<?>> supportedSourceTypes = typeConverter.getSupportedSourceTypes();
        Assert.notEmpty(supportedSourceTypes, "{typeConverter} 提供的源类型列表为空");
        for (Class<?> supportedSourceType : supportedSourceTypes) {
            doRegister(supportedSourceType, typeConverter.getTargetType(), typeConverter);
        }
    }

    @Override
    public <S, T> TypeConverter<T> unregister(Class<T> sourceType, Class<T> targetType) {
        if (sourceType == null || targetType == null) {
            return null;
        }
        SourceTypeConverterMap removed = getSourceTypeConverterMap(targetType);
        if (removed == null) {
            return null;
        }
        TypeConverter<?> removedTypeConverter = removed.remove(sourceType);
        return (TypeConverter<T>) removedTypeConverter;
    }

    @Override
    public <T> Collection<TypeConverter<T>> unregister(Class<T> targetType) {
        if (targetType == null) {
            return Collections.emptyList();
        }
        SourceTypeConverterMap removed = this.typeConverterMap.remove(targetType);
        return (Collection<TypeConverter<T>>) (removed == null ? Collections.emptyList() : removed.values());
    }

    @Override
    public boolean containsType(Class<?> targetType) {
        if (targetType == null) {
            return false;
        }
        return getTypeConverter(targetType) != null;
    }

    @Override
    public boolean containsType(Class<?> sourceType, Class<?> targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }
        return getTypeConverter(sourceType, targetType) != null;
    }

    @Override
    public boolean containsConverter(Class<TypeConverter<?>> typeConverterClass) {
        if (typeConverterClass == null) {
            return false;
        }
        Collection<SourceTypeConverterMap> values = this.typeConverterMap.values();
        for (ConcurrentMap<Class<?>, TypeConverter<?>> typeConverterMap : values) {
            for (TypeConverter<?> typeConverter : typeConverterMap.values()) {
                if (typeConverterClass == typeConverter.getClass()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        return getTypeConverter(sourceType, targetType) != null;
    }

    @Override
    public <T> T convert(Object source, Class<T> targetType) {
        if (source == null) {
            return null;
        }
        TypeConverter<T> typeConverter = getTypeConverter(source.getClass(), targetType);
        if (typeConverter == null) {
            throw new ConverterNotFoundException(source.getClass(), targetType);
        }
        return typeConverter.convert(source, targetType);
    }


    private <T> SourceTypeConverterMap getSourceTypeConverterMap(Class<T> targetType) {
        SourceTypeConverterMap m = this.typeConverterMap.getCache(targetType);
        if (m == null && !this.typeConverterMap.notTargetTypeConverter(targetType)) {
            m = this.typeConverterMap.getCache(targetType);
            if (m == null) {
                for (Map.Entry<Class<?>, SourceTypeConverterMap> entry : this.typeConverterMap.entrySet()) {
                    if (targetType.isAssignableFrom(entry.getKey())) {
                        m = entry.getValue();
                        this.typeConverterMap.putCache(targetType, m);
                        break;
                    }
                }
            }
            if (m == null) {
                this.typeConverterMap.addNotTargetTypeConverter(targetType);
            }
        }
        return m;
    }

    private void doRegister(Class<?> sourceType, Class<?> targetType, TypeConverter<?> typeConverter) {
        Assert.notNull(sourceType, "{sourceType} can not be null");
        Assert.notNull(targetType, "{targetType} can not be null");
        SourceTypeConverterMap sourceTypeMap = this.typeConverterMap.get(targetType);
        if (sourceType == null) {
            sourceTypeMap = new SourceTypeConverterMap(32);
            SourceTypeConverterMap old = this.typeConverterMap.putIfAbsent(targetType, sourceTypeMap);

            if (old != null) {
                sourceTypeMap = old;
            } else {
                this.typeConverterMap.clearAllNotTargetTypeConverter();
            }
        }
        sourceTypeMap.put(sourceType, typeConverter);
        sourceTypeMap.putCache(targetType, typeConverter);
    }


    static class SourceTypeConverterMap extends ConcurrentReferenceMap<Class<?>, TypeConverter<?>> {

        private final ConcurrentMap<Class<?>, TypeConverter<?>> sourceTypeConverterCache
                = new ConcurrentReferenceMap<Class<?>, TypeConverter<?>>(ReferenceKeyType.WEAK, ReferenceValueType.STRONG, 32);

        private final Set<Class<?>> notSourceTypeConverters = Sets.newConcurrentHashSet();

        public SourceTypeConverterMap(int initalCapacity) {
            super(ReferenceKeyType.WEAK, ReferenceValueType.STRONG, initalCapacity);
        }

        public SourceTypeConverterMap(ReferenceKeyType keyReferenceKeyType, ReferenceValueType valueReferenceKeyType) {
            super(keyReferenceKeyType, valueReferenceKeyType);
        }

        @Override
        public void clear() {
            this.sourceTypeConverterCache.clear();
            super.clear();
        }

        @Override
        public TypeConverter<?> remove(Object key) {
            TypeConverter<?> removedTypeConverter = super.remove(key);
            if (removedTypeConverter != null) {
                Class<?> sourceType = (Class<?>) key;
                for (Iterator<Class<?>> iterator = this.sourceTypeConverterCache.keySet().iterator(); iterator.hasNext(); ) {
                    if (sourceType.isAssignableFrom(iterator.next())) {
                        iterator.remove();
                    }
                }
            }
            return removedTypeConverter;
        }

        public boolean notSourceTypeConverter(Class<?> sourceType) {
            return this.notSourceTypeConverters.contains(sourceType);
        }

        public void addNotSourceTypeConverter(Class<?> sourceType) {
            this.notSourceTypeConverters.add(sourceType);
        }

        public TypeConverter<?> getCache(Class<?> sourceType) {
            return this.sourceTypeConverterCache.get(sourceType);
        }

        public void putCache(Class<?> sourceType, TypeConverter<?> typeConverter) {
            this.sourceTypeConverterCache.put(sourceType, typeConverter);
        }
    }

    static class TargetTypeConverterMap extends ConcurrentReferenceMap<Class<?>, SourceTypeConverterMap> {

        private final ConcurrentMap<Class<?>, SourceTypeConverterMap> targetTypeConverterCacheMap
                = new ConcurrentReferenceMap<Class<?>, SourceTypeConverterMap>(ReferenceKeyType.WEAK, ReferenceValueType.STRONG, 32);

        private final Set<Class<?>> notTargetTypeConverters = Sets.newConcurrentHashSet();

        public TargetTypeConverterMap(int initialCapacity) {
            super(ReferenceKeyType.WEAK, ReferenceValueType.STRONG, initialCapacity);
        }

        public TargetTypeConverterMap(ReferenceKeyType keyReferenceKeyType, ReferenceValueType valueReferenceKeyType) {
            super(keyReferenceKeyType, valueReferenceKeyType);
        }

        @Override
        public SourceTypeConverterMap remove(Object key) {
            SourceTypeConverterMap removed = super.remove(key);
            if (removed != null) {
                Class<?> targetType = (Class<?>) key;
                for (Iterator<Class<?>> iterator = this.targetTypeConverterCacheMap.keySet().iterator(); iterator.hasNext(); ) {
                    if (iterator.next().isAssignableFrom(targetType)) {
                        iterator.remove();
                    }
                }
            }
            return removed;
        }

        public boolean notTargetTypeConverter(Class<?> targetType) {
            return this.notTargetTypeConverters.contains(targetType);
        }

        public void addNotTargetTypeConverter(Class<?> targetType) {
            this.notTargetTypeConverters.add(targetType);
        }

        public void clearAllNotTargetTypeConverter() {
            this.notTargetTypeConverters.clear();
        }

        public void putCache(Class<?> targetType, SourceTypeConverterMap sourceTypeConverterMap) {
            this.targetTypeConverterCacheMap.put(targetType, sourceTypeConverterMap);
        }

        public SourceTypeConverterMap getCache(Class<?> targetType) {
            return this.targetTypeConverterCacheMap.get(targetType);
        }
    }

}
