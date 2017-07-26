package nl.idgis.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import nl.idgis.QueryBuilderOld;

@RunWith(SpringRunner.class)
@WebMvcTest(value = Controller.class, secure = false)
public class ControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private QueryBuilderOld builder;
	
	/**
	 * Test whether an invalid format type returns an error message or not.
	 * 
	 * @throws Exception
	 */
	@Test
	public void retrieveBadFormatType() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/rest/services/test/FeatureServer")
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = "{error:Invalid format type. Can only return JSON!}";
		
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
		
		requestBuilder = MockMvcRequestBuilders
				.get("/rest/services/test/FeatureServer/0")
				.accept(MediaType.APPLICATION_JSON);
		
		result = mockMvc.perform(requestBuilder).andReturn();
		
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}
	
	/**
	 * Test whether a request for json of the feature server returns a valid json
	 * 
	 * @throws Exception
	 */
	@Test
	public void retrieveFeatureServerMetadata() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/rest/services/test/FeatureServer?f=json")
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = "{currentVersion:10.51}";
		
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}
	
	/**
	 * Test whether a request for json of the feature layer returns a valid json
	 * 
	 * @throws Exception
	 */
	@Test
	public void retrieveFeatureLayerMetadata() throws Exception {
		RequestBuilder requestBuilder = MockMvcRequestBuilders
				.get("/rest/services/test/FeatureServer/0?f=json")
				.accept(MediaType.APPLICATION_JSON);
		
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		String expected = "{currentVersion:10.51}";
		
		JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
	}
}
