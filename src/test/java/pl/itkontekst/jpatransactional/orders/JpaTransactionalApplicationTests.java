package pl.itkontekst.jpatransactional.orders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class JpaTransactionalApplicationTests {

        @Autowired
        private OrdersDAO ordersDAO;

        @Autowired
        private OrdersService ordersService;
    
        @Autowired
        private EventLogDAO eventLogDAO;
    
	@Test
	public void contextLoads() {
	}


}
