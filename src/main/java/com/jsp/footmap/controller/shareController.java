package com.jsp.footmap.controller;import com.jsp.footmap.model.HotShare;import com.jsp.footmap.model.ResponseDetail;import com.jsp.footmap.model.ResponseShare;import com.jsp.footmap.model.Share;import com.jsp.footmap.service.ShareService;import com.jsp.footmap.utils.Response;import com.jsp.footmap.utils.footMapUtils;import com.jsp.footmap.utils.jsonUtils;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.beans.factory.annotation.Autowired;import org.springframework.stereotype.Controller;import org.springframework.util.StreamUtils;import org.springframework.web.bind.annotation.*;import org.springframework.web.multipart.MultipartFile;import javax.servlet.http.HttpServletResponse;import java.io.File;import java.io.FileInputStream;import java.util.List;import java.util.Map;@RequestMapping("/share")@Controllerpublic class shareController {    private static final Logger logger = LoggerFactory.getLogger(shareController.class);    @Autowired    private ShareService shareService;    /**     * 新增     * @param jsonStr     * @return     */    @RequestMapping(path = "/add", method = {RequestMethod.POST})    @ResponseBody    public Response addShare(@RequestBody String jsonStr) {        try {            Map<String, String> map = jsonUtils.StrToMap(jsonStr);            String subject = map.get("subject");            String contents = map.get("contents");            shareService.addShare(subject, contents);            return new Response(0, "新增成功");        } catch (Exception e) {            logger.error("addShare:" , e);            return new Response(1, "新增失败");        }    }    /**     * 获取所有share     * @return     */    @RequestMapping(path = "/get", method = {RequestMethod.GET})    @ResponseBody    public Response getShares() {        try {            List<ResponseShare> shares = shareService.getShares();            return new Response(0, "获取成功", shares);        } catch (Exception e) {            logger.error("getShares:" , e);            return new Response(1,"获取失败");        }    }    /**     * 获取share的详细信息     * @param sid     * @return     */    @RequestMapping("/detail")    @ResponseBody    public Response getDetail(@RequestParam("sid") int sid) {        try {            ResponseDetail detail = shareService.getDetail(sid);            return new Response(0, "获取成功", detail);        } catch (Exception e) {            logger.error("getDetail:" , e);            return new Response(1, "获取失败");        }    }    @RequestMapping("/dele")    @ResponseBody    public Response deleShare(@RequestParam("sid") int sid) {        try {            shareService.deleShare(sid);            return new Response(0, "删除成功");        } catch (Exception e){            logger.error("deleShare:" , e);            return new Response(1, "删除失败");        }    }    @RequestMapping("/getHot")    @ResponseBody    public Response getHotShare() {        try {            List<HotShare> hotShare = shareService.getHotShare();            return new Response(0, "获取成功", hotShare);        } catch (Exception e) {            logger.error("getHotShare:" , e);            return new Response(1, "获取失败");        }    }    /**     * 上传文件     * @param file     * @return     */    @RequestMapping(path = "/upload", method = {RequestMethod.POST})    @ResponseBody    public Response uploadImg(@RequestParam("file") MultipartFile file) {        try {            String fileUrl = shareService.saveFile(file);            if (fileUrl == null) {                return new Response(1, "文件格式不正确");            }            return new Response(0, "上传成功", fileUrl);        } catch (Exception e) {            logger.error("upload error:", e);            return new Response(1, "上传失败");        }    }    /**     * 下载文件     * @return     */    @RequestMapping("/download")    @ResponseBody    public Response downloadImg(@RequestParam("filename") String filename, HttpServletResponse response) {        try {            response.setContentType("image/jpeg");            File file = new File(footMapUtils.FILE_DIR + filename);            if (!file.exists()) {                return new Response(1, "找不到指定文件");            }            StreamUtils.copy(new FileInputStream(file), response.getOutputStream());            return null;        } catch (Exception e) {            logger.error("download error:", e);            return new Response(1, "下载失败");        }    }}