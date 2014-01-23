package ac.jejunu.photify.entity;

public class LikeCount {
	private Summary summary;
	
	public Integer getTotalCount() {
		return summary.getTotal_count();
	}
	
	class Summary {
		private int total_count = 0;
		
		public int getTotal_count() {
			return total_count;
		}
	}
}
