package com.osh.rvs.mapper.manage;

import java.util.List;

import com.osh.rvs.bean.manage.UserDefineCodesEntity;


public interface UserDefineCodesMapper {
	
	/*查询用户定义数值*/
    public List<UserDefineCodesEntity> searchUserDefineCodes();
    
    /*更新用户定义 设定值*/
    public void updateUserDefineCodes(UserDefineCodesEntity userDefineCodesEntity);
    
    /*根据传递的code查询出value*/
    public String searchUserDefineCodesValueByCode(String code);
}
