package com.aotain.zongfen.service.general.ipaddress.impl;

import com.aotain.common.policyapi.constant.FileTypeConstant;
import com.aotain.common.policyapi.constant.MessageType;
import com.aotain.common.policyapi.constant.OperationConstants;
import com.aotain.common.policyapi.constant.ProbeType;
import com.aotain.common.policyapi.model.ClassInfoLibsStrategy;
import com.aotain.common.policyapi.model.IpAddressArea;
import com.aotain.common.utils.file.SftpClientUtil;
import com.aotain.common.utils.redis.AlarmClassInfoUtil;
import com.aotain.common.utils.redis.FileTypeVersionUtil;
import com.aotain.common.utils.redis.MessageNoUtil;
import com.aotain.common.utils.redis.MessageSequenceNoUtil;
import com.aotain.common.utils.tools.FileUtils;
import com.aotain.common.utils.tools.Tools;
import com.aotain.login.support.Authority;
import com.aotain.login.support.StringUtils;
import com.aotain.zongfen.mapper.device.ZongFenDeviceMapper;
import com.aotain.zongfen.mapper.general.ClassFileInfoMapper;
import com.aotain.zongfen.mapper.general.GenIPAddressMapper;
import com.aotain.zongfen.mapper.general.IpAddressAreaMapper;
import com.aotain.zongfen.mapper.general.ZfV2GenIpImporttaskMapper;
import com.aotain.zongfen.model.device.ZongFenDevice;
import com.aotain.zongfen.model.general.*;
import com.aotain.zongfen.service.general.classinfo.ClassInfoLibsStrategyService;
import com.aotain.zongfen.service.general.classinfo.ClassInfoService;
import com.aotain.zongfen.service.general.ipaddress.IPAddressService;
import com.aotain.zongfen.utils.DateUtils;
import com.aotain.zongfen.utils.IPUtil;
import com.aotain.zongfen.utils.PageResult;
import com.aotain.zongfen.utils.SpringUtil;
import com.aotain.zongfen.validate.dataImport.general.IpAddressImportMgr;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.annotation.PostConstruct;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = {Exception.class})
public class IPAddressServiceImpl implements IPAddressService {

    private static final Logger LOG = LoggerFactory.getLogger(IPAddressServiceImpl.class);

    @Autowired
    private GenIPAddressMapper genIPAddressMapper;

    @Autowired
    private ClassFileInfoMapper classFileInfoMapper;

    @Autowired
    private IpAddressAreaMapper ipAddressAreaMapper;

    @Autowired
    private ClassInfoLibsStrategyService classInfoLibsStrategyService;

    @Autowired
    private ClassInfoService classInfoService;

    @Autowired
    private ZongFenDeviceMapper zongFenDeviceMapper;

    @Autowired
    private ZfV2GenIpImporttaskMapper zfV2GenIpImporttaskMapper;

    IpAddressImportMgr ipAddressImportMgr = new IpAddressImportMgr();
    private static String filePath = System.getProperty("user.dir") + File.separator + "ipimportfile";

    @PostConstruct
    public void initThread() {
        importTaskProcess();
    }

    /**
     * @param task
     * @param status
     * @desc 写任务消息状态。
     */
    private void updataTaskStatus(ZfV2GenIpImporttask task, int status) {
        task.setStatus(status);
        task.setServerIp(Tools.getHostAddressAndIp());
        task.setFinishTime(new Date());
        int count = zfV2GenIpImporttaskMapper.updateByPrimaryKey(task);
        LOG.info("[IP address library static import ] task=" + task.toString() + " " + (status == 1 ? "success" : "failure"));
    }

    /**
     * @param task
     * @param
     * @desc 写任务消息状态。
     */
    private void updataTaskStatusResultFile(ZfV2GenIpImporttask task, String resultFile) {
        task.setStatus(2);
        task.setServerIp(Tools.getHostAddressAndIp());
        task.setFinishTime(new Date());
        task.setResultFile(resultFile);
        int count = zfV2GenIpImporttaskMapper.updateByPrimaryKey(task);
        LOG.info("[IP address library static import ] task=" + task.toString() + " failure");
    }


    private Map<String, String> getReturnMap(String status, String message) {
        Map<String, String> retMap = Maps.newHashMap();
        retMap.put("status", status);
        retMap.put("message", message);
        return retMap;
    }

    /**
     * 获取最新的一个等待执行的任务。
     *
     * @return
     */
    private ZfV2GenIpImporttask getOneTask() {
        ZfV2GenIpImporttask task = null;
        try {
            task = zfV2GenIpImporttaskMapper.selectOne();
            LOG.info("IP address library statically import the acquisition task=" + (task != null ? task.toString() : null));
        } catch (Exception e) {
            LOG.error("", e);
            updataTaskStatus(task, 2);
        }
        // 0表示等待执行
        if (task != null && 0 == task.getStatus()) {
            return task;
        } else {
            return null;
        }
    }

    private void importTaskProcess() {
        LOG.debug("[IP address library static import ] import processing threads start ");
        ipAddressImportMgr.clearResultMsg();
        ZfV2GenIpImporttask task = getOneTask();
        if (task == null) {
            LOG.info("no task waitting for process.");
            return;
        }
        String saveFilePath = task.getFilename();
        File saveFile = new File(filePath,saveFilePath);
        String fileDir = saveFile.getParent();
        String fileName = saveFile.getName();
        if (fileName.contains("xlsx")) {
            fileName=fileName.replace("xlsx", "txt");
        } else if (fileName.contains("xls")) {
            fileName=fileName.replace("xls", "txt");
        }
        String resultFileName = task.getResultFile();

        String type = task.getImportType();
        File importFile = new File(filePath,saveFilePath);

        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(importFile));
        } catch (FileNotFoundException e) {
            ipAddressImportMgr.setErrorMsg(e.getMessage());
            LOG.error("[IP address library static import ] io error ",e);
        }
        Long t1 = System.currentTimeMillis();
        Map<String, List<GenIPAddress>> map = null;
        try {
            map = ipAddressImportMgr.readDataFromStreams(saveFilePath, is);
        } catch (Exception e) {
            ipAddressImportMgr.setErrorMsg(e.getMessage());
            LOG.error("[IP address library static import ] parse import file error ",e);
            decideContinue(task, resultFileName);
            return;
        }
        LOG.info("[IP address library static import ] parse file take time:" + (System.currentTimeMillis() - t1));
        LOG.info("[IP address library static import ] Self check start");
            //自校验。
        List<GenIPAddress> list = null;
        try {
            list = ipAddressImportMgr.checkSelf2(map, type);
        } catch (Exception e) {
            ipAddressImportMgr.setErrorMsg(e.getMessage());
            LOG.error("[IP address library static import ] self check error ",e);
            decideContinue(task, resultFileName);
            return;
        }
        LOG.info("[IP address library static import ] Self check end, fan return set size:" + list.size());
        try {
            if (list.size() > 0) {
                if ("0".equals(type)) {
                    uploadFileAll(list);
                    LOG.info("[IP address library static import ] Full import end");
                } else if ("1".equals(type)) { // 增量导入
                    LOG.info("[IP address library static import ] Incremental import start");
                    //查出所有的数据
                    List<GenIPAddress> allList = genIPAddressMapper.getIndexList(null);
                    //增量导入的时候要 跟数据库中的记录校验
                    validateIpCompareAll(allList, list, ipAddressImportMgr);

                    uploadFileAdd(list, saveFilePath, ipAddressImportMgr);
                    LOG.info("[IP address library static import ] Incremental import end");
                }

                try {
                    File resultFile = new File(filePath,resultFileName);
                    FileUtils.writeToFile(ipAddressImportMgr.getResultMsg().toString().getBytes(), resultFile.getAbsolutePath());
                    LOG.info(resultFileName + " is created, Path:" + resultFile.getAbsolutePath());
                    IpAddressImportMgr.process = 1.0;
                    updataTaskStatus(task,1);
                } catch (Exception e) {
                    ipAddressImportMgr.setErrorMsg("write result file failure;e:" + e.getMessage());
                    LOG.error("write result file failure;e:", e);
                    decideContinue(task, resultFileName);
                    return;
                }

                AlarmClassInfoUtil.getInstance().setToAlarmByType(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId());
            } else {
                ipAddressImportMgr.setErrorMsg("文件为空，请重新导入！");
                LOG.error("The file is empty, please import again");
                throw new RuntimeException("The file is empty, please import again");
            }
        } catch (Exception e) {
            ipAddressImportMgr.setErrorMsg(e.getMessage());
            LOG.error("[IP address library static import ] self check error ",e);
            decideContinue(task, resultFileName);
        }
        LOG.debug("ip import task end.");
    }

    private boolean decideContinue(ZfV2GenIpImporttask task, String resultFileName) {

        if (ipAddressImportMgr.getErrorMsg() != null) {
            File resultFile = new File(filePath,resultFileName);
            try {
                FileUtils.writeToFile(ipAddressImportMgr.getResultMsg().toString().getBytes(), resultFile.getAbsolutePath());
            } catch (Exception e) {
                LOG.error("[IP address library static import ] write resultFile error ",e);
            }
            updataTaskStatusResultFile(task,resultFile.getAbsolutePath());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, String> uploadFile(HttpServletRequest request) {
        Map<String, String> retMap = Maps.newHashMap();
        List<ZfV2GenIpImporttask> allTask = zfV2GenIpImporttaskMapper.selectAll();
        for (ZfV2GenIpImporttask task : allTask) {
            if (0 == task.getStatus()) {
                return getReturnMap("1", "已经存在导入任务");
            }
        }
        File saveFile = saveFile(request, "ipAddressFile");
        try {
            String type = request.getParameter("type");
            int userId = Authority.getUserDetailInfo(request) != null ? Authority.getUserDetailInfo(request).getUserId() : 0;

            if (insertTask(type, userId, saveFile)) {
                new Thread(new IpAddressThread()).start();
            }
        } catch (Exception e) {
            LOG.error("", e);
            return getReturnMap("3", "添加导入任务失败");
        }
        return getReturnMap("", "添加导入任务成功");
    }

    /**
     * 向数据库写导入任务。
     *
     * @param type
     * @param userId
     * @param saveFile
     */
    private boolean insertTask(String type, int userId, File saveFile) {
        ZfV2GenIpImporttask task = new ZfV2GenIpImporttask();

        String resultFileName = saveFile.getName();
        if (resultFileName.contains("xlsx")) {
            resultFileName=resultFileName.replace("xlsx", "txt");
        } else if (resultFileName.contains("xls")) {
            resultFileName=resultFileName.replace("xls", "txt");
        }
        task.setCreateTime(new Date());
        task.setFilename(saveFile.getName());
        task.setImportType(type);
        task.setResultFile(resultFileName);
        task.setStatus(0);
        task.setUserid(userId);
        task.setServerIp(Tools.getHostAddressAndIp());
        try {
            int h = zfV2GenIpImporttaskMapper.insert(task);
            LOG.info("[Static IP address Library ] insert task=" + task.toString() + " success");
            return true;
        } catch (Exception e) {
            LOG.info("[Static IP address Library ] insert task=" + task.toString() + " error", e);
            return false;
        }
    }

    /**
     * 保存导入文件。
     *
     * @param request
     * @param fileName
     * @return
     */
    public File saveFile(HttpServletRequest request,String fileName) {
        MultipartFile file = ((MultipartRequest) request).getFile(fileName);
        try {
            String saveName = file.getOriginalFilename().substring(0, file.getOriginalFilename().indexOf(".xlsx"));
            saveName = saveName+"_"+String.valueOf(System.currentTimeMillis()) + ".xlsx";
            String filePath = System.getProperty("user.dir") + "/ipimportfile/";
            File folder = new File(filePath);
            if (!folder.exists()) {
                folder.mkdir();
            }
            File diskFile = new File(filePath + saveName);
            file.transferTo(diskFile);
            LOG.info("import,save import file{" + file.getOriginalFilename() + "} to " + diskFile.getAbsolutePath() + " success");
            return diskFile;
        } catch (Exception e) {
            LOG.info("import,save import file{" + file.getOriginalFilename() + " failure,e=" + e);
        }
        return null;
    }

    class IpAddressThread implements Runnable {
        @Override
        public void run() {
            importTaskProcess();
        }
    }

    @Override
    public Map<String, String> queryProgress(HttpServletRequest request) {
        String filePath = System.getProperty("user.dir") + "/ipimportfile/";
        int userId = Authority.getUserDetailInfo(request) != null ? Authority.getUserDetailInfo(request).getUserId() : 0;
        Map<String, String> map = Maps.newHashMap();
        try {
            ZfV2GenIpImporttask task = null;
            // 查询数据库
            try {
                task = zfV2GenIpImporttaskMapper.selectByUserId(userId);

            } catch (Exception e) {
                LOG.error("", e);
            }

            if (task != null && 0 == task.getStatus()) {
                map.put("process", IpAddressImportMgr.process * 100 + "");
                map.put("resultFileName", "正在导入");
                LOG.error("[ip地址库静态导入] 查看任务处理进度,process=" + IpAddressImportMgr.process * 100 + ";resultFileName=" + task.getResultFile());
            } else if (task != null && 1 == task.getStatus()) {
                File resultFile = new File(filePath,task.getResultFile());
                map.put("process", "100");
                if(resultFile.length() == 0){
                    map.put("resultFileName", "导入成功");
                }else{
                    map.put("resultFileName", resultFile.getName());
                }
                LOG.error("[ip地址库静态导入] 查看任务处理进度,process=" + 100 + ";resultFileName=" + task.getResultFile());
            } else if (task != null && 2 == task.getStatus()) {
                File resultFile = new File(filePath,task.getResultFile());
                map.put("process", -1 + "");
                map.put("resultFileName", resultFile.getName());
                LOG.error("[ip地址库静态导入] 查看任务处理进度,resultFileName={" + task.getResultFile() + "} 处理状态失败");
            } else {
                map.put("process", 100 + "");
                map.put("resultFileName", "no file");
            }
        } catch (Exception e) {
            LOG.error("[ip地址库静态导入] 查看任务处理进度,失败 e:", e);
        }
        return map;
    }

    @Override
    public void queryResultFile(HttpServletRequest request, HttpServletResponse response) {

        int userId = Authority.getUserDetailInfo(request) != null ? Authority.getUserDetailInfo(request).getUserId() : 0;
        ZfV2GenIpImporttask task = zfV2GenIpImporttaskMapper.selectByUserId(userId);
        String resultFileName = task.getResultFile();
        List<String> lists = FileUtils.readFileByLines(new File(filePath, resultFileName));

        try {
            resultFileName = URLEncoder.encode(resultFileName, "utf-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("", e);
        }
        // 导出文件
        response.setContentType("text/plain");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFileName);
        BufferedOutputStream buff = null;
        StringBuffer write = new StringBuffer();
        String enter = "\r\n";
        ServletOutputStream outSTr = null;
        try {
            outSTr = response.getOutputStream(); // 建立
            buff = new BufferedOutputStream(outSTr);
            // 把内容写入文件
            if (lists.size() > 0) {
                for (int i = 0; i < lists.size(); i++) {
                    write.append(lists.get(i));
                    write.append(enter);
                }
            }
            buff.write(write.toString().getBytes("UTF-8"));
            buff.flush();
            buff.close();
        } catch (Exception e) {

        } finally {
            try {
                buff.close();
                outSTr.close();
            } catch (Exception e) {
            }
        }
    }


    /**
     * @param @param  allList
     * @param @param  addList
     * @param @return 设定文件
     * @return boolean    返回类型
     * @throws
     * @Title: validateIpCompareAll
     * @Description:
     */
    private boolean validateIpCompareAll(List<GenIPAddress> allList, List<GenIPAddress> addList, IpAddressImportMgr ipAddressImportMgr) throws Exception {
        boolean flag = true;
        //和数据库里的数据做比较
        IpAddressImportMgr.process = 0.6;
        List<GenIPAddress> updateList = new ArrayList<>();
        int Alllength = allList.size();
        int Addlength = addList.size();
        double count = 0.0;

        Iterator it = addList.iterator();

        while (it.hasNext()) {
            IpAddressImportMgr.process = 0.6 + ((count++) / Addlength) * 0.4;
            GenIPAddress m1 = (GenIPAddress) it.next();
            boolean b = false;

            for (int j = 0; j < Alllength; j++) {
                if (b) continue;
                GenIPAddress m2 = allList.get(j);
                if (!m1.getIpType().equals(m2.getIpType())) {
                    continue;
                }
                Long sIp1 = IPUtil.StringToLong(m2.getStartIp());
                Long eIp1 = IPUtil.StringToLong(m2.getEndIp());
                Long sIp2 = IPUtil.StringToLong(m1.getStartIp());
                Long eIp2 = IPUtil.StringToLong(m1.getEndIp());

                boolean test = !(sIp2 >= eIp1 || sIp1 >= eIp2);
                if (false) {
                    //两个记录区域id不一样
                    if (!m1.getAreaId().equals(m2.getAreaId())) {
                        ipAddressImportMgr.addResultMsg("导入对象{" + m1.getErrorMsg() + "}与数据库中的记录{" + m2.getErrorMsg() + "}有冲突");
                        j = Alllength - 2;
                        it.remove();
                        b = true;
                        //两个记录区域id一样
                    } else {
                        if (sIp1.intValue() == sIp2.intValue() && eIp1.intValue() == eIp2.intValue()) {
                            it.remove();
                            LOG.warn("duplicate data is:" + m1.getErrorMsg());
                            ipAddressImportMgr.addResultMsg("导入对象{" + m1.getErrorMsg() + "}与数据库中的记录重叠!");
                            continue;
                        }
                        Long startIp = sIp1 < sIp2 ? sIp1 : sIp2;
                        Long endIp = eIp1 > eIp2 ? eIp1 : eIp2;
                        m2.setStartIp(IPUtil.longToIP(startIp));
                        m2.setEndIp(IPUtil.longToIP(endIp));
                        ipAddressImportMgr.addResultMsg("导入对象{" + m1.getErrorMsg() + "}与数据库中的记录有包含关系，合并为{" + m2.getErrorMsg() + "}");
                        it.remove();
                        updateList.add(m2);
                    }
                }

            }
        }
        // update database;
        for (GenIPAddress m2 : updateList) {
            m2.setUpdateTime(new Date());
            genIPAddressMapper.updateByPrimaryKey(m2);
        }
        return true;
    }


    private void uploadFileAll(List<GenIPAddress> list) throws Exception {
        LOG.info("[IP address library static import ] 全量导入开始");
        //1:全量导入：删除原来数据
        genIPAddressMapper.deleteAll();
        ipAddressAreaMapper.deleteAll();
        LOG.debug("[IP address library static import ] 删除历史数据成功");
        LOG.debug("[IP address library static import ] 准备插入数据库记录为:" + list.size());

        //2:插入IP地址库数据
        List<GenIPAddress> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            tempList.add(list.get(i));
            if (tempList.size() == 5000) {
                genIPAddressMapper.insertList(tempList);
                tempList = new ArrayList<>();
            }
        }
        if (tempList.size() > 0) {
            genIPAddressMapper.insertList(tempList);
            tempList = new ArrayList<>();
        }
        if (list.size() > 0) {
            //3:处理省份地市的数据
            List<IpAddressArea> ipAreaList = dealIPAddress(list);
            if (ipAreaList.size() > 0) {
                //4:插入省份地市的数据
                ipAddressAreaMapper.insertList(ipAreaList);
                LOG.info("-------insert ipAreaList size(" + ipAreaList.size() + ") success!----------");
            }
        }
    }

    /**
     * @param @param addList
     * @param @param fileName    设定文件
     * @return void    返回类型
     * @throws
     * @Title: uploadFileAdd
     * @Description: 得从数据库里把数据取出来比较确认有没有(这里用一句话描述这个方法的作用)
     */
    private void uploadFileAdd(List<GenIPAddress> addList, String fileName, IpAddressImportMgr ipAddressImportMgr) throws Exception {
        try {
            LOG.info(fileName + " insert records{" + addList.size() + "}");
            //1: 插入IP地址库数据

            List<GenIPAddress> tempList = new ArrayList<>();
            for (int i = 0; i < addList.size(); i++) {
                tempList.add(addList.get(i));
                if (tempList.size() == 5000) {
                    genIPAddressMapper.insertList(tempList);
                    LOG.info("---------insert " + tempList.size() + " datas success!--------");
                    tempList = new ArrayList<>();
                }
            }
            if (tempList.size() > 0) {
                genIPAddressMapper.insertList(tempList);
                LOG.info("---------insert " + tempList.size() + " datas success!--------");
                tempList = new ArrayList<>();
            }

            if (addList.size() > 0) {
                //2:处理省份地市的数据
                List<IpAddressArea> addAreaList = dealIPAddress(addList);
                List<IpAddressArea> allAreaList = ipAddressAreaMapper.selectAll();
                //检查下省份地市的数据是否有重复：可以允许重发,有重复的话就去掉

                Map<String, String> maps = new HashMap<>();
                for (IpAddressArea m : allAreaList) {
                    maps.put(m.getAreaType() + "-" + m.getAreaId(), "");
                }
                Iterator it = addAreaList.iterator();
                while (it.hasNext()) {
                    IpAddressArea b = (IpAddressArea) it.next();
                    if (maps.containsKey(b.getAreaType() + "-" + b.getAreaId())) {
                        LOG.debug("dupliate data is:" + b.getAreaType() + "-" + b.getAreaId());
                        it.remove();
                    }
                }
                if (addAreaList.size() > 0) {
                    //3:插入省份地市的数据
                    ipAddressAreaMapper.insertList(addAreaList);
                }
            }
        } catch (Exception e) {
            LOG.error("", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * @param @param  list
     * @param @return 设定文件
     * @return List<IpAddressArea>    返回类型
     * @throws
     * @Title: dealIPAddress
     * @Description: 修改后的处理IP地址库的方(很重要)
     * @Description: 区域类别:1=省份，2=地市，3=IDC，4=国内运营商，5=国际运营商,6=大洲，7=国
     */
    private List<IpAddressArea> dealIPAddress(List<GenIPAddress> list) {
        List<IpAddressArea> ipAreaList = new ArrayList<IpAddressArea>();

        if (list.size() == 0) {
            return ipAreaList;
        }
        //先用set集合去重
        Set<IpAddressArea> set = new HashSet<IpAddressArea>();
        IpAddressArea ipArea = null;
        //1:对相同的地市去重：去重原理同Area_ID

        Map<String, GenIPAddress> maps = new HashMap<>();
        for (GenIPAddress m : list) {
            maps.put(m.getAreaId(), m);
        }
        if (maps.size() > 0) {
            list = new ArrayList<GenIPAddress>();
            for (GenIPAddress k : maps.values()) {
                list.add(k);
            }
        }

        if (list.size() == 0) {
            return ipAreaList;
        }

        //2:赋值，先给子节点赋值
        for (GenIPAddress temp : list) {
            //获取areaType
            //先要判断是否为区域id，即是否为二进制，是的话，就添加进去，不是就进行下一步循环
            if (!isBinary(temp.getAreaId())) {
                continue;
            }
            Integer type = Integer.parseInt(temp.getAreaId().substring(0, 3), 2);
            Byte areaType = 1;
            if (type == 1) {//电信网内 : 源数据类别（3bits）+  区域类别（2bits）+ 省份ID（5bits）+ 地市_ID/IDC_ID（6bits）

                if(temp.getAreaId().length()<16){
                    LOG.error("error line:"+temp.toString());
                    continue;
                }

                if (temp.getAreaId().substring(3, 5).equals("01")) {//省
                    //地市
                    // 省份
                    ipArea = new IpAddressArea();
                    areaType = 1;
                    ipArea.setAreaId(temp.getAreaId().substring(5, 10));
                    ipArea.setIsParent(true);
                    ipArea.setPareaId("0");
                    ipArea.setAreaName(temp.getProvinceName());
                    ipArea.setAreaType(areaType);
                    ipAreaList.add(ipArea);
                    //--------------------------------------------------------------------------------
                    ipArea = new IpAddressArea();
                    areaType = 2;
                    ipArea.setAreaId(temp.getAreaId().substring(10, 16));
                    ipArea.setIsParent(false);
                    ipArea.setPareaId(temp.getAreaId().substring(5, 10));
                    ipArea.setAreaName(temp.getAreaName());
                    ipArea.setAreaType(areaType);
                    ipAreaList.add(ipArea);
                } else {//IDC（10）
                    //IDC
                    ipArea = new IpAddressArea();
                    areaType = 3;
                    ipArea.setAreaId(temp.getAreaId().substring(10, 16));
                    ipArea.setIsParent(false);
                    ipArea.setPareaId(temp.getAreaId().substring(5, 10));
                    ipArea.setAreaName(temp.getAreaName());
                    ipArea.setAreaType(areaType);
                    ipAreaList.add(ipArea);
                }
            } else if (type == 2) {//互联互通:源数据类别（3bits）+ 国内运营商ID（6bits）	+ 省份ID（5bits）	+ 城域网ID（6bits）
                //国内运营商
				/*ipArea = new IpAddressArea();
				areaType = 4;
				ipArea.setAreaId(temp.getAreaId().substring(3, 9));
				ipArea.setIsParent(true);
				ipArea.setPAreaId("0");
				ipArea.setAreaName(temp.getProvinceName());
				ipArea.setAreaType(areaType);
				set.add(ipArea);*/
            } else {//国际: 源数据类别（3bits）+国际运营商ID（8bits）+ 大洲ID（3bits）+ 国家ID（8bits）
            }
        }
    //    ipAreaList.addAll(set);

        Map<String, IpAddressArea> maps2 = new HashMap<>();
        for (IpAddressArea m : ipAreaList) {
            maps2.put(m.getAreaType()+"_"+m.getAreaId()+"_"+m.getPareaId(), m);
           System.out.println(m.getAreaType()+"-"+m.getAreaId()+":"+m.getAreaName()+"-"+m.getPareaId());
        }
        if (maps2.size() > 0) {
            ipAreaList = new ArrayList<IpAddressArea>();
            for (IpAddressArea k : maps2.values()) {
                ipAreaList.add(k);
            }
        }
        return ipAreaList;
    }


    @Override
    public PageResult<GenIPAddress> getIndexList(Integer pageSize, Integer pageIndex, String ipType, String ipaddress) {
        Map<String, String> query = new HashMap<String, String>();
        query.put("ipType", ipType);
        query.put("ipaddress", ipaddress);
        PageResult<GenIPAddress> result = new PageResult<GenIPAddress>();
        PageHelper.startPage(pageIndex, pageSize);
        List<GenIPAddress> info = genIPAddressMapper.getIndexList(query);
        PageInfo<GenIPAddress> pageResult = new PageInfo<GenIPAddress>(info);
        result.setTotal(pageResult.getTotal());
        result.setRows(info);
        return result;
    }

    @Override
    public Map<String, String> createAndSend(Integer serverId) {
        Map<String, String> resultMap = new HashMap<String, String>();
        Map<String, String> query = new HashMap<String, String>();
        query.put("ipType", null);
        query.put("ipaddress", null);
        List<GenIPAddress> allList = genIPAddressMapper.getIndexList(query);
        if (allList != null && allList.size() > 0) {
            String filePath = System.getProperty("user.dir") + "/productFile/";
            File directory = new File(filePath);
            File[] files = directory.listFiles();
            Long versionNo = FileTypeVersionUtil.getInstance().getFileType(FileTypeConstant.APPLIB.getValue());
            String fileName = "";
            for (File temFile : files) {
                fileName = temFile.getPath().substring(temFile.getPath().lastIndexOf(System.getProperty("file.separator")));
                if (fileName.indexOf("IPAT") > 0) {
                    temFile.delete();
                }
            }
            File newFile = new File(filePath + "IPAT_" + versionNo + "_000.txt");
            BufferedWriter buffer = null;
            try {
                newFile.createNewFile();
                buffer = new BufferedWriter(new java.io.FileWriter(newFile));
                StringBuffer writerData = new StringBuffer();
                for (GenIPAddress ipAddress : allList) {
                    writerData.append(ipAddress.getIpType()).append("\t").
                            append(ipAddress.getStartIp()).append("\t").
                            append(ipAddress.getEndIp()).append("\t").
                            append(ipAddress.getAreaName()).append("\r\n");
                }
                buffer.write(writerData.toString());
                buffer.flush();
                buffer.close();
                //后面还要先删除原来相关联的分类库策略，然后新增相应的分类库策略
                ClassFileInfo record = new ClassFileInfo();

                record.setMessageType(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId());
                record.setFileType(FileTypeConstant.IPLIB.getValue());
                ClassFileInfoKey classFileInfoKey = new ClassFileInfoKey();
                classFileInfoKey.setFileType(FileTypeConstant.IPLIB.getValue());
                classFileInfoKey.setMessageType(FileTypeConstant.IPLIB.getValue());
                record.setClassFileName("IPAT_" + versionNo + "_000.txt");
                record.setModifyOper(SpringUtil.getSysUserName());
                record.setModifyTime(new Date());
                record.setVersionNo(versionNo);
                ClassFileInfo result = classFileInfoMapper.selectByPrimaryKey(classFileInfoKey);
                record.setCreateOper(SpringUtil.getSysUserName());
                record.setCreateTime(record.getModifyTime());
                if (result != null && result.getClassFileName() != null) {
                    classFileInfoMapper.updateByPrimaryKeySelective(record);
                } else {
                    classFileInfoMapper.insert(record);
                }
                resultMap.put("fileName", record.getClassFileName());
                String msg = sendIPAddressLibMessage(record.getClassFileName(), versionNo, serverId);
                resultMap.put("message", msg);
                return resultMap;
            } catch (IOException e) {
                resultMap.put("message", "error");
                return resultMap;
            }
        } else {
            resultMap.put("message", "is empty");
            return resultMap;
        }
    }

    /**
     * 判断字符串是否为二进制
     *
     * @param str
     * @return
     */
    private boolean isBinary(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) != '0' && str.charAt(i) != '1') {
                return false;
            }
        }
        return true;
    }

    /**
     * @param @param fileName    设定文件
     * @return void    返回类型
     * @throws
     * @Title: sendIPAddressLibMessage
     * @Description: 发策略(这里用一句话描述这个方法的作用)
     */
    private String sendIPAddressLibMessage(String fileName, Long versionNo, Integer serverId) {
        ZongFenDevice zongFenDevice = zongFenDeviceMapper.getZongfenDevByPrimary(serverId);
        List<ClassFileSendMessage> classFileMessage = classInfoService.getMessageByTypes(MessageType.WEB_CLASS_TABLE_POLICY.getId(), zongFenDevice);
        if (classFileMessage != null) {
            try {
                for (ClassFileSendMessage tem : classFileMessage) {
                    //上传文件到SFTP服务器
                    String src = System.getProperty("user.dir") + "/productFile/" + fileName;
                    String dst = fileName;
                    ChannelSftp chSftp = SftpClientUtil.getChannel(tem.getUsername(), tem.getIp(), tem.getPassword(), tem.getPort());
                    chSftp.put(src, dst, ChannelSftp.OVERWRITE);//上传文件到目前用户根目录
                    chSftp.quit();
                }
                SftpClientUtil.closeChannel();
                AlarmClassInfoUtil.getInstance().cancleAlarmByType(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId());//取消消息提示
            } catch (Exception e) {
                LOG.error("sftp upload fail !");
                return "sftp_error";
            }
            String ip = null;
            Integer port = null;
            String username = null;
            String password = null;
            if (zongFenDevice.getIsVirtualIp() == 1) {//负载均衡
                ip = zongFenDevice.getZongfenIp();
                port = zongFenDevice.getZongfenFtpPort();
                username = classFileMessage.get(0).getUsername();
                password = classFileMessage.get(0).getPassword();
            } else if (zongFenDevice.getIsVirtualIp() == 0 && classFileMessage.size() == 1) {//非负载均衡
                ip = classFileMessage.get(0).getIp();
                port = classFileMessage.get(0).getPort();
                username = classFileMessage.get(0).getUsername();
                password = classFileMessage.get(0).getPassword();
            }
            if (ip != null && port != null && username != null && password != null) {//可以成功发策略
                //1:设置策略的对象值
                ClassInfoLibsStrategy appNameStrategy = new ClassInfoLibsStrategy();
                appNameStrategy.setMessageNo(MessageNoUtil.getInstance().getMessageNo(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId()));
                appNameStrategy.setMessageSequenceNo(MessageSequenceNoUtil.getInstance().getSequenceNo(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId()));
                appNameStrategy.setMessageType(MessageType.IP_ADDRESS_ALLOCATION_POLICY.getId());
                appNameStrategy.setOperationType(OperationConstants.OPERATION_SAVE);
                appNameStrategy.setProbeType(ProbeType.DPI.getValue());
                appNameStrategy.setIp(ip);
                appNameStrategy.setPort(port);
                appNameStrategy.setUserName(username);
                appNameStrategy.setPassword(password);
                appNameStrategy.setVersionNo(versionNo);
                appNameStrategy.setFileName(fileName);
                appNameStrategy.setZongfenId(zongFenDevice.getZongfenId());
                //2:发策略
                classInfoLibsStrategyService.addPolicy(appNameStrategy);

                return "success";
            } else {//不能成功发策略
                LOG.error("it has no sftp user infomations !");
                return "no_dev";
            }
        } else {
            LOG.error("it has no sftp user infomations !");
            return "no_dev";
        }
    }

    @Override
    public List<GenIPAddress> getIpV4() {
        return genIPAddressMapper.selectIpV4();
    }

    @Override
    public List<GenIPAddress> getIpV6() {
        return genIPAddressMapper.selectIpV6();
    }


}
