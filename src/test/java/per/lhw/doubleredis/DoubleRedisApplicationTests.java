package per.lhw.doubleredis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import per.lhw.doubleredis.redis.PrimaryRedisService;
import per.lhw.doubleredis.redis.SecondaryRedisService;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DoubleRedisApplicationTests {

    @Autowired
    private PrimaryRedisService primaryRedisService;

    @Autowired
    private SecondaryRedisService secondaryRedisService;

    @Test
    public void contextLoads() {
    }

    @Test
    public void primaryToSecondary() throws Exception {
        Map<String, String> map = primaryRedisService.multiGet("");
        secondaryRedisService.multiSet(map);
    }

    @Test
    public void primaryOneToSecondaryOne() throws Exception {
        primaryRedisService.set("2", "222");
        secondaryRedisService.set("2", "222");
    }
}
