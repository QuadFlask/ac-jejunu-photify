package ac.jejunu.photify.command;



public class TestCommand {

	int articleid;
	String title;

	public int getArticleid() {
		return articleid;
	}
	public void setArticleid(int articleid) {
		this.articleid = articleid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "TestCommand [articleid=" + articleid + ", title=" + title + "]";
	}

}
