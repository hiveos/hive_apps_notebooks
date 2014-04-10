package hive.apps.notebooks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.os.Environment;
import android.util.Log;

public class CitanjeXMLa {
	
	private File fajl;
	private String ime;
	private String stil;
	private String boja;
	
	public CitanjeXMLa(File xmlFajl)
	{
		fajl = xmlFajl;
		parsiraj(readFromFile());
	}
	
	private String readFromFile() {

		String ret = "";

		try {
			FileInputStream inputStream = new FileInputStream(fajl);

			if (inputStream != null) {
				InputStreamReader inputStreamReader = new InputStreamReader(
						inputStream);
				BufferedReader bufferedReader = new BufferedReader(
						inputStreamReader);
				String receiveString = "";
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null) {
					stringBuilder.append(receiveString);
				}

				inputStream.close();
				ret = stringBuilder.toString();
			}
		} catch (FileNotFoundException e) {
			Log.e("DAM", "File not found: " + e.toString());
		} catch (IOException e) {
			Log.e("DAM", "Can not read file: " + e.toString());
		}

		return ret;
	}
	
	public Document XMLfromString(String v) {

		Document doc = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(v));
			doc = db.parse(is);

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return doc;

	}
	
	private void parsiraj(String xml) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(xml));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("notebook");

			for (int i = 0; i < nodes.getLength(); i++) {
				Element element = (Element) nodes.item(i);

				NodeList name = element.getElementsByTagName("name");
				Element line = (Element) name.item(0);
				ime = getCharacterDataFromElement(line);
				Log.d("DAM", "Name: " + ime);

				NodeList title = element.getElementsByTagName("style");
				line = (Element) title.item(0);
				stil = getCharacterDataFromElement(line);
				Log.d("DAM", "Style: "  + stil);
				
				NodeList cc = element.getElementsByTagName("covercolor");
				line = (Element) cc.item(0);
				boja = getCharacterDataFromElement(line);
				Log.d("DAM", "CoverColor: "  + boja);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}
	
	public String getIme(){
		return ime;
	}
	
	public String getStil(){
		return stil;
	}
	
	public String getBoja(){
		return boja;
	}
}
