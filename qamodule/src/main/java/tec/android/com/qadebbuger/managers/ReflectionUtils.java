package tec.android.com.qadebbuger.managers;

import java.lang.reflect.Field;

/**
 * @param <BASE>   is the type of class you want to inspect
 * @param <RESULT> is the type of the field inside the BASE class
 */
public class ReflectionUtils<BASE, RESULT> {

    public RESULT getProperty(BASE type, String fieldName) {
        Field field;
        RESULT desiredProperty = null;
        try {
            field = type.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            desiredProperty = (RESULT) field.get(type);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return desiredProperty;
    }

}