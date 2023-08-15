package com.osh.rvs.service.partial;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.struts.action.ActionForm;

import com.osh.rvs.bean.partial.ConsumableApplicationDetailEntity;
import com.osh.rvs.bean.partial.MaterialPartialDetailEntity;
import com.osh.rvs.form.partial.ConsumableApplicationForm;
import com.osh.rvs.mapper.partial.ConsumableApplicationDetailMapper;
import com.osh.rvs.mapper.partial.MaterialConsumableDetailMapper;
import com.osh.rvs.mapper.partial.MaterialPartialMapper;
import com.osh.rvs.mapper.partial.PartialReceptMapper;

import framework.huiqing.common.util.copy.BeanUtil;
import framework.huiqing.common.util.copy.CopyOptions;

public class ConsumableApplicationDetailService {
	public List<ConsumableApplicationForm> search(ActionForm form, SqlSession conn){
		
		
		ConsumableApplicationForm consumableApplicationForm =(ConsumableApplicationForm)form;
		
		ConsumableApplicationDetailMapper dao = conn.getMapper(ConsumableApplicationDetailMapper.class);
		List<ConsumableApplicationDetailEntity> consumableApplicationDetailEntityList = dao.serach(consumableApplicationForm.getConsumable_application_key());
		
		List<ConsumableApplicationForm>  list = new ArrayList<ConsumableApplicationForm>();
		BeanUtil.copyToFormList(consumableApplicationDetailEntityList, list, CopyOptions.COPYOPTIONS_NOEMPTY, ConsumableApplicationForm.class);
		
		return list;
	}

	public List<MaterialPartialDetailEntity> searchCcdAdvanced(String material_id, SqlSession conn) {
		ConsumableApplicationDetailMapper mapper = conn.getMapper(ConsumableApplicationDetailMapper.class);

		return mapper.searchCcdAdvanced(material_id);
	}


	/**
	 * 将投线前使用的消耗品对应到订购单中的零件进行签收
	 * @param material_id
	 * @param conn
	 */
	public void setConsumables2OrderParts(String material_id, SqlSessionManager conn) {
		MaterialConsumableDetailMapper cnsbMapper = conn.getMapper(MaterialConsumableDetailMapper.class);

		// 消耗品使用记录
		List<MaterialPartialDetailEntity> listCnsb = cnsbMapper.searchForMaterialWithLine(material_id, "00000000011");

		if (listCnsb.size() == 0) {
			return;
		}

		MaterialPartialMapper mpMapper = conn.getMapper(MaterialPartialMapper.class);
		MaterialPartialDetailEntity condition = new MaterialPartialDetailEntity();
		// 零件订购单记录
		condition.setMaterial_id(material_id);
		List<MaterialPartialDetailEntity> listOrd = mpMapper.searchMaterialPartialDetail(condition);

		if (listOrd.size() == 0) {
			return;
		}

		PartialReceptMapper rcptMapper = conn.getMapper(PartialReceptMapper.class);	

		for (MaterialPartialDetailEntity cnsbEntity : listCnsb) {
			for (MaterialPartialDetailEntity ordEntity : listOrd) {
				if (cnsbEntity.getPartial_id().equals(ordEntity.getPartial_id())) {
					if (ordEntity.getRecent_signin_time() == null) {
						int waiting_receive_quantity = ordEntity.getQuantity() - ordEntity.getCur_quantity();
						if (waiting_receive_quantity > 0) {
							int recept_quantity = 0;
							if (cnsbEntity.getCur_quantity() > waiting_receive_quantity) {
								recept_quantity = waiting_receive_quantity;
								cnsbEntity.setRecept_quantity(cnsbEntity.getCur_quantity() - waiting_receive_quantity);
								cnsbMapper.updateQuantity(cnsbEntity);
							} else {
								recept_quantity = cnsbEntity.getCur_quantity();
								cnsbMapper.remove(cnsbEntity);
							}
							ordEntity.setR_operator_id(cnsbEntity.getR_operator_id());
							ordEntity.setRecent_receive_time(cnsbEntity.getRecent_signin_time());
							ordEntity.setRecept_quantity(recept_quantity);
							rcptMapper.updatePartialRecept(ordEntity);
						}
						break;
					}
				}
			}
		}
	}
}
