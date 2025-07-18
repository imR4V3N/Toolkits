package mg.framework.dao.utils;

import mg.framework.dao.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.sql.Timestamp;
import java.sql.Date;

public class Reflect {
    public String getTableName (Object obj){
        String result = null;
        Annotation colonne = obj.getClass().getAnnotation(Table.class);
        result = ((Table) colonne).name();
        if (result.equalsIgnoreCase("")){
            result = obj.getClass().getSimpleName();
        }
        return result;
    }

    public Field [] getColonnes (Object obj){
        Field [] attr = obj.getClass().getDeclaredFields();
        List<Field> fields = new ArrayList<>();
        for (Field f : attr){
            if (f.isAnnotationPresent(Column.class)){
                fields.add(f);
            }
        }
        return fields.toArray(new Field[]{});
    }

    public boolean isPrimaryKey (Field field){
        boolean result = false;
        Annotation colonne = field.getAnnotation(Column.class);
        result = ((Column) colonne).isPK();
        return result;
    }

    public String getColonneName (Field field){
        String result = null;
        Annotation colonne = field.getAnnotation(Column.class);
        result = ((Column) colonne).name();
        if (result.equalsIgnoreCase("")){
            result = field.getName();
        }
        return result;
    }


    public Field findColonneByName (String name,Field [] colonnes){
        Field result = null;
        for (Field f : colonnes){
            if (name.equalsIgnoreCase(this.getColonneName(f))){
                result = f;
                break;
            }
        }
        return result;
    }

    public Field [] convertToColonnes(String request,Field [] colonnes){
        List<Field> result = new ArrayList<>();
        String [] col_name = request.split(",");
        for (String c : col_name){
            Field f = findColonneByName(c,colonnes);
            if (f != null){
                result.add(f);
            }
        }
        return result.toArray(new Field[]{});
    }

    public Object castValue(String value, Class<?> clazz) throws Exception {
        Object result = null;

        try {
            if (clazz == String.class) {
                result = value;
            }

            if (clazz == Integer.TYPE || clazz == Integer.class) {
                result = Integer.valueOf(value);
            }

            if (clazz == Double.TYPE || clazz == Double.class) {
                result = Double.valueOf(value);
            }

            if (clazz == Date.class) {
                result = Date.valueOf(value);
            }

            if (clazz == Timestamp.class) {
                result = Timestamp.valueOf(value);
            }

            if (clazz == Boolean.TYPE) {
                result = Boolean.valueOf(value);
            }

            return result;
        } catch (Exception var5) {
            throw new Exception("Impossible de caster l'objet");
        }
    }
}
