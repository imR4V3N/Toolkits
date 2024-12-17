package mg.framework.dao.utils;

import java.lang.reflect.Field;

public class Doc {
    public String majFormat(String mot){
        return mot.substring(0,1).toUpperCase() + mot.substring(1);
    }

    public String prepareSQLRequestForInsert(String tableName, Field [] tableColonnes) {
        Reflect reflect = new Reflect();
        String sql = "INSERT INTO "+tableName+"(";
        String sql_values = " VALUES (";
        for (int i = 0; i < tableColonnes.length; i++) {
            String virgule = "";
            if (i<tableColonnes.length-1){
                virgule = ",";
            }
            if (!reflect.isPrimaryKey(tableColonnes[i])){
                sql+=reflect.getColonneName(tableColonnes[i])+virgule;
                sql_values+="?"+virgule;
            }
        }
        sql+=")"+sql_values+")";
        return sql;
    }

    public String prepareSQLRequestForUpdate(String tableName, Field [] tableColonnes) {
        Reflect reflect = new Reflect();
        String sql = "UPDATE "+tableName+" SET ";
        for (int i = 0; i < tableColonnes.length; i++) {
            String virgule = "";
            if (i<tableColonnes.length-1){
                virgule = ",";
            }
            if (!reflect.isPrimaryKey(tableColonnes[i])){
                sql+=reflect.getColonneName(tableColonnes[i])+"=?"+virgule;
            }
        }
        return sql;
    }

    public String prepareCodition(String tableName, Field [] tableColonnes) {
        Reflect reflect = new Reflect();
        String sql = " WHERE ";
        for (int i = 0; i < tableColonnes.length; i++) {
            String virgule = "";
            if (i<tableColonnes.length-1){
                virgule = " AND ";
            }
            sql+=reflect.getColonneName(tableColonnes[i])+"=?"+virgule;
        }
        return sql;
    }
}
