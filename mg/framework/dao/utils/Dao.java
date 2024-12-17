package mg.framework.dao.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class Dao {

    public void create(Connection conn) {
        Object obj = this;
        PreparedStatement ps = null;
        Reflect reflect = new Reflect();
        Doc doc = new Doc();
        String tableName = reflect.getTableName(obj);
        Field[] colonnes = reflect.getColonnes(obj);
        try{
            String sql = doc.prepareSQLRequestForInsert(tableName, colonnes);
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            int indice = 1;
            for (int i = 0; i < colonnes.length; i++) {
                if (!reflect.isPrimaryKey(colonnes[i])) {
                    Method objMethod = obj.getClass().getDeclaredMethod("get"+doc.majFormat(colonnes[i].getName()));
                    Method psMethod = ps.getClass().getDeclaredMethod("set"+doc.majFormat(colonnes[i].getType().getSimpleName()),int.class,colonnes[i].getType());
                    psMethod.setAccessible(true);
                    Object [] args = new Object[2];
                    args[0] = indice;
                    args[1] = objMethod.invoke(obj);
                    psMethod.invoke(ps,args);
                    indice++;
                }
            }
            ps.executeUpdate();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void update(String col_req,String col_condition, Connection conn) {
        Object obj = this;
        PreparedStatement ps = null;
        Reflect reflect = new Reflect();
        Doc doc = new Doc();
        String tableName = reflect.getTableName(obj);
        Field[] colonnes = reflect.getColonnes(obj);
        try{

            // UPDATE tableName SET colName=?,.....
            String sql_1 = "";
            Field [] cols1 = reflect.convertToColonnes(col_req,colonnes);
            if (cols1.length <= 0){
                cols1=colonnes;
            }
            sql_1 = doc.prepareSQLRequestForUpdate(tableName, cols1);

            // WHERE colName=? .......
            String sql_2 = "";
            Field [] cols2 = reflect.convertToColonnes(col_condition,colonnes);
            if (cols2.length <= 0){
                cols2=colonnes;
            }
            sql_2 = doc.prepareCodition(tableName, cols2);

            String sql = sql_1+sql_2;
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            int indice = 1;
            for (Field c : cols1) {
                if (!reflect.isPrimaryKey(c)) {
                    Method objMethod = obj.getClass().getDeclaredMethod("get"+doc.majFormat(c.getName()));
                    Method psMethod = ps.getClass().getDeclaredMethod("set"+doc.majFormat(c.getType().getSimpleName()),int.class,c.getType());
                    psMethod.setAccessible(true);
                    Object [] args = new Object[2];
                    args[0] = indice;
                    args[1] = objMethod.invoke(obj);
                    psMethod.invoke(ps,args);
                    indice++;
                }
            }
            for (Field c : cols2) {
                Method objMethod = obj.getClass().getDeclaredMethod("get"+doc.majFormat(c.getName()));
                Method psMethod = ps.getClass().getDeclaredMethod("set"+doc.majFormat(c.getType().getSimpleName()),int.class,c.getType());
                psMethod.setAccessible(true);
                Object [] args = new Object[2];
                args[0] = indice;
                args[1] = objMethod.invoke(obj);
                psMethod.invoke(ps,args);
                indice++;
            }
            ps.executeUpdate();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String col_condition, Connection conn) {
        Object obj = this;
        PreparedStatement ps = null;
        Reflect reflect = new Reflect();
        Doc doc = new Doc();
        String tableName = reflect.getTableName(obj);
        Field[] colonnes = reflect.getColonnes(obj);
        try{

            // WHERE colName=? .......
            String sql_2 = "";
            Field [] cols2 = reflect.convertToColonnes(col_condition,colonnes);
            if (cols2.length <= 0){
                cols2=colonnes;
            }
            sql_2 = doc.prepareCodition(tableName, cols2);

            String sql = "DELETE FROM "+tableName+sql_2;
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            int indice = 1;
            for (Field c : cols2) {
                Method objMethod = obj.getClass().getDeclaredMethod("get"+doc.majFormat(c.getName()));
                Method psMethod = ps.getClass().getDeclaredMethod("set"+doc.majFormat(c.getType().getSimpleName()),int.class,c.getType());
                psMethod.setAccessible(true);
                Object [] args = new Object[2];
                args[0] = indice;
                args[1] = objMethod.invoke(obj);
                psMethod.invoke(ps,args);
                indice++;
            }
            ps.executeUpdate();
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Object> read(String order_req, Connection conn) {
        Object obj = this;
        List<Object> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet res = null;
        Reflect reflect = new Reflect();
        Doc doc = new Doc();
        String tableName = reflect.getTableName(obj);
        Field[] colonnes = reflect.getColonnes(obj);

        try{

            String sql = "SELECT * FROM "+tableName+" "+order_req;
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            res = ps.executeQuery();
            while (res.next()){
                Object new_obj = obj.getClass().newInstance();
                for (Field c : colonnes) {
                    Method objMethod = obj.getClass().getDeclaredMethod("set"+doc.majFormat(c.getName()),c.getType());
                    Method resMethod = res.getClass().getDeclaredMethod("get"+doc.majFormat(c.getType().getSimpleName()),String.class);
//                    resMethod.setAccessible(true);
                    Object [] args = new Object[1];
                    args[0] = resMethod.invoke(res,reflect.getColonneName(c));
                    objMethod.invoke(new_obj,args);
                }
                result.add(new_obj);
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    public List<Object> readWhere(String col_condition, String order_req, Connection conn) {
        Object obj = this;
        List<Object> result = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet res = null;
        Reflect reflect = new Reflect();
        Doc doc = new Doc();
        String tableName = reflect.getTableName(obj);
        Field[] colonnes = reflect.getColonnes(obj);

        try{

            // WHERE colName=? .......
            String sql_2 = "";
            Field [] cols2 = reflect.convertToColonnes(col_condition,colonnes);
            if (cols2.length <= 0){
                cols2=colonnes;
            }
            sql_2 = doc.prepareCodition(tableName, cols2);

            String sql = "SELECT * FROM "+tableName+sql_2+" "+order_req;
            System.out.println(sql);
            ps = conn.prepareStatement(sql);
            int indice = 1;
            for (Field c : cols2) {
                Method objMethod = obj.getClass().getDeclaredMethod("get"+doc.majFormat(c.getName()));
                Method psMethod = ps.getClass().getDeclaredMethod("set"+doc.majFormat(c.getType().getSimpleName()),int.class,c.getType());
                psMethod.setAccessible(true);
                Object [] args = new Object[2];
                args[0] = indice;
                args[1] = objMethod.invoke(obj);
                psMethod.invoke(ps,args);
                indice++;
            }
            res = ps.executeQuery();
            while (res.next()){
                Object new_obj = obj.getClass().newInstance();
                for (Field c : colonnes) {
                    Method objMethod = obj.getClass().getDeclaredMethod("set"+doc.majFormat(c.getName()),c.getType());
                    Method resMethod = res.getClass().getDeclaredMethod("get"+doc.majFormat(c.getType().getSimpleName()),String.class);
//                    resMethod.setAccessible(true);
                    Object [] args = new Object[1];
                    args[0] = resMethod.invoke(res,reflect.getColonneName(c));
                    objMethod.invoke(new_obj,args);
                }
                result.add(new_obj);
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }


}
