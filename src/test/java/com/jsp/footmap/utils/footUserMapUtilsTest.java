package com.jsp.footmap.utils;import org.junit.Assert;import org.junit.Test;public class footUserMapUtilsTest {    @Test    public void testMD5() {        String password = "123456";        String result = footMapUtils.MD5(password);        Assert.assertEquals("验证成功","e10adc3949ba59abbe56e057f20f883e",result);    }}