package ac.jejunu.photify.rest;

import org.springframework.http.converter.StringHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = ServerIpAddress.IP, converters = { StringHttpMessageConverter.class })
public interface ReadArticleClient {
	
	@Get("/getArticleList/{orderby}.photo?no={no}&limit={limit}")
	public String readArticleList(String orderby, int no, int limit);
	
	@Get("/getArticleList/around.photo?lat={lat}&lng={lng}&limit=10")
	public String readArticleList(int lat, int lng);
	
}
