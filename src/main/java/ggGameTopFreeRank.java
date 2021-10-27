import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import nu.validator.htmlparser.dom.HtmlDocumentBuilder;

public class ggGameTopFreeRank {
	long sleepLength;
	private ArrayList<AppGameItem> itemList;
	int count = 0;
	int i;

	public ggGameTopFreeRank(ArrayList<AppGameItem> itemList) {
		sleepLength = 10000;
		this.itemList = itemList;
		i = 0;
	}

	public ggGameTopFreeRank() {
		sleepLength = 10000;
		i = 0;
	}

	public void getAppStoreInfo(String ust) {
		try {
			Thread.sleep(sleepLength); //sleepLengthミリ秒sleep
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			URL url = new URL(ust);
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			// DOMツリーの構築
			HtmlDocumentBuilder builder = new HtmlDocumentBuilder();
			Document document = builder.parse(new InputSource(reader));
			// XPath の表現を扱う XPath オブジェクトを生成
			XPath xPath = XPathFactory.newInstance().newXPath();
			// XPath 式内で接頭辞 h がついている要素を HTML の要素として認識
			xPath.setNamespaceContext(new NamespaceContextHTML());
			NodeList infoList = (NodeList) xPath.evaluate(
					"//h:article[@class='standard_ranking_list']",
					document, XPathConstants.NODESET);
					System.out.println("Number of tracks: " + infoList.getLength());
			System.out.println();
			if (count == 1) {
				i = 20;
			} else if (count == 2) {
				i = 40;
			}
			Node infoNode;
			String right;
			String title;
			String[] str = new String[2];
			for (int k = i; k < infoList.getLength() + i; k++) { // 各article要素について
				infoNode = infoList.item(k - i);
				right = xPath.evaluate("h:div[2]/h:p[1]", infoNode);
				title = xPath.evaluate("h:div[2]/h:h2/h:a", infoNode);
				if (Pattern.compile("[\\p{Ps}\\p{Pe}]").matcher(title).find()) {
					str = title.split("[\\p{Ps}\\p{Pe}]", 0);
				} else if (title.contains("-")) {
					str = title.split("-", 0);
				} else {
					str[0] = title;
				}
				str[0] = str[0].replaceAll(" ", "").replaceAll("　", "").replaceAll("\\～", "灣");
				//itemList.add(new AppGameItem(k + 1, str[0], "©︎" + right));
				System.out.println(k + 1+ str[0]+ "©︎" + right);
			}
			count++;
			reader.close();
		} catch (IOException e) {
			System.out.println(e);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		ggGameTopFreeRank g=new ggGameTopFreeRank();
		g.getAppStoreInfo("https://appget.com/c/app_ranking/android/free/");
	}
}
