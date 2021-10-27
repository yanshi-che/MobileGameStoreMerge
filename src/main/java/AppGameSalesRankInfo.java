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

public class AppGameSalesRankInfo {
	String ust;
	long sleepLength;
	private ArrayList<AppGameItem> itemList;
	int div;

	public AppGameSalesRankInfo(String ust, ArrayList<AppGameItem> itemList, int div) {
		this.ust = ust;
		this.itemList = itemList;
		sleepLength = 20000;
		this.div = div;
	}

	public void infoReader() {
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
			NodeList infoList = (NodeList) xPath.evaluate("//h:body/h:div/h:div/h:div/h:table/h:tbody/h:tr",
					document, XPathConstants.NODESET);
			Node infoNode;
			String title;
			String rank;
			String rankSpan;
			String[] rstr = new String[2];
			String[] tstr = new String[2];
			for (int i = 0; i < infoList.getLength(); i++) { // 各tr要素について
				infoNode = infoList.item(i);
				title = xPath.evaluate("h:td[3]/h:strong/h:a", infoNode);
				rank = xPath.evaluate("h:td[1]", infoNode);
				rankSpan = xPath.evaluate("h:td[1]/h:span/h:span", infoNode);
				if (Pattern.compile("[\\p{Ps}\\p{Pe}]").matcher(title).find()) {
					tstr = title.split("[\\p{Ps}\\p{Pe}]");
				} else if (title.contains("-")) {
					tstr = title.split("-", 0);
				} else {
					tstr[0] = title;
				}
				tstr[0] = tstr[0].replaceAll(" ", "").replaceAll("　", "").replaceAll("\\〜", "灣");
				if (rank.contains("▼")) {
					rstr = rank.split("▼", 0);
				} else if (rank.contains("⇒")) {
					rstr = rank.split("⇒", 0);
				} else if (rank.contains("▲")) {
					rstr = rank.split("▲", 0);
				}
				//System.out.println(tstr[0]+rstr[0]+rankSpan);}
				for (AppGameItem item : itemList) {
					if ( tstr[0].contains(item.getTitle()) || item.getTitle().contains(tstr[0])
							|| tstr[0].matches(item.getTitle()) || item.getTitle().matches(tstr[0])) {
						if (div == 0) {
							item.setiosSaleRank(rstr[0]);
							item.setiosSpan(rankSpan);
						}else {
							item.setggSaleRank(rstr[0]);
							item.setggSpan(rankSpan);
						}
						break;
					}

				}
			}

			for (AppGameItem item : itemList) {
				if(div==0) {
					if (item.getiosSaleRank() == null) {
						item.setiosSaleRank("圏外");
						item.setiosSpan("-");
					}
				}else {
					if (item.getggSaleRank() == null) {
						item.setggSaleRank("圏外");
						item.setggSpan("-");
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println(e);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
	}
}
