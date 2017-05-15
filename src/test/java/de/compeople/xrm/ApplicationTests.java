package de.compeople.xrm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class ApplicationTests {

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ObjectMapper mapper;
    private MockMvc mvc;

    private String customerV1;
    private String customerV2;

    @Before
    public void setup() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Ralf Stuckert");
        map.put("_links", selfLink("http://localhost/customers/17"));
        customerV1 = mapper.writeValueAsString(map);

        map.clear();
        map.put("firstName", "Ralf");
        map.put("lastName", "Stuckert");
        map.put("_links", selfLink("http://localhost/customers/17"));
        customerV2 = mapper.writeValueAsString(map);
    }

    private Map<String, Object> selfLink(String href) {
        return Collections.singletonMap("self", Collections.singletonMap("href", href));
    }

    @Test
    public void getCustomerV2() throws Exception {
        this.mvc.perform(get("/customers/17").accept("application/vnd.customer.v2+json")).andDo(print())
                .andExpect(status().is2xxSuccessful()).andExpect(content().json(customerV2));
    }

    @Test
    public void getCustomerV1() throws Exception {
        this.mvc.perform(get("/customers/17").accept("application/vnd.customer.v1+json")).andDo(print())
                .andExpect(status().is2xxSuccessful()).andExpect(content().json(customerV1));
    }

}
