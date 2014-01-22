package ac.jejunu.photify.rest;

import org.springframework.http.converter.StringHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Rest;

@Rest(rootUrl = "http://graph.facebook.com", converters = { StringHttpMessageConverter.class })
public interface ReadFacebookArticleClient {
	
	@Get("/{postId}")
	public String getArticle(String postId);

}
