package com.aotain.zongfen.service.apppolicy.paramset;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.aotain.common.policyapi.base.BaseService;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.CommonCookie;
import com.aotain.common.policyapi.model.CommonParameterStrategy;
import com.aotain.common.policyapi.model.base.BaseVO;
import com.aotain.common.utils.constant.RedisKeyConstant;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.zongfen.exception.ImportException;
import com.aotain.zongfen.log.constant.DataType;
import com.aotain.zongfen.log.constant.OperationType;
import com.aotain.zongfen.mapper.apppolicy.CommonCookieMapper;
import com.aotain.zongfen.mapper.apppolicy.CommonSearchMapper;
import com.aotain.zongfen.mapper.apppolicy.CommonThresholdMapper;
import com.aotain.zongfen.mapper.policy.PolicyMapper;
import com.aotain.zongfen.mapper.policy.PolicyStatusMapper;
import com.aotain.zongfen.model.apppolicy.CommonSearch;
import com.aotain.zongfen.model.apppolicy.CommonThreshold;
import com.aotain.zongfen.model.common.BaseKeys;
import com.aotain.zongfen.model.common.ResponseResult;
import com.aotain.zongfen.model.policy.Policy;
import com.aotain.zongfen.model.policy.PolicyStatus;
import com.aotain.zongfen.service.common.CommonService;
import com.aotain.zongfen.utils.ExcelUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.CookieImportMgr;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

@Service
@Transactional(propagation=Propagation.REQUIRED,rollbackFor={Exception.class})
public class ParamSetServiceImpl extends BaseService implements ParamSetService {

	@Autowired
	private CommonCookieMapper commonCookieMapper;
	
	@Autowired
	private CommonSearchMapper commonSearchMapper;
	
	@Autowired
	private CommonThresholdMapper commonThresholdMapper;
	
	@Autowired
	private PolicyMapper policyMapper;
	
	@Autowired
    private PolicyStatusMapper policyStatusMapper;
	
    @Autowired
    private CommonService commonService;
	
	@Override
	public ResponseResult<BaseKeys> saveAndSync(Integer webHitThreshold, Integer kwThreholds,Integer commonId) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		Policy record = new Policy();
		record.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		Long messageNo = policyMapper.getMaxMessageNoByType(record);
		CommonThreshold  commonThresholdDB = new CommonThreshold();
		CommonThreshold commonThresholdDB1 = commonThresholdMapper.getThreshold();
		if(commonThresholdDB1!=null) {
			commonThresholdDB = commonThresholdDB1;
		}
		commonThresholdDB.setModifyTime(new Date());
		commonThresholdDB.setKwThreholds(kwThreholds);
		commonThresholdDB.setWebHitThreshold(webHitThreshold);
		commonThresholdDB.setModifyOper(SpringUtil.getSysUserName());
		if(messageNo!=null && commonThresholdDB.getMessageNo()!=null) {
			commonThresholdDB.setMessageNo(messageNo);
			commonThresholdMapper.updateByPrimaryKeySelective(commonThresholdDB);
			key.setOperType(OperationType.MODIFY.getType());
		}else if(messageNo!=null && commonThresholdDB.getMessageNo()==null){
			commonThresholdDB.setMessageNo(messageNo);
			commonThresholdDB.setCreateOper(SpringUtil.getSysUserName());
			commonThresholdDB.setCreateTime(commonThresholdDB.getModifyTime());
			commonThresholdMapper.insert(commonThresholdDB);
			key.setOperType(OperationType.CREATE.getType());
		}else if(messageNo==null && commonThresholdDB.getMessageNo()==null) {
			commonThresholdDB.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.COMMON_PARAMETER_SET.getId()));
			commonThresholdDB.setCreateOper(SpringUtil.getSysUserName());
			commonThresholdDB.setCreateTime(commonThresholdDB.getModifyTime());
			commonThresholdMapper.insert(commonThresholdDB);
			record.setMessageName(MessageType.COMMON_PARAMETER_SET.name());
			record.setMessageSequenceno(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.COMMON_PARAMETER_SET.getId()));
			record.setMessageNo(commonThresholdDB.getMessageNo());
			record.setCreateTime(commonThresholdDB.getCreateTime());
			record.setCreateOper(SpringUtil.getSysUserName());
			record.setModifyOper(SpringUtil.getSysUserName());
			record.setModifyTime(commonThresholdDB.getCreateTime());
			record.setOperateType(1);
			policyMapper.insertSelective(record);
			key.setOperType(OperationType.CREATE.getType());
		}
		rediscluster.putHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()), "1");
		key.setMessageNo(commonThresholdDB.getMessageNo());
		key.setMessageName("通用参数：阈值设置");
		key.setMessageType(record.getMessageType());
		key.setDataType(DataType.POLICY.getType());
		keys.add(key);
		result.setResult(1);
		result.setKeys(keys);
		return result;
	}

	@Override
	public CommonThreshold getThreshold() {
		return commonThresholdMapper.getThreshold();
	}

	@Override
	public ResponseResult<BaseKeys> searchSave(String[] searchEngine, Long seId) {
		ResponseResult<BaseKeys> resultMes = new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		Policy record = new Policy();
		record.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		Long messageNo = policyMapper.getMaxMessageNoByType(record);
		List<CommonSearch>  result = commonSearchMapper.getAllSearch();
		if(messageNo!=null && result.size()>0) {
			if(seId!=null) {
				CommonSearch commonSearch = new CommonSearch();
				commonSearch.setSename(searchEngine[0]);
				commonSearch.setSeId(seId);
				commonSearch.setMessageNo(messageNo);
				commonSearch.setModifyOper(SpringUtil.getSysUserName());
				commonSearch.setModifyTime(new Date());
				commonSearch.setOperateType(2);
				if(commonSearchMapper.isSameRecord(commonSearch)==0) {
					commonSearchMapper.updateByPrimaryKeySelective(commonSearch);
				}else {
					resultMes.setResult(0);
					resultMes.setMessage("已存在相同的搜索引擎！");
					return resultMes;
				}
				BaseKeys key = new BaseKeys();
				key.setOperType(OperationType.MODIFY.getType());
				key.setMessageNo(record.getMessageNo());
				key.setMessageName("通用参数：搜索引擎");
				key.setMessageType(record.getMessageType());
				key.setId(commonSearch.getSeId());
				key.setDataType(DataType.POLICY.getType());
				keys.add(key);
			}else {
				List<CommonSearch> newSea = new ArrayList<CommonSearch>();
				for(String str:searchEngine) {
					CommonSearch commonSearch = new CommonSearch();
					commonSearch.setSename(str);
					commonSearch.setMessageNo(messageNo);
					commonSearch.setModifyOper(SpringUtil.getSysUserName());
					commonSearch.setModifyTime(new Date());
					commonSearch.setOperateType(1);
					commonSearch.setCreateOper(commonSearch.getModifyOper());
					commonSearch.setCreateTime(commonSearch.getModifyTime());
					if(commonSearchMapper.isSameRecord(commonSearch)==0) {
							newSea.add(commonSearch);
						}else {
							resultMes.setResult(0);
							resultMes.setMessage("已存在相同的搜索引擎！");
							return resultMes;
						}
				}
				if(newSea.size()>0) {
					commonSearchMapper.insertList(newSea);
					for(CommonSearch sea:newSea) {
						BaseKeys key = new BaseKeys();
						key.setOperType(OperationType.CREATE.getType());
						key.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
						key.setDataType(DataType.POLICY.getType());
						key.setId(sea.getSeId());
						keys.add(key);
					}
					
				}
			}
		}else if(result.size()==0){
			if(messageNo==null) {
				messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.COMMON_PARAMETER_SET.getId());
				record.setMessageName(MessageType.COMMON_PARAMETER_SET.name());
				record.setMessageSequenceno(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.COMMON_PARAMETER_SET.getId()));
				record.setMessageNo(messageNo);
				record.setCreateTime(new Date());
				record.setCreateOper(SpringUtil.getSysUserName());
				record.setModifyOper(SpringUtil.getSysUserName());
				record.setModifyTime(record.getCreateTime());
				record.setOperateType(1);
				policyMapper.insertSelective(record);
			}
			List<String> newSearch = new ArrayList<String>();
			for(String str:searchEngine) {
				if(!newSearch.contains(str)) {
					newSearch.add(str);
				}
			}
			List<CommonSearch> addSearch = new ArrayList<CommonSearch>();
			for(String name : newSearch) {
				if(commonSearchMapper.getRecordByname(name)==0) {
					CommonSearch search = new CommonSearch();
					search.setModifyTime(new Date());
					search.setModifyOper(SpringUtil.getSysUserName());
					search.setMessageNo(messageNo);
					search.setCreateOper(SpringUtil.getSysUserName());
					search.setCreateTime(search.getModifyTime());
					search.setSename(name);
					search.setOperateType(1);
					addSearch.add(search);
				}
			}
			if(addSearch.size()>0) {
				commonSearchMapper.insertList(addSearch);
				for(CommonSearch tem : addSearch) {
					BaseKeys key = new BaseKeys();
					key.setOperType(OperationType.CREATE.getType());
					key.setMessageName("通用参数：搜索引擎");
					key.setMessageType(record.getMessageType());
					key.setId(tem.getSeId());
					key.setDataType(DataType.POLICY.getType());
					keys.add(key);
				}
			}
		}
		rediscluster.putHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()), "1");
		resultMes.setKeys(keys);
		resultMes.setResult(1);
		return resultMes;
	}

	@Override
	public List<CommonSearch> getAllSearch() {
		return commonSearchMapper.getAllSearch();
	}

	@Override
	public ResponseResult<BaseKeys> deleteSearch(String[] ids) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		for(String str:ids) {
			CommonSearch record = new CommonSearch();
			record.setSeId(Long.valueOf(str));
			record.setModifyOper(SpringUtil.getSysUserName());
			record.setModifyTime(new Date());
			record.setOperateType(OperationType.DELETE.getType());
			commonSearchMapper.updateByPrimaryKeySelective(record);
		}
		result.setResult(1);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> cookieSave(HttpServletRequest request, CommonCookie cookies,Integer existFile) throws ImportException {
		if(existFile==1) {
			//保存导入文件到本地，返回保存的文件名
	        String fileName =  commonService.saveFile(request, "cookieFile","CookiesFile");
		}
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		if(cookies.getCookieId()==null) {
			String[] website = cookies.getCookieHostName().split(",");
			String[] keyValue = cookies.getCookieKeyValue().split(",");
			List<CommonCookie> newCookie = new ArrayList<CommonCookie>();
			for(int i=0;i<website.length;i++) {
				CommonCookie cookie = new CommonCookie();
				if(website[i]!=null && !"".equals(website[i])
						&& keyValue[i]!=null && !"".equals(keyValue[i])) {
					cookie.setCookieHostName(website[i]);
					cookie.setCookieKeyValue(keyValue[i]);
					newCookie.add(cookie);
				}
			}
			Map<String,InputStream> fileMap = ExcelUtil.getInputStreamList(request,"cookieFile");
			if(fileMap!=null && !fileMap.isEmpty() && existFile==1) {
				CookieImportMgr cookieImportMgr = new CookieImportMgr();
				Map<String, List<CommonCookie>> maps = cookieImportMgr.readDataFromStreas(fileMap);
				Set<String> set = maps.keySet();
				Iterator<String> it= set.iterator();
				if(cookieImportMgr.getErrorMsg()==null) {
					for(;it.hasNext();){
						newCookie.addAll(maps.get(it.next()));
					}
				}else {
					result.setResult(0);
					result.setMessage(cookieImportMgr.getErrorMsg());
					return result;
				}
			}
			Policy record = new Policy();
			record.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
			Long messageNo = policyMapper.getMaxMessageNoByType(record);
			if(messageNo==null) {
				messageNo = MessageNoUtil.getInstance().getMessageNo(MessageType.COMMON_PARAMETER_SET.getId());
				record.setMessageName(MessageType.COMMON_PARAMETER_SET.name());
				record.setMessageSequenceno(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.COMMON_PARAMETER_SET.getId()));
				record.setMessageNo(messageNo);
				record.setCreateTime(new Date());
				record.setCreateOper(SpringUtil.getSysUserName());
				record.setModifyOper(SpringUtil.getSysUserName());
				record.setModifyTime(record.getCreateTime());
				record.setOperateType(1);
				policyMapper.insertSelective(record);
			}
			List<CommonCookie> newCookies = new ArrayList<CommonCookie>();
			for(int i=0;i<newCookie.size();i++) {
				int isSame = 0;
				for(int j=0;j<newCookies.size();j++) {
					if(newCookies.get(j).getCookieHostName().equals(newCookie.get(i).getCookieHostName())
							&& newCookies.get(j).getCookieKeyValue().equals(newCookie.get(i).getCookieKeyValue())) {
						isSame++;
					}
					if(isSame>0) {
						break;
					}
				}
				if(isSame>0) {
					continue;
				}
				newCookie.get(i).setMessageNo(messageNo);
				newCookie.get(i).setModifyOper(SpringUtil.getSysUserName());
				newCookie.get(i).setOperateType(1);
				newCookie.get(i).setModifyTime(new Date());
				newCookie.get(i).setCreateOper(SpringUtil.getSysUserName());
				newCookie.get(i).setCreateTime(newCookie.get(i).getModifyTime());
				newCookies.add(newCookie.get(i));
			}
			List<CommonCookie> addCookies = new ArrayList<CommonCookie>();
			for(CommonCookie tem:newCookies) {
				if(commonCookieMapper.isSameDate(tem)==0) {
					addCookies.add(tem);
				}
			}
			if(addCookies.size()>0) {
				commonCookieMapper.insertList(addCookies);
				for(CommonCookie tem:addCookies) {
					BaseKeys key = new BaseKeys();
					key.setMessageName("通用参数：cookie");
					key.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
					key.setId(tem.getCookieId());
					key.setOperType(OperationType.CREATE.getType());
					key.setDataType(DataType.POLICY.getType());
					keys.add(key);
				}
			}
		}else {
			if(commonCookieMapper.isSameDates(cookies)==0) {
				cookies.setOperateType(2);
				cookies.setModifyOper(SpringUtil.getSysUserName());
				cookies.setModifyTime(new Date());
				commonCookieMapper.updateByPrimaryKeySelective(cookies);
				BaseKeys key = new BaseKeys();
				key.setMessageName("通用参数：cookie");
				key.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
				key.setId(cookies.getCookieId());
				key.setOperType(OperationType.MODIFY.getType());
				key.setDataType(DataType.POLICY.getType());
				keys.add(key);
			}else {
				result.setResult(0);
				result.setMessage("已存在相同的Cookie!");
				return result;
			}
		}
		rediscluster.putHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()), "1");
		result.setKeys(keys);
		result.setResult(1);
		return result;
	}

	@Override
	public PageResult<CommonCookie> getCookieData(Integer pageSize, Integer pageIndex) {
		PageResult<CommonCookie> result = new PageResult<CommonCookie>();
		PageHelper.startPage(pageIndex, pageSize);
		List<CommonCookie> info = commonCookieMapper.getCookieList();
		PageInfo<CommonCookie> pageResult = new PageInfo<CommonCookie>(info);
		result.setTotal(pageResult.getTotal());
		result.setRows(info);
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> deleteCookie(String[] ids) {
		for(String str:ids) {
			CommonCookie record = new CommonCookie();
			record.setCookieId(Long.valueOf(str));
			record.setModifyOper(SpringUtil.getSysUserName());
			record.setModifyTime(new Date());
			record.setOperateType(OperationType.DELETE.getType());
			commonCookieMapper.updateByPrimaryKeySelective(record);
		}
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		result.setResult(1);
		return result;
	}
	
	public String getAlarmType() {
		return rediscluster.getHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()))==null ? "0":rediscluster.getHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()));
	}

	@Override
	protected boolean addDb(BaseVO policy) {
		CommonParameterStrategy strategy = (CommonParameterStrategy)policy;
		Policy record = new Policy();
		record.setMessageNo(strategy.getMessageNo());
		record.setMessageSequenceno(strategy.getMessageSequenceNo());
		record.setMessageType(strategy.getMessageType());
		record.setModifyTime(new Date());
		record.setModifyOper(SpringUtil.getSysUserName());
		policyMapper.updatePolicyByMessageNoAndType(record);
		return true;
	}

	@Override
	protected boolean deleteDb(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean modifyDb(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean addCustomLogic(BaseVO policy) {
		 return setPolicyOperateSequenceToRedis(policy) && addTaskAndChannelToRedis(policy);
	}

	@Override
	protected boolean modifyCustomLogic(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean deleteCustomLogic(BaseVO policy) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ResponseResult<BaseKeys> sendPolicy() {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		CommonThreshold  commonThreshold  =commonThresholdMapper.getThreshold();
		if(commonThreshold==null) {
			result.setMessage("请先保存阈值信息！");
			result.setResult(0);
			return result;
		}
		List<CommonCookie> cookieList = commonCookieMapper.getCookieList();
		List<String> searchList = commonSearchMapper.getAllSearchName();
		Policy record = new Policy();
		record.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		Long messageNo = policyMapper.getMaxMessageNoByType(record);
		Long sequenceNo = MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.COMMON_PARAMETER_SET.getId());
		CommonParameterStrategy strategy = new CommonParameterStrategy();
		strategy.setMessageNo(messageNo);
		strategy.setMessageSequenceNo(sequenceNo);
		strategy.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		strategy.setOperationType(OperationConstants.OPERATION_SAVE);
		strategy.setSeName(searchList);
		strategy.setCookieHost(cookieList);
		strategy.setKwThreholds(commonThreshold.getKwThreholds());
		strategy.setWebHitThreshold(commonThreshold.getWebHitThreshold());
		strategy.setProbeType(ProbeType.DPI.getValue());
		addPolicy(strategy);
		result.setResult(1);
		rediscluster.putHash(RedisKeyConstant.GLOBAL_COMMON_ALARM, String.valueOf(MessageType.COMMON_PARAMETER_SET.getId()), "0");
		key.setMessageNo(strategy.getMessageNo());
		key.setMessageName(MessageType.COMMON_PARAMETER_SET.name());
		key.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		key.setOperType(OperationType.SEND.getType());
		key.setDataType(DataType.POLICY.getType());
		keys.add(key);
		result.setKeys(keys);
		return result;
	}

	public List<CommonThreshold> getPolicyDetail() {
		List<CommonThreshold>  result = new ArrayList<CommonThreshold>();
		CommonThreshold  commonThreshold  =commonThresholdMapper.getThreshold();
		PolicyStatus appPolicy = new PolicyStatus();
		 if(commonThreshold.getMessageNo()!=null) {
         	PolicyStatus query2 = new PolicyStatus();
         	query2.setMessageNo(commonThreshold.getMessageNo());
         	query2.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
         	appPolicy = policyStatusMapper.getCountTotalAndFail(query2);
         }
         if(appPolicy!=null) {
        	 commonThreshold.setPolicyCount(appPolicy.getMainCount() == null ? "0/0" : appPolicy.getMainCount());
         }else{
        	 commonThreshold.setPolicyCount("0/0");
         }
         if(commonThreshold!=null) {
        	 result.add(commonThreshold);
         }
		return result;
	}

	@Override
	public ResponseResult<BaseKeys> policyResend(long messageNo) {
		ResponseResult<BaseKeys> result = new ResponseResult<BaseKeys>();
		List<BaseKeys> keys = new ArrayList<BaseKeys>();
		BaseKeys key = new BaseKeys();
		List<String> ip = new ArrayList<String>();
		manualRetryPolicy(ProbeType.DPI.getValue(), MessageType.COMMON_PARAMETER_SET.getId(), messageNo, ip);
		result.setMessage("重发成功");
		result.setResult(1);
		key.setMessageName(MessageType.COMMON_PARAMETER_SET.name());
		key.setMessageNo(messageNo);
		key.setMessageType(MessageType.COMMON_PARAMETER_SET.getId());
		key.setOperType(OperationType.RESEND.getType());
		key.setDataType(DataType.POLICY.getType());
		keys.add(key);
		result.setKeys(keys);
		return result;
	}
}
