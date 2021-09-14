package org.apache.iotdb.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.admin.common.exception.BaseException;
import org.apache.iotdb.admin.common.exception.ErrorCode;
import org.apache.iotdb.admin.mapper.RoleMapper;
import org.apache.iotdb.admin.model.dto.IotDBRole;
import org.apache.iotdb.admin.model.entity.Role;
import org.apache.iotdb.admin.model.vo.RoleVO;
import org.apache.iotdb.admin.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public void upsertRoleInfo(String host, Integer port, IotDBRole iotDBRole) throws BaseException {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("host", host);
        wrapper.eq("port", port);
        wrapper.eq("name", iotDBRole.getRoleName());
        try {
            Role role = roleMapper.selectOne(wrapper);
            int flag = 0;
            if (role == null) {
                role = new Role();
                role.setDescription(iotDBRole.getDescription());
                role.setHost(host);
                role.setPort(port);
                role.setName(iotDBRole.getRoleName());
                flag = roleMapper.insert(role);
            } else {
                role.setDescription(iotDBRole.getDescription());
                flag = roleMapper.updateById(role);
            }
            if (flag == 0) {
                throw new BaseException(ErrorCode.UPSERT_ROLE_INFO_FAIL, ErrorCode.UPSERT_ROLE_INFO_FAIL_MSG);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ErrorCode.UPSERT_ROLE_INFO_FAIL, ErrorCode.UPSERT_ROLE_INFO_FAIL_MSG);
        }
    }

    @Override
    public void deleteRoleInfo(String host, Integer port, String roleName) throws BaseException {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("host", host);
        wrapper.eq("port", port);
        wrapper.eq("name", roleName);
        try {
            roleMapper.delete(wrapper);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ErrorCode.DELETE_ROLE_INFO_FAIL, ErrorCode.DELETE_ROLE_INFO_FAIL_MSG);
        }
    }

    @Override
    public Role getRoleInfo(String host, Integer port, String roleName) throws BaseException {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.eq("host", host);
        wrapper.eq("port", port);
        wrapper.eq("name", roleName);
        try {
            Role role = roleMapper.selectOne(wrapper);
            return role;
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(ErrorCode.GET_ROLE_INFO_FAIL, ErrorCode.GET_ROLE_INFO_FAIL_MSG);
        }
    }
}
