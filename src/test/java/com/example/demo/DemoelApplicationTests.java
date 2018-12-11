package com.example.demo;

import com.example.demo.bean.Movie;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoelApplicationTests {

	@Test
	public void contextLoads() {

		Movie movie = new Movie();
		movie.setId(1);
		movie.setName("海王");
		movie.setPrice(35);

	}

}
