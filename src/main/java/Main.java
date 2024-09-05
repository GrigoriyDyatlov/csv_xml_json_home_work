import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String file = "data.csv";
        List<Employee> staff = parseCSV(file, columnMapping);
        String staffJson = listToJson(staff);
        writeString(staffJson, "data.json");
        String fileXml = "data.xml";
        String staffXmlToJson = listToJson(parseXML(fileXml));
        writeString(staffXmlToJson, "data2.json");
    }

    public static List<Employee> parseCSV(String fileName, String[] columnMapping) {
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String listToJson(List<Employee> staff) {
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String json = gson.toJson(staff, listType);
        return json;
    }

    public static void writeString(String string, String fileName) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(string);
            writer.flush();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        List<Employee> staff = new ArrayList<>();
        NodeList employees = doc.getElementsByTagName("employee");
        for (int i = 0; i < employees.getLength(); i++) {
            Node node = employees.item(i);
            Element element = (Element) node;

            Employee employee = new Employee(
                    Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                    element.getElementsByTagName("firstName").item(0).getTextContent(),
                    element.getElementsByTagName("lastName").item(0).getTextContent(),
                    element.getElementsByTagName("country").item(0).getTextContent(),
                    Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent()));

            staff.add(employee);
        }
        return staff;
    }


}
