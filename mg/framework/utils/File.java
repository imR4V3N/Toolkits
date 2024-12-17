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

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

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

    public static void exportPdf(Object object, String file_name, String title) {
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file_name + ".pdf"));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);

            Paragraph paragraph_title = new Paragraph(title, titleFont);
            paragraph_title.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph_title);
            document.add(new Paragraph("\n"));

            PdfPTable table = new PdfPTable(2);

            Manager.processObject(object, table, "");

            document.add(table);

        } catch (FileNotFoundException | DocumentException | IllegalAccessException e) {
            e.printStackTrace();
        } finally {
            document.close();
        }
    }

    public static void manageCsv(List<?> objects, PrintWriter out, String separator) throws ServletException, IOException {

        if (objects == null || objects.isEmpty()) {
            return;
        }

        Field[] fields = new Reflect().getColonnes(objects.get(0));

        StringBuilder header = new StringBuilder();

        for (Field field : fields) {
            header.append(field.getName()).append(separator);
        }
        header.deleteCharAt(header.length() - 1);
        out.println(header.toString());

        for (Object obj : objects) {
            StringBuilder row = new StringBuilder();
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    row.append(value != null ? value.toString() : "").append(separator);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            row.deleteCharAt(row.length() - 1);
            out.println(row.toString());
        }
        out.flush();
    }

    public static void exportCsvServlet(List<?> objects, String file_name, HttpServletResponse response, String separator) throws IOException, ServletException {
        PrintWriter out = response.getWriter();
        Class<?> clazz = objects.get(0).getClass();

        String fileName = objects.isEmpty() ? file_name+".csv" : clazz.getSimpleName()+".csv";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        File.manageCsv(objects, out, separator);

        out.close();
    }

    public static void exportCsv(List<?> objects, String file_name, char separator) {
        
        java.io.File file = new java.io.File(file_name+".csv");
        
        try {
            CSVWriter writer = new CSVWriter(new FileWriter(file), separator, CSVWriter.NO_QUOTE_CHARACTER, 
                                                                                CSVWriter.DEFAULT_ESCAPE_CHARACTER, 
                                                                                CSVWriter.DEFAULT_LINE_END);
            
            Field[] fields = new Reflect().getColonnes(objects.get(0));

            List<String> headers = new ArrayList<>();
            for (Field field : fields) {
                headers.add(field.getName());
            }
            
            writer.writeNext(headers.toArray(new String[]{}));

            for (Object object : objects) {
                List<String> values = new ArrayList<>();
                for (Field field : fields) {
                    Method method = object.getClass().getDeclaredMethod("get"+new Doc().majFormat(field.getName()));
                    Object value = method.invoke(object);
                    values.add(value.toString());
                }
                writer.writeNext(values.toArray(new String[]{}));
                writer.flush();
            }
            writer.close();
        } catch (RuntimeException | IOException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } 
    }

    public static <T> List<T> importCsv(String filePath, Class<T> clazz, String separator) throws Exception {
        List<T> objects = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line = reader.readLine();
        String[] headers = line.split(separator);

        while ((line = reader.readLine()) != null) {
            String[] values = line.split(separator);

            T obj = clazz.getDeclaredConstructor().newInstance();

            for (int i = 0; i < headers.length; i++) {
                Field field = clazz.getDeclaredField(headers[i]);
                field.setAccessible(true);
                String value = values[i];

                Object convertedValue = Manager.convertValue(field.getType(), value);
                if (field.isAnnotationPresent(Column.class)) {
                    field.set(obj, convertedValue);
                } else {
                    throw new Exception("Undefinied attribute " + field.getName());
                }
            }
            objects.add(obj);
        }
        reader.close();
        return objects;
    }
}