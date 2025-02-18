package mg.framework.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Builder<T> {
    private final Class<T> clazz;
    private final Map<String, Object> values = new HashMap<>();

    public Builder(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Builder<T> set(String fieldName, Object value) {
        values.put(fieldName, value);
        return this;
    }

    public T build() {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                Field field = clazz.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                field.set(instance, entry.getValue());
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Error creating instance of " + clazz.getName(), e);
        }
    }
}