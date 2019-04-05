package com.jsp.footmap.controller;import com.jsp.footmap.model.Record;import com.jsp.footmap.model.Task;import com.jsp.footmap.service.RecordService;import com.jsp.footmap.utils.Response;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.ResponseBody;import java.util.List;import java.util.concurrent.CopyOnWriteArrayList;@Controller@RequestMapping("/thread")public class ThreadController {    private static final Logger logger = LoggerFactory.getLogger(ThreadController.class);    private int runTimes = 0;    private boolean isRun = true;    @Autowired    private RecordService recordService;    @RequestMapping("/run")    @ResponseBody    public Response startAllThreads () {        if (runTimes < 1) {            new addThread().start();            new getThread().start();            runTimes ++;            return new Response(0, "开启成功");        } else {            return new Response(1, "已经开启");        }    }    @RequestMapping("/stop")    @ResponseBody    public Response stopAllThreads () {        if (isRun) {            isRun = false;            return new Response(0, "关闭成功");        } else {            return new Response(0, "已经关闭");        }    }    @RequestMapping("/test")    @ResponseBody    public Response test() {        return new Response(0, "获取ok", recordService.getAllRecords());    }    /**     * 添加任务线程。 每五分钟从数据库中读取     */    class addThread extends Thread {        @Override        public void run(){            try {                Task task = Task.getTask();                System.out.println("增加线程开启！");                while (isRun) {                    //清除原先内容                    task.cleanAllTask();                    List<Record> allRecords = recordService.getAllRecords();                    for (Record record : allRecords) {                        task.produceTask(record);                    }                    //设置pos为0                    task.setPos(0);                    Thread.sleep(5 * 60 * 1000);                }                System.out.println("增加线程停止！");            } catch (Exception e) {                logger.error("addThread:出现问题了" + e.getMessage());            }        }    }    class getThread extends Thread {        @Override        public void run() {            //获取task对象            Task task = Task.getTask();            try {                System.out.println("获取线程开启！");                while(isRun) {                    CopyOnWriteArrayList<Record> records = task.assumeTask();                    if(records != null && records.size() > 0) {                        //去处理 pos位置的任务                        int pos = task.getPos();                        boolean result = recordService.dealWithRecord(records.get(pos));                        if (result) {                            task.setPos(++pos);                        }                    }                }                System.out.println("获取线程停止！");            } catch (Exception e){                e.printStackTrace();            }        }    }}