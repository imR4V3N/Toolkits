package mg.framework.utils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import com.itextpdf.text.pdf.PdfPTable;
import java.lang.reflect.Field;

public class Manager {

    private static String formatValue(Object value) {
        if (value instanceof java.sql.Date || value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.format(value);
        }
        return value.toString();
    }

    public static void processObject(Object object, PdfPTable table, String parentFieldName) throws IllegalAccessException {
        if (object == null) {
            table.addCell(parentFieldName);
            table.addCell("null");
            return;
        }

        Class<?> objClass = object.getClass();

        if (objClass.isPrimitive() || objClass == String.class || Number.class.isAssignableFrom(objClass)
                || objClass == java.sql.Date.class || objClass == Date.class) {
            table.addCell(parentFieldName);
            table.addCell(formatValue(object));
            return;
        }

        Field[] fields = objClass.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = parentFieldName.isEmpty() ? field.getName() : parentFieldName + "." + field.getName();
            Object fieldValue = field.get(object);

            if (fieldValue != null && !field.getType().isPrimitive() && field.getType() != String.class 
                && !Number.class.isAssignableFrom(field.getType()) && field.getType() != java.sql.Date.class 
                && field.getType() != Date.class) {
                processObject(fieldValue, table, fieldName);
            } else {
                table.addCell(fieldName);
                table.addCell(fieldValue != null ? formatValue(fieldValue) : "null");
            }
        }
    }

    public static Object convertValue(Class<?> fieldType, String value) {
        if (fieldType == String.class) {
            return value;
        } else if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == Date.class) {
            try {
                return Date.valueOf(value);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        } 
        return value;
    }


}