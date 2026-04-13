package file;

import data.*;
import utility.*;
import Exception.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Date;
import java.util.List;

/**
 * Менеджер для работы с файлами данных в формате XML.
 * Реализует чтение через BufferedReader и запись через OutputStreamWriter.
 * Использует DOM-парсер для преобразования XML в объекты Java и обратно.
 */
public class FileManager {
    private final File file;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FileManager(File file) throws FileNotFoundException {
        this.file = file;
        if (!file.exists()) throw new FileNotFoundException("Файл не найден");
        if (file.isDirectory()) throw new FileNotFoundException("Это директория");
        if (!file.canRead()) throw new FileOperationException("Нет прав на чтение файла");
    }
    /**
     * Читает коллекцию объектов LabWork из XML-файла.
     *
     * @return LinkedList заполненных объектов LabWork
     * @throws Exception если файл не найден, поврежден или данные не прошли валидацию
     */
    public LinkedList<LabWork> readElementsFromFile() throws Exception {
        LinkedList<LabWork> collection = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(reader));
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("labwork");
            List<LabWork> list = new java.util.ArrayList<>();
            LabWork[] works = new LabWork[nodeList.getLength()];

            for (int i = 0; i < nodeList.getLength(); i++) {
                works[i] = parseElement((Element) nodeList.item(i));
                list.add(works[i]);
            }
            LabWorkValidator.validateLabWorks(works);
            collection.addAll(list);
        } catch (Exception e) {
            throw new FileOperationException("Ошибка чтения файла: " + e.getMessage(), e);
        }
        return collection;
    }

    private LabWork parseElement(Element el) throws ParseException {
        Long id = Long.parseLong(getText(el, "id"));
        String name = getText(el, "name");
        double x = Double.parseDouble(getText(el, "x"));
        long y = Long.parseLong(getText(el, "y"));
        float minP = Float.parseFloat(getText(el, "minimalPoint"));
        double pqMax = Double.parseDouble(getText(el, "personalQualitiesMaximum"));
        Difficulty diff = Difficulty.valueOf(getText(el, "difficulty"));
        String discName = getText(el, "disciplineName");
        String lhStr = getText(el, "lectureHours");
        Integer lh = lhStr.isEmpty() ? null : Integer.parseInt(lhStr);
        Coordinates coords = new Coordinates(x, y);
        Discipline disc = new Discipline(discName, lh);
        Date date = new Date();
        String dStr = getText(el, "creationDate");
        if (!dStr.isEmpty()) date = dateFormat.parse(dStr);
        return new LabWork(id, name, coords, date, minP, pqMax, diff, disc);
    }

    private String getText(Element parent, String tag) {
        NodeList list = parent.getElementsByTagName(tag);
        return list.getLength() > 0 ? list.item(0).getTextContent() : "";
    }
    /**
     * Сохраняет текущую коллекцию объектов в XML-файл.
     * Использует OutputStreamWriter для записи символов в кодировке UTF-8.
     *
     * @param collection список объектов LabWork для сохранения
     */
    public void saveToFile(LinkedList<LabWork> collection) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            Element root = doc.createElement("labworks");
            doc.appendChild(root);

            for (LabWork lw : collection) {
                Element el = doc.createElement("labwork");
                root.appendChild(el);
                append(doc, el, "id", String.valueOf(lw.getId()));
                append(doc, el, "name", lw.getName());
                append(doc, el, "x", String.valueOf(lw.getCoordinates().getX()));
                append(doc, el, "y", String.valueOf(lw.getCoordinates().getY()));
                append(doc, el, "minimalPoint", String.valueOf(lw.getMinimalPoint()));
                append(doc, el, "personalQualitiesMaximum", String.valueOf(lw.getPersonalQualitiesMaximum()));
                append(doc, el, "difficulty", lw.getDifficulty().name());
                append(doc, el, "disciplineName", lw.getDiscipline().getName());
                append(doc, el, "lectureHours", lw.getDiscipline().getLectureHours() != null ? String.valueOf(lw.getDiscipline().getLectureHours()) : "");
                append(doc, el, "creationDate", dateFormat.format(lw.getCreationDate()));
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            DOMSource source = new DOMSource(doc);

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                StreamResult result = new StreamResult(writer);
                transformer.transform(source, result);
                System.out.println("Коллекция сохранена.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    private void append(Document doc, Element parent, String tag, String value) {
        Element e = doc.createElement(tag);
        e.appendChild(doc.createTextNode(value));
        parent.appendChild(e);
    }
}