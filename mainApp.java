
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class mainApp {

   
    public static void main(String[] args) {
        
        try {
            // Check input arguments
            if (args.length == 0) {
                System.out.println("Usage: java ArxmlSorter input.arxml");
                return;
            }

            // Read input and output file names
            String file_Input = args[0];
            String file_Output = getFileName_Output(file_Input);

            // Validate input file extension
            FileExtension_Valid(file_Input);

            // Parse input file
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(file_Input));
            doc.getDocumentElement().normalize();

            // Check if input file is empty
            if (doc.getDocumentElement().getChildNodes().getLength() == 0) {
                throw new Exception_EmptyAutosarFile("Input ARXML file is empty.");
            }

            // Sort CONTAINER elements by SHORT-NAME
            NodeList containerList = doc.getElementsByTagName("CONTAINER");
            ArrayList<Element> containers = new ArrayList<>();
            for (int i = 0; i < containerList.getLength(); i++) {
                containers.add((Element) containerList.item(i));
            }
            Collections.sort(containers, (Element e1, Element e2) -> {
                String name1 = e1.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                String name2 = e2.getElementsByTagName("SHORT-NAME").item(0).getTextContent();
                return name1.compareTo(name2);
            });

            // Update document with sorted containers
            Element rootElement = doc.getDocumentElement();
            for (Element container : containers) {
                rootElement.appendChild(container);
            }

            // Save output file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(file_Output));
            transformer.transform(source, result);

            System.out.println("Containers sorted. Output written to " + file_Output);
        } catch (Exception_NotValidAutosarFile | Exception_EmptyAutosarFile e) { // incorrect File Extension "Exception"
            System.err.println("Error: " + e.getMessage());

        }catch (SAXException | IOException e) { // Input file does not have any content "Exception"
            System.err.println("Error: Input file does not have any content" );

        }
        // Empty File "Exception"
         catch (ParserConfigurationException | TransformerException | DOMException  e) {
            // "Exception"
                    }
    }

    private static String getFileName_Output(String file_Input) {
        int dotIndex = file_Input.lastIndexOf(".");
        String fileWithoutExtension = file_Input.substring(0, dotIndex);
        String file_Extension = file_Input.substring(dotIndex);
        return fileWithoutExtension + "_mod" + file_Extension;
    }

    private static void FileExtension_Valid(String fileName) throws Exception_NotValidAutosarFile {
        if (!fileName.endsWith(".arxml")) {
            throw new Exception_NotValidAutosarFile("Input file does not have .arxml extension.");
        }
    }

}

class Exception_NotValidAutosarFile extends Exception {
    public Exception_NotValidAutosarFile(String msg) {
        super(msg);
    }
}

class Exception_EmptyAutosarFile extends RuntimeException {
    public Exception_EmptyAutosarFile(String msg) {
        super(msg);
    }
        
}
