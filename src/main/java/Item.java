public class Item {
	/** title要素 */
	private String title;
	/** link要素 */
	private String link;
	/** description要素 */
	private String description;
	/**
	 *  コンストラクタ
	 *  @param title タイトル
	 *  @param link リンク
	 *  @param description 説明の文章
	 */
	public Item(String title, String link, String description) {
		this.title = title;
		this.link = link;
		this.description = description;
	}
	/**
	 *  title要素の内容である item のタイトルを返す
	 *  @return タイトル
	 */
	public String getTitle() {
		return title;
	}
	/**
	 *  link要素の内容である URL を文字列で返す
	 *  @return リンク先のURL
	 */
	public String getLink() {
		return link;
	}
	/**
	 *  description要素の内容である説明の文章を返す
	 *  @return 説明の文章
	 */
	public String getDescription() {
		return description;
	}
	/**
	 *  この item 要素の文字列表現を返す
	 *  @return この item 要素の文字列表現
	 */
	@Override
	public String toString() {
		return "title: " + title + "\nlink: " + link + "\ndescription: " + description;
	}
}