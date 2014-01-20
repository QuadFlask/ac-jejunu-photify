package ac.jejunu.photify.rest;

import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import ac.jejunu.photify.entity.SampleEntity;

@Rest(rootUrl = "http://113.198.164.145:8080/", converters = {FormHttpMessageConverter.class, GsonHttpMessageConverter.class})
public interface MyRestClient {
	@Post("/jsontest.photo")
	SampleEntity getSampleEntity();
}
