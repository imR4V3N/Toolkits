package mg.framework.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import mg.framework.servlet.annotation.Mapping;

@WebServlet(
   name = "Controller",
   value = {"*.Controller"}
)
@MultipartConfig
public class Controller extends HttpServlet{
    public RequestDispatcher dispat;
    public HttpServletRequest request;
    public HttpServletResponse response;
 
    protected void processResquest() throws IOException {
       this.executeMethod(this, this.request.getRequestURI());
    }
 
    public void redirect(String url) throws ServletException, IOException {
       this.dispat = this.request.getRequestDispatcher(url);
       this.dispat.forward(this.request, this.response);
    }
 
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       this.request = request;
       this.response = response;
       this.processResquest();
    }
 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
       this.request = request;
       this.response = response;
       this.processResquest();
    }
 
    public String urlUtil(String url) {
       String[] listUrl = url.split("/");
       String result = listUrl[listUrl.length - 1];
       System.out.println("Target >>> " + result);
       return result;
    }
 
    public String getController(String url) {
       String result = this.urlUtil(url).split("\\.")[1];
       System.out.println("controller >>> " + result);
       return result;
    }
 
    public String getMethodName(String url) {
       String newUrl = this.urlUtil(url);
       String controllerName = this.getController(url);
       String result = newUrl.split("." + controllerName)[0];
       System.out.println("methodName >>> " + result);
       return result;
    }
 
    public void executeMethod(Object controller, String url) {
       Method[] controllerMethods = controller.getClass().getDeclaredMethods();
       Method[] newControllerMethods = controllerMethods;
       int length = controllerMethods.length;
 
       for(int i = 0; i < length; ++i) {
          Method controllerMethod = newControllerMethods[i];
          Annotation methodAnnotation = controllerMethod.getAnnotation(Mapping.class);
          Mapping mapping = (Mapping)methodAnnotation;
          if (mapping != null && mapping.value().equals(this.getMethodName(url))) {
             try {
                controllerMethod.invoke(controller);
                break;
             } catch (Exception e) {
                throw new RuntimeException(e);
             }
          }
       }
 
    }
}
