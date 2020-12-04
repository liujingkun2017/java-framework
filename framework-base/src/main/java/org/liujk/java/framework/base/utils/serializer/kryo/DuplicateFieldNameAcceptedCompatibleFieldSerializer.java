
package org.liujk.java.framework.base.utils.serializer.kryo;



import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.minlog.Log;

import java.util.*;

/**
 * 说明：
 * <p>
 * Serializes objects using direct field assignment, with limited support for forward and backward
 * compatibility. Fields can be added or removed without invalidating previously serialized bytes.
 * Note that changing the type of a field is not supported.
 * <p>
 * Features like {@link CompatibleFieldSerializer} , <b>but
 * The situation where a super class has a field with the same name as a subclass will be
 * accepted.</b>
 *
 */
public class DuplicateFieldNameAcceptedCompatibleFieldSerializer<T>
        extends CompatibleFieldSerializer<T> {

    private CachedField[] fields;

    public DuplicateFieldNameAcceptedCompatibleFieldSerializer (Kryo kryo, Class type) {
        super (kryo, type);
    }

    /**
     * ignore the duplicated fields in the super class
     */
    @Override
    protected void initializeCachedFields () {
        // get all cached fields form super class
        CachedField[] fields = super.getFields ();
        boolean found = false;
        Map<String, CachedField> cachedFiledMap = new HashMap<String, CachedField> ();
        for (CachedField field : fields) {
            String fieldName = field.getField ().getName ();
            if (!cachedFiledMap.containsKey (fieldName)) {
                cachedFiledMap.put (fieldName, field);
            } else {
                // field belong to this.getType() will be save to cachedFiledMap
                if (field.getField ().getDeclaringClass ().equals (this.getType ())) {
                    cachedFiledMap.put (fieldName, field);
                } else {
                    Log.warn (field.getField ().getDeclaringClass () + "和" + this.getType ()
                                      + "中都存在[field=" + fieldName + "],请去掉" + this.getType () + "中的"
                                      + fieldName);
                    // field belong to super class will be ignored
                    found = true;
                }
            }
        }
        if (!found) {
            setFields (fields);
        } else {
            List<CachedField> list = new ArrayList<> (cachedFiledMap.values ());
            Collections.sort (list, this);
            // set cached fields, the fields order must be as before
            setFields (list.toArray (new CachedField[cachedFiledMap.size ()]));
        }
    }

    @Override
    public void removeField (String fieldName) {
        for (int i = 0; i < fields.length; i++) {
            CachedField cachedField = fields[i];
            if (cachedField.getField ().getName ().equals (fieldName)) {
                CachedField[] newFields = new CachedField[fields.length - 1];
                System.arraycopy (fields, 0, newFields, 0, i);
                System.arraycopy (fields, i + 1, newFields, i, newFields.length - i);
                fields = newFields;
                super.removeField (fieldName);
                return;
            }
        }
        throw new IllegalArgumentException (
                "Field \"" + fieldName + "\" not found on class: " + getType ().getName ());
    }

    /**
     * override the
     * {@link CompatibleFieldSerializer#getFields}
     */
    @Override
    public CachedField[] getFields () {
        return fields;
    }

    public void setFields (CachedField[] fields) {
        this.fields = fields;
    }

}