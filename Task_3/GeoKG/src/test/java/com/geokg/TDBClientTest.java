package com.geokg;

import com.geokg.utlis.TDBClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class TDBClientTest {
    @Test
    public void testac() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        TDBClient s = ac.getBean(TDBClient.class);
        System.out.println(s.getOwlIRI());
    }
}