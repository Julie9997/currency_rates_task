package org.julie;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class CurrencyRatesApp {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Введите код валюты (например USD): ");
            String currencyCode = scanner.nextLine();

            System.out.print("Введите дату в формате (dd/MM/yyyy): ");
            String dateString = scanner.nextLine();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateString);

            URL cbrUrl = new URL("http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + dateString);
            CurrencyInfo currencyInfo = convertCurrency(cbrUrl, currencyCode);
            System.out.println(currencyCode + " (" + currencyInfo.getName() + "): " + currencyInfo.getRate());
        } catch (IOException | ParserConfigurationException |
                 SAXException | XPathExpressionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static CurrencyInfo convertCurrency(URL url, String currencyCode)
            throws IOException,
            ParserConfigurationException,
            SAXException,
            XPathExpressionException {

        DocumentBuilder documentBuilder = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder();
        Document document = documentBuilder.parse(url.openStream());
        XPath xPath = XPathFactory.newInstance().newXPath();

        String expressionCurrency =
                "/ValCurs/Valute[CharCode='" + currencyCode + "']";

        Node nodeCurrency = (Node) xPath.compile(expressionCurrency)
                .evaluate(document, XPathConstants.NODE);

        NodeList nodesCurrency = nodeCurrency.getChildNodes();

        String name = searchName(nodesCurrency);
        float courseCurrency = searchCourse(nodesCurrency);
        float nominalCurrency = searchNominal(nodesCurrency);

        return new CurrencyInfo(name, courseCurrency / nominalCurrency);
    }

    private static String searchName(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Name")) {
                return n.getChildNodes().item(0).getTextContent();
            }
        }
        return "";
    }

    private static float searchCourse(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Value")) {
                return Float.parseFloat(n.getChildNodes()
                        .item(0)
                        .getTextContent()
                        .replace(",", "."));
            }
        }
        return 0f;
    }

    private static float searchNominal(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); i++) {
            Node n = nodes.item(i);
            if (n.getNodeName().equals("Nominal")) {
                return Float.parseFloat(n.getChildNodes()
                        .item(0)
                        .getTextContent());
            }
        }
        return 0f;
    }
}

