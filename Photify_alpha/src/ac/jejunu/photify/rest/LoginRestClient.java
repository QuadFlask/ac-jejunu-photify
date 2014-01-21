package ac.jejunu.photify.rest;

import org.springframework.http.converter.FormHttpMessageConverter;

import com.googlecode.androidannotations.annotations.rest.Accept;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import com.googlecode.androidannotations.api.rest.MediaType;

@Rest(rootUrl = "http://113.198.164.111:8080", converters = { FormHttpMessageConverter.class })
// StringHttpMessageConverter, ByteArrayHttpMessageConverter.class,
// FormHttpMessageConverter.class,GsonHttpMessageConverter.class
public interface LoginRestClient {
	
	@Post("/login.photo")
	@Accept(MediaType.APPLICATION_JSON)
	String login(String fbid);

	@Get("/checkreg.photo?fbid={fbid}")
	Boolean isRegistered(String fbid);
}