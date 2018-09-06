package com.osh.rvs.mapper.manage;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.osh.rvs.bean.data.ProductionFeatureEntity;
import com.osh.rvs.bean.master.PcsFixOrderEntity;


public interface PcsFixOrderMapper {

	/** 建立修改申请 */
	public int createPcsFixOrder(PcsFixOrderEntity entity) throws Exception;

	/** 解决修改申请 */
	public int resolvePcsFixOrder(PcsFixOrderEntity entity) throws Exception; 

	public PcsFixOrderEntity getPcsFixOrder(String pcs_fix_order_key);

	public List<PcsFixOrderEntity> searchPcsFixOrders(PcsFixOrderEntity condition);

	public void updateInputs(ProductionFeatureEntity entity) throws Exception;

	public void updateInputsLeader(@Param("leader_pcs_key") String jam_code, @Param("pcs_inputs") String pcs_inputs) throws Exception;

	public void updateComments(ProductionFeatureEntity entity) throws Exception;

	public void updateCommentsLeader(@Param("leader_pcs_key") String jam_code, @Param("pcs_comments") String pcs_comments) throws Exception;
}
