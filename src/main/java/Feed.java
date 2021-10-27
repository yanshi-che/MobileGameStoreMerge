import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

/**
 * フィード
 */
public class Feed {
	/** フィードの URL */
	private String urlString;
	/** DOMツリー */
	private Document document;
	/**
	 *  コンストラクタ
	 *  @param urlString フィードのURL
	 *  @param encoding フィードの文字コード
	 */
	public Feed(String urlString, String encoding) {
		// TLS v1.2 の有効化 (Java 8 以降では指定不要)
		System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
		this.urlString = urlString;
		try {
			// InputStreamの用意
			URL url = new URL(urlString);
			URLConnection connection = url.openConnection();
			connection.connect();
			InputStream inputStream = connection.getInputStream();
			// DOMツリーの構築
			document = this.buildDocument(inputStream, encoding);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 *  コンストラクタ
	 *  フィードの文字コードは UTF-8 を想定
	 *  @param urlString フィードのURL
	 */
	public Feed(String urlString) {
		this(urlString, "utf-8");
	}
	/**
	 * フィードの URLを返す
	 * @return URL
	 */
	public String getURLString() {
		return urlString;
	}
	/**
	 * item 要素のリストを返す
	 * @return item要素の ArrayList
	 */
	public ArrayList<Item> getItemList() {
		ArrayList<Item> itemList = new ArrayList<Item>();
		try {
			// XPath の表現を扱う XPath オブジェクトを生成
			XPath xPath = XPathFactory.newInstance().newXPath();
			// item要素のリストを得る (RSS 2.0の場合)
			NodeList itemNodeList = (NodeList)xPath.evaluate("/rss/channel/item",
					document, XPathConstants.NODESET);
			// item要素のリストを得る (RSS 1.0の場合)
			if(itemNodeList.getLength() == 0) {
				itemNodeList = (NodeList)xPath.evaluate("/RDF/item",
						document, XPathConstants.NODESET);
			}

			for(int i = 0; i < itemNodeList.getLength(); i++) {	// 各item要素について
				Node itemNode= itemNodeList.item(i);
				// item要素(itemNode) の子ノードである title, link, description 要素の内容を得る
				// それぞれ1要素しかないので NodeList で受ける必要がない
				String title = xPath.evaluate("title", itemNode);
				String link = xPath.evaluate("link", itemNode);
				String description = xPath.evaluate("description", itemNode);
				itemList.add(new Item(title, link, description));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return itemList;
	}
	/**
	 * DOM ツリーの構築
	 * @param inputStream XMLで記述されたテキストを InputStream にしたもの
	 * @param encoding テキストの文字コード
	 * @return 文書全体の DOM ツリー
	 */
	public Document buildDocument(InputStream inputStream, String encoding) {
		Document document = null;
		try {
			// DOM実装(implementation)の用意 (Load and Save用)
			DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS implementation = (DOMImplementationLS)registry.getDOMImplementation("XML 1.0");
			// 読み込み対象の用意
			LSInput input = implementation.createLSInput();
			input.setByteStream(inputStream);
			input.setEncoding(encoding);
			// 構文解析器(parser)の用意
			LSParser parser = implementation.createLSParser(DOMImplementationLS.MODE_SYNCHRONOUS, null);
			parser.getDomConfig().setParameter("namespaces", false);
			// DOMの構築
			document = parser.parse(input);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return document;
	}

	/** 動作確認用 */
	public static void main(String[] args) {
		//Feed feed = new Feed("https://kyoko-np.net/index.xml");
		Feed feed = new Feed("https://weather.goo.ne.jp/area/4410.rdf", "euc-jp");
		ArrayList<Item> itemList = feed.getItemList();
		// リストのitem要素を表示
		for(Item item: itemList) {
			System.out.println(item);
			System.out.println();
		}
	}
}
