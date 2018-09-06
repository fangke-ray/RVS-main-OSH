package com.osh.rvs.action.pda;

import java.util.List;

import framework.huiqing.action.BaseAction;
import framework.huiqing.bean.message.MsgInfo;
import framework.huiqing.common.util.CommonStringUtil;

/**
 * 
 * @Title PdaApplyAction.java
 * @Project rvs
 * @Package com.osh.rvs.action.pda
 * @ClassName: PdaApplyAction
 * @Description: TODO
 * @author lxb
 * @date 2015-5-29 上午10:48:08
 */
public class PdaBaseAction extends BaseAction {

	public String getStrMsgInfo(List<MsgInfo> errors) {
		StringBuffer msgInfo = new StringBuffer();
		for (int i = 0; i < errors.size(); i++) {
			MsgInfo error = errors.get(i);
			if (!CommonStringUtil.isEmpty(error.getErrmsg())) {
				if (i >= 1) {
					msgInfo.append("\\n");
				}
				msgInfo.append(error.getErrmsg());
			}
		}
		return msgInfo.toString();
	}
}
