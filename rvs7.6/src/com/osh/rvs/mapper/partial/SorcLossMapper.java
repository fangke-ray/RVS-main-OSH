package com.osh.rvs.mapper.partial;

import java.util.List;

import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.bean.partial.SorcLossEntity;


public interface SorcLossMapper {

  //查询SORC损金表的详细数据
  public List<SorcLossEntity> searchSorcLoss(SorcLossEntity sorcLossEntity);
  
  //查询维修对象损金详细数据
  public List<SorcLossEntity> searchSorcLossOfRepair(SorcLossEntity sorcLossEntity);
  
  //查询月损金数据
  public List<SorcLossEntity> searchSorcLossMonth(SorcLossEntity sorcLossEntity);

  //插入SORC损金表数据
  public void insertSorcLoss(SorcLossEntity sorcLossEntity);

  //更新SORC损金表数据
  public void updateSorcLoss(SorcLossEntity sorcLossEntity);

  //更新material_partial_detail表数据
  public void updateMaterialPartialDetail(MaterialPartialDetailEntity materialPartialDetailEntity);
  
  //查询出损金详细的所有不良简述
  public List<SorcLossEntity> searchLossDetailOfNogoodDescription();
  
  //更新不良简述
  public void updateNogoodDescription(SorcLossEntity sorcLossEntity);
}
