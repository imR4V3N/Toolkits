package mg.framework.utils;

import jakarta.servlet.http.HttpServlet;

public class Path extends HttpServlet{
    
    public String getPath() {
        return getServletContext().getRealPath("/");
    }
}