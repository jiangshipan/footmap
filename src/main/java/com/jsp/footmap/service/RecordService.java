package com.jsp.footmap.service;import com.jsp.footmap.controller.MailController;import com.jsp.footmap.dao.EmailDao;import com.jsp.footmap.dao.RecordDao;import com.jsp.footmap.dao.TokenDao;import com.jsp.footmap.model.Record;import com.jsp.footmap.model.Task;import com.jsp.footmap.model.TaskHolder;import com.jsp.footmap.model.Token;import com.jsp.footmap.utils.DateUtils;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Service;import org.springframework.util.StringUtils;import java.util.*;@Servicepublic class RecordService {    @Autowired    private TokenDao tokenDAO;    @Autowired    private RecordDao recordDAO;    @Autowired    private EmailDao emailDAO;    @Autowired    private EmailService emailService;    public Map<String,String> addRecord(String targetTime, String remarks, String targetPlace,                                        String auth_token, String startTime,String title) {        Map<String ,String > map = new HashMap<String ,String >();        if (StringUtils.isEmpty(targetTime)) {            map.put("msg", "目标时间不能为空");            return map;        }        if (StringUtils.isEmpty(startTime)) {            map.put("msg", "开始时间不能为空");            return map;        }        if (StringUtils.isEmpty(targetPlace)) {            map.put("msg", "地点不能为空");            return map;        }        if (StringUtils.isEmpty(title)) {            map.put("msg", "主题不能为空");            return map;        }        Record userRecord = new Record();        Date targetDate = DateUtils.StringToDate3(targetTime);        Date startDate = DateUtils.StringToDate3(startTime);        Token token = tokenDAO.selectByToken(auth_token);        userRecord.setUid(token.getUid());        userRecord.setRemarks(remarks);        userRecord.setTargetPlace(targetPlace);        userRecord.setTargetTime(targetDate);        userRecord.setStartTime(startDate);        userRecord.setTitle(title);        //设置状态为0        userRecord.setStatus(0);        recordDAO.addRecord(userRecord);        map.put("msg", "新增成功");        // todo 若中途修改了时间？？如何处理        Task task = Task.getTask();        task.produceTask(userRecord);        return map;    }    /**     * 时间地点匹配后，返回提醒     * @param currentPlace     * @param auth_token     */    public boolean isOk(String currentPlace, String auth_token) {        Token token = tokenDAO.selectByToken(auth_token);        List<Record> records = recordDAO.getRecordByUid(token.getUid(), currentPlace);        //获取现在的时间        Date date = new Date();        Date currentDate = DateUtils.StringToDate3(DateUtils.DateToString(date));        for (Record r : records) {            String targetTime = DateUtils.DateToString(r.getTargetTime());            String currentTime = DateUtils.DateToString(currentDate);            System.out.println("目标:" + targetTime + ": 现在:" + currentTime);            if (targetTime.equals(currentTime)) {                return true;            }        }        return false;    }    /**     * 得到某人所有计划（按照截至日期排序）     * @param auth_token     * @return     */    public List<Record> getRecord(String auth_token) {        Token token = tokenDAO.selectByToken(auth_token);        List<Record> records = recordDAO.getRecordsByUid(token.getUid());        for (Record record : records) {            record.setStartTime(DateUtils.addEightHour(record.getStartTime()));            record.setTargetTime(DateUtils.addEightHour(record.getTargetTime()));        }        Collections.sort(records, new Comparator<Record>() {            @Override            public int compare(Record o1, Record o2) {                return o1.getTargetTime().compareTo(o2.getTargetTime());            }        });        return records;    }    /**     * 根据rid修改状态为1     * @param rid record对应id     */    public void finishRecord(int rid) {        recordDAO.updateStatus(rid, 1);    }    /**     * 删除record     * @param rid     */    public void deleRecord(int rid) {        recordDAO.updateStatus(rid, -1);    }    public Map<String, String> updateRecord(String targetTime, String remarks, String targetPlace, String beginTime, String title, int rid) {        Map<String ,String > map = new HashMap<String ,String >();        if (StringUtils.isEmpty(targetTime)) {            map.put("msg", "目标时间不能为空");            return map;        }        if (StringUtils.isEmpty(beginTime)) {            map.put("msg", "开始时间不能为空");            return map;        }        if (StringUtils.isEmpty(targetPlace)) {            map.put("msg", "地点不能为空");            return map;        }        if (StringUtils.isEmpty(title)) {            map.put("msg", "主题不能为空");            return map;        }        Record userRecord = new Record();        Date targetDate = DateUtils.StringToDate3(targetTime);        Date startDate = DateUtils.StringToDate3(beginTime);        userRecord.setRid(rid);        userRecord.setRemarks(remarks);        userRecord.setTargetPlace(targetPlace);        userRecord.setTargetTime(targetDate);        userRecord.setStartTime(startDate);        userRecord.setTitle(title);        recordDAO.updateRecord(userRecord);        map.put("msg", "修改成功");        return map;    }    /**     * 提醒     * @param record     */    public boolean dealWithRecord(Record record){       try {           boolean flag = true;           int num = 0;           //返回执行结果           boolean result = false;           //轮询超过五次返回。重新执行           while(flag && num < 5) {               //获取现在的时间               Date date = new Date();               Date currentDate = DateUtils.StringToDate3(DateUtils.DateToString(date));               //目标时间               String targetTime = DateUtils.DateToString(record.getTargetTime());               //当前时间               String currentTime = DateUtils.DateToString(currentDate);               System.out.println("目标:" + targetTime + ": 现在:" + currentTime);               if (targetTime.equals(currentTime)) {                   //结束循环                   flag = false;                   num = 0;                   //执行成功                   result = true;                   //获取此人邮箱                   String email = emailDAO.selectEmailByUid(record.getUid());                   emailService.remindUser(record, email);               }               num ++;               Thread.sleep(60000);           }           return result;       } catch (Exception e) {           e.printStackTrace();           return false;       }    }    /**     * 得到比现在靠前的record 并且未完成  仅仅获取10个。     * @return     */    public List<Record> getAllRecords() {        Date date = new Date();        List<Record> allRecords = recordDAO.getAllRecords(date);        //按照时间排序        Collections.sort(allRecords, new Comparator<Record>() {            @Override            public int compare(Record o1, Record o2) {                return o1.getTargetTime().compareTo(o2.getTargetTime());            }        });        if (allRecords.size() > 10) {            return allRecords.subList(0,10);        } else {            return allRecords;        }    }}