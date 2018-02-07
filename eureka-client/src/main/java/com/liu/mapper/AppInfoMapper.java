package com.liu.mapper;

import com.liu.model.AppInfo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

@Component(value = "AppInfoMapper")
public interface AppInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(AppInfo record);

    int insertSelective(AppInfo record);

    AppInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(AppInfo record);

    int updateByPrimaryKeyWithBLOBs(AppInfo record);

    int updateByPrimaryKey(AppInfo record);

    int batchUpdataData(@Param("list")List<AppInfo> appInfos);
}