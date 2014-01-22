package ac.jejunu.photify.entity;

public class ArticleCommand {

	private String id; // facebook 에 등록후 받아온 게시글 ID. client 에서 전송할 필요 없음
	private int likecnt; // db 에서 읽어오는 값
	private String regdttm; // db 에서 읽어오는 값

	private String fbid;

	private int lat, lng;

	private String content;

	private String attachPath;

	private int avgColor = 0xffffff;

	public int getLikecnt() {
		return likecnt;
	}

	public void setLikecnt(int likecnt) {
		this.likecnt = likecnt;
	}

	public String getRegdttm() {
		return regdttm;
	}

	public void setRegdttm(String regdttm) {
		this.regdttm = regdttm;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFbid() {
		return fbid;
	}

	public void setFbid(String fbid) {
		this.fbid = fbid;
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

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAttachPath() {
		return attachPath;
	}

	public void setAttachPath(String attachPath) {
		this.attachPath = attachPath;
	}

	public int getAvgColor() {
		return avgColor;
	}

	public void setAvgColor(int avgColor) {
		this.avgColor = avgColor;
	}

}