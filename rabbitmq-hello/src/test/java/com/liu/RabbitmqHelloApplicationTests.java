package com.liu;

import com.liu.productor.SenderA;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class RabbitmqHelloApplicationTests {

	@Autowired
	private SenderA sender;

	@Test
	public void hello() throws Exception {
		sender.send("sss");
	}

}
