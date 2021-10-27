import java.util.ArrayList;

public class itemQuickSort {
	private ArrayList<AppGameItem> list = new ArrayList<>();

	public itemQuickSort(ArrayList<AppGameItem> list) {
		this.list = list;
	}

	int pivot(int i, int j) {
		int k = i + 1;
		while (k <= j && list.get(i).getsortnum() == list.get(k).getsortnum())
			k++;
		if (k > j)
			return -1;
		if (list.get(i).getsortnum() >= list.get(k).getsortnum())
			return i;
		return k;
	}

	private int partition(int i, int j,int x) {
		int l = i, r = j;
		while (l <= r) {
			while (l <= j && list.get(l).getsortnum() < x)
				l++;
			while (r >= i && x <= list.get(r).getsortnum())
				r--;
			if (l > r)
				break;
			AppGameItem t = list.get(l);
			list.set(l, list.get(r));
			list.set(r, t);
			l++;
			r--;
		}

		return l;
	}

	private void quicksort(int i, int j) {
		if (i == j)
			return;
		int p = pivot(i, j);
		if (p != -1) {
			int k=partition(i,j, list.get(p).getsortnum());
			quicksort(i, k - 1);
			quicksort(k, j);
		}
	}

	public void sort() {
		quicksort(0, list.size() - 1);
		int i = 1;
		for (AppGameItem item : list) {
			item.setRank(i);
			i++;
		}
	}
}
