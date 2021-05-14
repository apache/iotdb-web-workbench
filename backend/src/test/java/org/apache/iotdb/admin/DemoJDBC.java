package org.apache.iotdb.admin;

import org.apache.iotdb.admin.mapper.DemoUserMapper;
import org.apache.iotdb.admin.model.entity.DemoUser;
import java.util.List;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * ${DESCRIPTION}
 *
 * @author fanli
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DemoJDBC {

    @Resource
    private DemoUserMapper demoUserMapper;

    @Test
    public void testSelect() {
        System.out.println(("----- selectAll method test ------"));
        List<DemoUser> userList = demoUserMapper.selectList(null);
        //Assert.assertEquals(5, userList.size());
        userList.forEach(System.out::println);
    }
}
