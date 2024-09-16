import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;

public class XbeXtlidDbGen {

    public static final Map<String, String[]> xtlids = new HashMap<>();

    public static void main(String[] args) {
        try {
            File inputFile = new File(args[0]);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList libList = doc.getElementsByTagName("lib");
            for (int i = 0; i < libList.getLength(); i++) {
                Node libNode = libList.item(i);
                if (libNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element libElement = (Element) libNode;
                    String libName = libElement.getAttribute("name");

                    NodeList funcList = libElement.getElementsByTagName("func");
                    for (int j = 0; j < funcList.getLength(); j++) {
                        Node funcNode = funcList.item(j);
                        if (funcNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element funcElement = (Element) funcNode;
                            String funcId = funcElement.getAttribute("id");
                            String funcName = funcElement.getAttribute("name");

                            // Populate the xtlids map with data from XML
                            xtlids.put(funcId, new String[]{libName, funcName});
                        }
                    }
                }
            }

            // Output the generated Java code
            generateJavaCode();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void generateJavaCode() {
        System.out.println("package xbeloader;");
        System.out.println();
        System.out.println("import java.util.HashMap;");
        System.out.println("import java.util.Map;");
        System.out.println();
        System.out.println("public class XbeXtlidDb {");
        System.out.println("    public static final Map<Long, String[]> xtlids;");
        System.out.println();
        System.out.println("    static {");
        System.out.println("        xtlids = new HashMap<>();");

        // Print all entries from the xtlids map
        for (Map.Entry<String, String[]> entry : xtlids.entrySet()) {
            String id = entry.getKey();
            String[] values = entry.getValue();
            System.out.printf("        xtlids.put(%sL, new String[]{\"%s\", \"%s\"});%n", id, values[0], values[1]);
        }

        System.out.println("    }");
        System.out.println("}");
    }
}
