package com.imooc.service.impl;

import com.imooc.enums.YesOrNoEnum;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserAddressMapper userAddressMapper;
    @Autowired
    Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<UserAddress> queryUserAllAddress(String userId) {

        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);

        return userAddressMapper.select(userAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void addNewUserAddress(AddressBO addressBO) {

        String addressId = sid.nextShort();

        // 1. 判断当前用户是否存在地址，如果没有，则新增为‘默认地址’
        Integer isDefault = 0;
        List<UserAddress> userAddressList = this.queryUserAllAddress(addressBO.getUserId());
        if (userAddressList == null || userAddressList.isEmpty() || userAddressList.size() == 0) {
            isDefault = 1;
        }

        // 2. 保存地址到数据库
        UserAddress newUserAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, newUserAddress);
        newUserAddress.setId(addressId);
        newUserAddress.setIsDefault(isDefault);
        newUserAddress.setCreatedTime(new Date());
        newUserAddress.setUpdatedTime(new Date());

        userAddressMapper.insert(newUserAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserAddress(AddressBO addressBO) {

        UserAddress updateUserAddress = new UserAddress();
        BeanUtils.copyProperties(addressBO, updateUserAddress);
        updateUserAddress.setId(addressBO.getAddressId());
        updateUserAddress.setUpdatedTime(new Date());

        userAddressMapper.updateByPrimaryKeySelective(updateUserAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserAddress(String userId, String addressId) {

        UserAddress deleteUserAddress = new UserAddress();
        deleteUserAddress.setUserId(userId);
        deleteUserAddress.setId(addressId);

        userAddressMapper.delete(deleteUserAddress);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void setDefaultUserAddress(String userId, String addressId) {

        // 1. 查找默认地址，设置为不默认
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        userAddress.setIsDefault(YesOrNoEnum.YES.type);
        List<UserAddress> userAddressList = userAddressMapper.select(userAddress);
        for (UserAddress ua: userAddressList) {
            ua.setIsDefault(YesOrNoEnum.No.type);
            userAddressMapper.updateByPrimaryKeySelective(ua);
        }

        // 2. 根据地址id修改为默认的地址
        UserAddress defaultUserAddress = new UserAddress();
        defaultUserAddress.setUserId(userId);
        defaultUserAddress.setId(addressId);
        defaultUserAddress.setIsDefault(YesOrNoEnum.YES.type);
        userAddressMapper.updateByPrimaryKeySelective(defaultUserAddress);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public UserAddress queryUserAddress(String userId, String addressId) {

        UserAddress singleUserAddress = new UserAddress();
        singleUserAddress.setUserId(userId);
        singleUserAddress.setId(addressId);

        return userAddressMapper.selectOne(singleUserAddress);
    }
}
