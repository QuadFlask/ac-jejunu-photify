package ac.jejunu.photify.command;

public class ArticleCommand {
	int from, count;

	public ArticleCommand(int from, int count) {
		this.from = from;
		this.count = count;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}


}