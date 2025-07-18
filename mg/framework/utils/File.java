package mg.framework.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import mg.framework.dao.annotation.Column;
import mg.framework.dao.utils.Doc;
import mg.framework.dao.utils.Reflect;

@SuppressWarnings("resource")
public class File {

    public static boolean delete(String file_path, String file_name) {
        java.io.File file = new java.io.File(file_path +"/"+ file_name);
        if (file.exists()) {
            file.deleteOnExit();
        } else {
            return false;
        }
        return false;
    }

    public static void upload(Part part, String directory, String file_name) throws IOException {

        java.io.File fileDirectory = new java.io.File(directory);
        if (!fileDirectory.exists()) {
            fileDirectory.mkdirs();
        }
        java.io.File file = new java.io.File(fileDirectory, file_name);
        try (InputStream input = part.getInputStream()){
            Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (RuntimeException e) {
            throw new IOException();
        }
    }

    public static void download(HttpServletResponse response, String file_path, String file_name) throws Exception {
        try {
            response.setHeader("Content-Disposition", "attachment;filename="+file_name);
            response.setContentType("text/plain");
            InputStream is = new FileInputStream(file_path +"/"+ file_name);
            OutputStream os = response.getOutputStream();
            int count;
            byte buf[] = new byte[4096];
            while ((count = is.read(buf)) > -1)
                os.write(buf, 0, count);
            is.close();
            os.close();
        } catch (Exception e) {
            throw new Exception();
        }
    }
}