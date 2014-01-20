package ac.jejunu.photify.entity;

public class Article {
	int no;
	int articleid;
	String title;
	String photoUrl;
	String fbid;
	String writer;
	String contents;

	int lat, lng;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

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

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getFbid() {
		return fbid;
	}

	public void setFbid(String fbid) {
		this.fbid = fbid;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public int getLat() {
		return lat;
	}

	public void setLat(int lat) {
		this.lat = lat;
	}

	public int getLng() {
		return lng;
	}

	public void setLng(int lng) {
		this.lng = lng;
	}

	@Override
	public String toString() {
		return "TArticle [articleid=" + articleid + ", title=" + title + ", photoUrl=" + photoUrl + ", fbid=" + fbid + ", writer=" + writer + ", contents="
				+ contents + ", lat=" + lat + ", lng=" + lng + "]";
	}
}