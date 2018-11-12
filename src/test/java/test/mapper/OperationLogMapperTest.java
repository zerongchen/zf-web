package test.mapper;

import com.aotain.common.config.pagehelper.Page;
import com.aotain.zongfen.ZongfenApplication;
import com.aotain.zongfen.mapper.system.OperationLogMapper;
import com.aotain.zongfen.model.system.OperationLog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * Demo class
 *
 * @author daiyh@aotain.com
 * @date 2018/02/28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ZongfenApplication.class)
public class OperationLogMapperTest {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Test
    public void test() {
        OperationLog operationLog = new OperationLog();
        operationLog.setClientIp("12.1.1.10");
        operationLog.setClientPort(8080);
        operationLog.setDataJson("hello");
        operationLog.setInputParam("a");
        operationLog.setOperModel(1);
        operationLog.setOperTime(new Date());
        operationLog.setOperType(1);
        operationLog.setServerName("boye");
        operationLog.setOperUser("admin");

        operationLogMapper.insert(operationLog);
    }

//    @Test
    public void test2(){
        Page<OperationLog> page = new Page<OperationLog>();
        page.setPage(1);
        page.setPageSize(10);
        List<OperationLog> result = operationLogMapper.listOperationLog(page);
        System.out.println(page.getTotalPage()+"==="+page.getTotalRecord());
    }
}
