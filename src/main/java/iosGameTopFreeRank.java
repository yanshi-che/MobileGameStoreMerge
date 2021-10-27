import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

public class iosGameTopFreeRank {
	private String urlString;
	private Document document;
	private ArrayList<AppGameItem> itemList;

	public iosGameTopFreeRank(String urlString) throws MalformedURLException {
		this.urlString = urlString;
		document = null;
		itemList = new ArrayList<>();
		Input(this.urlString);
	}

	public void Input(String urlString) {
		try {
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			document = this.buildDocument(inputStream, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Document buildDocument(InputStream inputStream, String encoding) {
		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS implementation = (DOMImplementationLS) registry.getDOMImplementation("XML 1.0");
			LSInput input = implementation.createLSInput();
			input.setByteStream(inputStream);
			input.setEncoding(encoding);
			LSParser parser = implementation.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			parser.getDomConfig().setParameter("namespaces", false);
			document = parser.parse(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	public ArrayList<AppGameItem> getAppStoreInfo() {
		try {
			XPathFactory factory = XPathFactory.newInstance();
			XPath xPath = factory.newXPath();
			NodeList itemNodeList = (NodeList) xPath.evaluate("/feed/entry",
					document, XPathConstants.NODESET);
			Node itemNode;
			String title;
			String right;
			String[] str = new String[2];
			for (int i = 0; i < itemNodeList.getLength(); i++) {
				itemNode = itemNodeList.item(i);
				title = xPath.evaluate("title", itemNode);
				right = xPath.evaluate("rights", itemNode);
				if (Pattern.compile("[\\p{Ps}\\p{Pe}]").matcher(title).find()) {
					str = title.split("[\\p{Ps}\\p{Pe}]");
				} else if (title.contains("-")) {
					str = title.split("-", 0);
				} else {
					str[0] = title;
				}
				str[0] = str[0].replaceAll(" ", "").replaceAll("　", "").replaceAll("\\～", "灣");
				itemList.add(new AppGameItem(i + 1, str[0], right));
			}
		} catch (DOMException e) {
			System.err.println("DOMエラー:" + e);
		} catch (XPathExpressionException e) {
			System.err.println("XPath 表現のエラー:" + e);
		}
		return itemList;
	}

}
