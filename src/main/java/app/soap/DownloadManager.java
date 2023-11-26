package app.soap;

import app.ForgalomApplication;
import app.mnb.MNBArfolyamServiceSoap;
import app.mnb.MNBArfolyamServiceSoapImpl;
import app.soap.db.Arfolyam;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import app.soap.db.Deviza;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DownloadManager {
    private static MNBArfolyamServiceSoap getSoapService() {
        MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
        return impl.getCustomBindingMNBArfolyamServiceSoap();
    }

    private static Document parseXml(String xml) throws Exception {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(xml));
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
    public static void downloadCurrencies() {
        MNBArfolyamServiceSoap service = getSoapService();
        try {
            Session session = ForgalomApplication.getDbSessionFactory().openSession();
            Transaction t = session.beginTransaction();

            List<Deviza> devizak = session.createQuery("FROM Deviza ").list();
            if (!devizak.isEmpty()) {
                System.out.println("A devizák már korábban letöltésre kerültek!");
                session.close();
                return;
            }

            session.createQuery("delete FROM Deviza ").executeUpdate();

            Document doc = parseXml(service.getInfo());
            NodeList currNodes = doc.getElementsByTagName("Curr");
            for (int idx = 0; idx < currNodes.getLength(); idx++) {
                Deviza deviza = new Deviza(currNodes.item(idx).getTextContent());
                session.save(deviza);
            }

            t.commit();
            session.close();
            System.out.println("A devizák listája frissítésre került.");
        } catch (Exception e) {
            System.err.print(e);
        }
    }

    public static void downloadRates(String from, String to, String rates) {
        MNBArfolyamServiceSoap service = getSoapService();
        try {
            if (from == null || to == null) {
                Document doc = parseXml(service.getInfo());
                NodeList firstDateNodes, lastDateNodes;
                firstDateNodes = doc.getElementsByTagName("FirstDate");
                lastDateNodes = doc.getElementsByTagName("LastDate");
                from = firstDateNodes.item(0).getTextContent();
                to = lastDateNodes.item(0).getTextContent();
            }
            if (rates == null) {
                Session session = ForgalomApplication.getDbSessionFactory().openSession();
                Transaction t = session.beginTransaction();
                List<Deviza> devizak = session.createQuery("FROM Deviza ").list();
                rates = devizak.stream().map(Deviza::getKod).collect(Collectors.joining(","));
                t.commit();
                session.close();
            }
            System.out.println(from + " - " + to + ": " + rates);

            // devizánkénti árfolyam lekérése és mentése
            Document doc = parseXml(
                    service.getExchangeRates(from, to, rates)
            );
            NodeList dayList = doc.getElementsByTagName("Day");

            Session session = ForgalomApplication.getDbSessionFactory().openSession();
            Transaction t = session.beginTransaction();
            session.createQuery("delete FROM Arfolyam ").executeUpdate();

            for (int idxDay = 0; idxDay < dayList.getLength(); idxDay++) {
                Node dayNode = dayList.item(idxDay);
                if (dayNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                Element dayElement = (Element) dayNode;
                String date = dayElement.getAttribute("date");

                NodeList rateList = dayElement.getElementsByTagName("Rate");

                for (int rateIdx = 0; rateIdx < rateList.getLength(); rateIdx++) {
                    Node rateNode = rateList.item(rateIdx);
                    if (dayNode.getNodeType() != Node.ELEMENT_NODE) {
                        continue;
                    }
                    Element rateElement = (Element) rateNode;
                    String currency = rateElement.getAttribute("curr");
                    String rate = rateElement.getTextContent();

                    Arfolyam arfolyam = new Arfolyam(LocalDate.parse(date), currency, Double.parseDouble(rate.replace(',', '.')));
                    session.save(arfolyam);
                }
            }
            t.commit();
            session.close();
        } catch (Exception e) {
            System.err.print(e);
        }
    }
}
