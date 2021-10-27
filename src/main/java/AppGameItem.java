import java.net.MalformedURLException;
import java.util.ArrayList;

public class AppGameItem { //データ保持
	private int rank;
	private String title;
	private final String right;
	private int sortnum;
	private String iosRank;
	private String iosSaleRank;
	private String ggRank;
	private String ggSaleRank;
	private String iosSpan;
	private String ggSpan;

	public AppGameItem(String title, String right, String iosRank, String ggRank) {
		this.rank = 0;
		this.title = title;
		this.right = right;
		this.iosRank = iosRank;
		this.iosSaleRank = null;
		this.ggRank = ggRank;
		this.ggSaleRank = null;
		this.iosSpan = null;
		this.ggSpan = null;
		this.sortnum = 0;
	}

	public AppGameItem(int rank, String title, String right) {
		this.rank = rank;
		this.title = title;
		this.right = right;
	}

	public int getRank() {
		return rank;
	}

	public String getTitle() {
		return title;
	}

	public String getRight() {
		return right;
	}

	public String getiosRank() {
		return iosRank;
	}

	public String getggRank() {
		return ggRank;
	}

	public String getiosSaleRank() {
		return iosSaleRank;
	}

	public String getggSaleRank() {
		return ggSaleRank;
	}

	public int getsortnum() {
		return sortnum;
	}

	public void setTitle(String t) {
		title = t;
	}

	public void setRank(int ra) {
		rank = ra;
	}

	public void setiosSaleRank(String sr) {
		iosSaleRank = sr;
	}

	public void setiosSpan(String sp) {
		iosSpan = sp;
	}

	public void setggSaleRank(String sr) {
		ggSaleRank = sr;
	}

	public void setggSpan(String sp) {
		ggSpan = sp;
	}

	public void setsortnum(int sn) {
		sortnum = sn;
	}

	public static ArrayList<AppGameItem> listIntegrater(ArrayList<AppGameItem> ios, ArrayList<AppGameItem> gg) {
		ArrayList<AppGameItem> list = new ArrayList<>();
		AppGameItem item;
		boolean itemExist = false;
		for (AppGameItem iositem : ios) {
			for (AppGameItem ggitem : gg) {
				if (iositem.getTitle().contains(ggitem.getTitle())
						|| ggitem.getTitle().contains(iositem.getTitle())) {
					item = new AppGameItem(iositem.getTitle(), iositem.getRight(), String.valueOf(iositem.getRank()),
							String.valueOf(ggitem.getRank()));
					list.add(item);
					itemExist = true;
					gg.remove(ggitem);
					break;
				}
			}
			if (!itemExist) {
				item = new AppGameItem(iositem.getTitle(), iositem.getRight(), String.valueOf(iositem.getRank()),
						"圏外");
				list.add(item);
			}
			itemExist = false;
		}

		for (AppGameItem ggitem : gg) {
			item = new AppGameItem(ggitem.getTitle(), ggitem.getRight(), "圏外",
					String.valueOf(ggitem.getRank()));
			list.add(item);
		}
		return list;
	}

	public static void createSortnum(ArrayList<AppGameItem> result) {
		boolean iosSNumeric;
		boolean ggSNumeric;
		boolean iosRNumeric;
		boolean ggRNumeric;
		int iSnum = 1001;
		int gSnum = 201;
		int iRnum = 51;
		int gRnum = 51;
		for (AppGameItem item : result) {
			iosSNumeric = item.getiosSaleRank().chars().allMatch(Character::isDigit);
			if (iosSNumeric) {
				iSnum = Integer.parseInt(item.getiosSaleRank());
			}
			ggSNumeric = item.getggSaleRank().chars().allMatch(Character::isDigit);
			if (ggSNumeric) {
				gSnum = Integer.parseInt(item.getggSaleRank());
			}
			iosRNumeric = item.getiosRank().chars().allMatch(Character::isDigit);
			if (iosRNumeric) {
				iRnum = Integer.parseInt(item.getiosRank());
			}
			ggRNumeric = item.getggRank().chars().allMatch(Character::isDigit);
			if (ggRNumeric) {
				gRnum = Integer.parseInt(item.getggRank());
			}
			item.setsortnum(iSnum + gSnum + iRnum + gRnum);
			iSnum = 1001;
			gSnum = 201;
			iRnum = 51;
			gRnum = 51;
		}
		for (AppGameItem item : result) {
			item.setTitle(item.getTitle().replaceAll("灣", "\\〜"));
		}
	}

	public String toString() {
		return "ランキング:" + rank + "位" + "\nタイトル:" + title + "		コピーライト:" + right
				+ "\niosランキング:" + iosRank + "	iosセールスランキング:" + iosSaleRank + "	ios順位変動:" + iosSpan
				+ "\nandroidランキング:" + ggRank + "		androidセールスランキング:" + ggSaleRank +
				"	android順位変動:" + ggSpan+ "\n"+ sortnum;

	}

	public static void main(String[] args) throws MalformedURLException { //起動用
		iosGameTopFreeRank iosApp = new iosGameTopFreeRank(
				"https://rss.itunes.apple.com/api/v1/jp/ios-apps/top-free/games/50/explicit.atom");
		ArrayList<AppGameItem> iositemList = iosApp.getAppStoreInfo();
		ArrayList<AppGameItem> ggitemList = new ArrayList<>();
		ggGameTopFreeRank ggApp = new ggGameTopFreeRank(ggitemList);
		ggApp.getAppStoreInfo("https://appget.com/c/app_ranking/android/free/");
		ggApp.getAppStoreInfo("https://appget.com/c/app_ranking/android/free/page/2/");
		ggApp.getAppStoreInfo("https://appget.com/c/app_ranking/android/free/page/3/");
		ArrayList<AppGameItem> resultitemList;
		resultitemList = listIntegrater(iositemList, ggitemList);
		AppGameSalesRankInfo iosSale = new AppGameSalesRankInfo("http://game-i.daa.jp/?リアルタイムiOSセルランTOP1000",
				resultitemList, 0);
		iosSale.infoReader();
		AppGameSalesRankInfo ggSale = new AppGameSalesRankInfo("http://game-i.daa.jp/?GooglePlayアプリ最新セールスランキング",
				resultitemList, 1);
		ggSale.infoReader();
		createSortnum(resultitemList);
		itemQuickSort sort = new itemQuickSort(resultitemList);
		sort.sort();
		for (AppGameItem item : resultitemList) {
			System.out.println(item);
			System.out.println();
		}
	}

}
