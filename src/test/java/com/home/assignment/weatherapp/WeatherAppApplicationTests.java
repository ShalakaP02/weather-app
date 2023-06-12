package com.home.assignment.weatherapp;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@RunWith(SpringRunner.class)
@SpringBootTest(classes = WeatherAppApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
class WeatherAppApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@Order(1)
	void contextLoads() throws Exception {
		ResultActions response = mockMvc.perform(get("/weather").accept(APPLICATION_JSON));

		response.andExpect(status().isOk())
				.andExpect(content().contentType(APPLICATION_JSON))
				//.andExpect(jsonPath("$.city", is("Pune")))
				.andExpect(jsonPath("$.city", notNullValue()))
				.andDo(print());
	}

	@Test
	@Order(2)
	public void getWeatherDataByIpITTest() throws Exception {
		ResultActions response = mockMvc.perform(get("/weather/" + "103.208.69.96"));
		response.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(greaterThan(0))));

	}



}
