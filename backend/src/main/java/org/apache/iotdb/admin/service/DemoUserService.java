package org.apache.iotdb.admin.service;

import org.apache.iotdb.admin.model.entity.DemoUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 目的在提供编程的例子，你可以删除Demo开始的文件，不会报错。
 *
 * @author fanli
 */
public interface DemoUserService extends IService<DemoUser> {

    /**
     * 通过mapper xml的sql方式来查询
     *
     * @return 查询结果
     */
    List<DemoUser> selectByXml();
}
