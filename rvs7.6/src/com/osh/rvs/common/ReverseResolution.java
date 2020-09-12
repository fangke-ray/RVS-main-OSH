package com.osh.rvs.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.log4j.Logger;

import com.osh.rvs.bean.master.CustomerEntity;
import com.osh.rvs.bean.master.ModelEntity;
import com.osh.rvs.bean.master.PartialEntity;
import com.osh.rvs.bean.master.PositionEntity;
import com.osh.rvs.mapper.master.CustomerMapper;
import com.osh.rvs.mapper.master.ModelMapper;
import com.osh.rvs.mapper.master.PartialMapper;
import com.osh.rvs.mapper.master.PositionMapper;

import framework.huiqing.common.mybatis.SqlSessionFactorySingletonHolder;
import framework.huiqing.common.util.CommonStringUtil;

public class ReverseResolution {
	protected static final Logger logger = Logger.getLogger("Production");

	public static Map<String, String> positionRever = new HashMap<String, String>();

	public static Map<String, String> modelRever = new HashMap<String, String>();
	public static Map<String, String> itemCodeRever = new HashMap<String, String>();

	public static Map<String, ModelEntity> modelEntityRever = new HashMap<String, ModelEntity>();

	public static Map<String, String> partialRever = new HashMap<String, String>();

	public static Map<String, PartialEntity> partialEntityRever = new HashMap<String, PartialEntity>(); // TODO TOClearIT

	public static Map<String, String> customerRever = new HashMap<String, String>();

	public static String getModelByName(String model_name, SqlSession conn) {
		boolean ownConn = false;
		if (conn == null) {
			conn = getTempConn();
			ownConn = true;
		}
		if (!modelRever.containsKey(model_name)) {
			ModelMapper dao = conn.getMapper(ModelMapper.class);
			ModelEntity mEntity = new ModelEntity();
			mEntity.setName(model_name);
			List<String> lResult = dao.checkModelByName(mEntity);
			String model_id = null; 
			if (lResult.size() > 0) {
				model_id = lResult.get(0);
			} else {
				model_id = dao.getModelByName(RvsUtils.regfy(model_name));
			}

			if (model_id != null) {
				modelRever.put(model_name, model_id);
			}
		}
		if (ownConn) {
			logger.info("Connnection close");
			conn.close();
			conn = null;
		}
		if (modelRever.get(model_name) != null) {
			return modelRever.get(model_name);
		} else {
			return getModelByItemCode(model_name, conn);
		}
	}

	public static String getModelByItemCode(String model_name, SqlSession conn) {
		boolean ownConn = false;
		if (conn == null) {
			conn = getTempConn();
			ownConn = true;
		}
		if (!itemCodeRever.containsKey(model_name)) {
			ModelMapper dao = conn.getMapper(ModelMapper.class);
			String model_id = dao.getModelByItemCode(model_name);

			if (model_id != null) {
				itemCodeRever.put(model_name, model_id);
			}
		}
		if (ownConn) {
			logger.info("Connnection close");
			conn.close();
			conn = null;
		}
		return itemCodeRever.get(model_name);
	}

	public static ModelEntity getModelEntityByName(String model_name, SqlSession conn) {
		if (modelEntityRever.containsKey(model_name)) {
			return modelEntityRever.get(model_name);
		}

		String model_id = getModelByName(model_name, conn);
		if (model_id == null) return null;

		ModelMapper dao = conn.getMapper(ModelMapper.class);
		ModelEntity modelEntity = dao.getModelByID(model_id);
		if (modelEntity == null) {
			return null;
		} else {
			modelEntityRever.put(model_name, modelEntity);
			return modelEntityRever.get(model_name);
		}
	}

	public static String getPositionByProcessCode(String process_code, SqlSession conn) {
		if (CommonStringUtil.isEmpty(process_code)) return null;

		if (!positionRever.containsKey(process_code)) {
			boolean ownConn = false;
			if (conn == null) {
				conn = getTempConn();
				ownConn = true;
			}

			PositionEntity condition = new PositionEntity();
			condition.setProcess_code(process_code);
			PositionMapper dao = conn.getMapper(PositionMapper.class);
			List<PositionEntity> position = dao.searchPosition(condition);
			if (position.size() > 0) {
				positionRever.put(process_code, position.get(0).getPosition_id());
				logger.info("Get " + process_code + " is :" + position.get(0).getPosition_id());
			}

			if (ownConn) {
				logger.info("Connnection close");
				conn.close();
				conn = null;
			}
		}
		return positionRever.get(process_code);
	}

	public static SqlSession getTempConn() {
		logger.info("new Connnection");
		@SuppressWarnings("static-access")
		SqlSessionFactory factory = SqlSessionFactorySingletonHolder.getInstance().getFactory();
		return factory.openSession(TransactionIsolationLevel.READ_COMMITTED);
	}

	public static String getPartialByCode(String code, SqlSession conn) {
		if (CommonStringUtil.isEmpty(code)) return null;
		if (partialRever.containsKey(code)) return partialRever.get(code);

		PartialEntity partialEntity = new PartialEntity();
		partialEntity.setCode(code);

		PartialMapper partialDao = conn.getMapper(PartialMapper.class);

		List<String> partialIDList = partialDao.checkPartial(partialEntity);
		if (partialIDList.size() == 0) {
			return null;
		} else {
			partialRever.put(code, partialIDList.get(0));
			return partialRever.get(code);
		}
	}

	public static PartialEntity getPartialEntityByCode(String code, SqlSession conn) {
		if (CommonStringUtil.isEmpty(code)) return null;
		if (partialEntityRever.containsKey(code)) return partialEntityRever.get(code);

		PartialMapper partialDao = conn.getMapper(PartialMapper.class);

		List<PartialEntity> partialList = partialDao.getPartialByCode(code);
		if (partialList.size() == 0) {
			return null;
		} else {
			partialEntityRever.put(code, partialList.get(0));
			return partialEntityRever.get(code);
		}
	}

	public static String getCustomerByName(String name, SqlSession conn) {
		if (CommonStringUtil.isEmpty(name)) return null;
		boolean ownConn = false;
		if (conn == null) {
			conn = getTempConn();
			ownConn = true;
		}
		if (!customerRever.containsKey(name)) {
			CustomerEntity condition = new CustomerEntity();
			condition.setName(name);
			CustomerMapper dao = conn.getMapper(CustomerMapper.class);
			List<CustomerEntity> customer = dao.searchCustomer(condition);
			if (customer.size() > 0) {
				customerRever.put(name, customer.get(0).getCustomer_id());
				logger.info("Get " + name + " is :" + customer.get(0).getCustomer_id());
			}
		}
		if (ownConn) {
			logger.info("Connnection close");
			conn.close();
			conn = null;
		}
		return customerRever.get(name);
	}

	public static void clearModelEntityRever() {
		modelEntityRever.clear();
	}
}
