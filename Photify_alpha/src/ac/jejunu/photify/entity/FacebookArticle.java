package ac.jejunu.photify.entity;

// new GsonBuilder().setPrettyPrinting().create();	
public class FacebookArticle {
	private String id, created_time, icon, name, picture, source;
	private User from;
	private Image[] images;
	private int width, height;
	private Comments comments;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}

	public Image[] getImages() {
		return images;
	}

	public void setImages(Image[] images) {
		this.images = images;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Comments getComments() {
		return comments;
	}

	public void setComments(Comments comments) {
		this.comments = comments;
	}

	public class User {
		private String name, id;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	}

	public class Image {
		private int width, height;
		private String source;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public String getSource() {
			return source;
		}

		public void setSource(String source) {
			this.source = source;
		}
	}

	public class Comments {
		private Comment data[];

		public Comment[] getData() {
			return data;
		}

		public void setData(Comment[] data) {
			this.data = data;
		}
	}

	public class Comment {
		private String id, created_time, message;
		private int like_count;
		private User from;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getCreated_time() {
			return created_time;
		}

		public void setCreated_time(String created_time) {
			this.created_time = created_time;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public int getLike_count() {
			return like_count;
		}

		public void setLike_count(int like_count) {
			this.like_count = like_count;
		}

		public User getFrom() {
			return from;
		}

		public void setFrom(User from) {
			this.from = from;
		}
	}
}
