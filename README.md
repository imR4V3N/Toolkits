# Toolkits
This is a small tool created with java like a generic dao allowing crud management, a small servlet framework and file management such as import or export of csv file, pdf or even file upload or file downloadThis is a small tool created with java like a generic dao allowing crud management, a small servlet framework and file management such as import or export of csv file, pdf or even file upload or file download

### Here are some code examples using this tool

- #### Generic Dao

````java
package money;

import java.sql.Connection;

import mg.framework.dao.annotation.Column;
import mg.framework.dao.annotation.Table;
import mg.framework.dao.utils.Dao;

@Table(name="money_type")
public class Type extends Dao{
    @Column(name="id", isPK = true)
    private int id;
    @Column(name="name")
    private String name;

    public void save(Connection connection) {
        this.create(connection);
    }

    public Type[] select(Connection connection) {
        return this.read("ORDER BY id ASC", connection).toArray(new Type[]{});
    }

    public Type select(int id, Connection connection) {
        String query = "WHERE id = " + id;
        Type[] types = this.read(query, connection).toArray(new Type[]{});
        if (types.length == 0) {
            return null;
            
        }
        return types[0];
    }

    public void delete(Connection connection) {
        this.delete("id", connection);
    }

    public void update(Connection connection) {
        this.update("name", "id", connection);
    }
}
````

- #### Servlet Framework

````java
package controller;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import mg.framework.servlet.Controller;
import mg.framework.servlet.annotation.Mapping;
import money.Type;


@WebServlet(name = "TypeController", value = "*.TypeController")
public class TypeController extends Controller{
    @Mapping("insert")
    public void insert() throws IOException {
        PrintWriter out = response.getWriter();
        String name = request.getParameter("name");

        try {
            Connection connection = new Connexion().getConnexion();

            Type type = new Type(name);
            type.save(connection);

            connection.close();

            RequestDispatcher dispatcher = request.getRequestDispatcher("type.TypeController");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
}
````