package ac.jejunu.photify.rest;

import org.springframework.http.converter.StringHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = ServerIpAddress.IP, converters = { StringHttpMessageConverter.class })
public interface ReadArticleClient {

	@Get("/getArticleList/{orderby}.photo")
	public String readArticleList(String orderby);

}
