package com.wellness.admin.tdGroup;

import com.google.common.collect.ImmutableMap;
import com.wellness.qa.api.AddTdGroupApi;
import com.ejlchina.okhttps.HttpResult;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellness.qa.apiUtils.db.JdbcUtil;
import com.wellness.qa.apiUtils.db.MyBeanProcessor;
import com.wellness.qa.db.DBUtil;
import com.wellness.qa.util.HttpResultChecker;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.junit.jupiter.api.*;
import com.wellness.qa.request.AddTdGroupRequest;
import com.wellness.qa.util.TokenUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


@DisplayName("通店组管理")
@Epic("通店组")
@Feature("通店组管理")
@Slf4j
public class AddGroupTest {

    private static long userId = 1000247004L;

    private static Connection conn;

    private static QueryRunner qr;

    private static MyBeanProcessor bean;

    private static RowProcessor processor ;

    private static ObjectMapper mapper;

    private static String token;

    @BeforeAll
    public static void beforeAll(){
//
        conn = DBUtil.getConnection("interest");
        qr = new QueryRunner();
        bean = new MyBeanProcessor();
        processor = new BasicRowProcessor(bean);
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        token =  TokenUtil.getToken(userId,5);

    }

    @AfterAll
    static void afterAll() throws SQLException {
        JdbcUtil.close(conn);
    }

    @DisplayName("新增通店组成功")
    @Nested
    public class AddGroup{

        @DisplayName("新增单个门店成功")
        @Test
        public void addGroupSuccess() throws SQLException {

            AddTdGroupRequest addTdGroupRequest = new AddTdGroupRequest();
            List<Object> list  = new ArrayList<>();
            HashMap<String,String> map =new HashMap<>();
            map.put("storeCode","07087");
            map.put("storeName","上海联洋");
            list.add(map);
            addTdGroupRequest.setStoreList(list);
            addTdGroupRequest.setTdgroupName("自动化测试"+System.currentTimeMillis());
            addTdGroupRequest.setTdgroupCode("TD"+System.currentTimeMillis());
            addTdGroupRequest.setTdgroupDesc(String.valueOf(System.currentTimeMillis()));

            try {
                HttpResult httpResult = AddTdGroupApi.addTdGroupRequest(addTdGroupRequest,token);
                new HttpResultChecker(httpResult).success();
            }catch (Exception e)
            {
                e.printStackTrace();
                log.info("执行异常");
            }

            List<Map<String, Object>> db = qr.query(conn,
                    "\n" +
                            "SELECT\n" +
                            "tg.tdgroup_code AS tdgroupCode,\n" +
                            "tg.tdgroup_name AS tdgroupName,\n" +
                            "tg.tdgroup_desc AS tdgroupDesc,\n" +
                            "store_code,\n" +
                            "store_name \n" +
                            "FROM\n" +
                            "tdgroup tg\n" +
                            "LEFT JOIN tdgroup_detail tgd ON tg.tdgroup_code = tgd.tdgroup_code \n" +
                            "WHERE\n" +
                            "tg.tdgroup_code = ?;",
                    new MapListHandler(),addTdGroupRequest.getTdgroupCode());

            for (Map<String, Object> map1 : db) {
                assertAll(
                        ()->assertThat(map1.get("tdgroupCode").toString(), equalTo( addTdGroupRequest.getTdgroupCode())),
                        ()->assertThat(map1.get("tdgroupName").toString(), equalTo( addTdGroupRequest.getTdgroupName())),
                        ()->assertThat(map1.get("tdgroupDesc").toString(), equalTo( addTdGroupRequest.getTdgroupDesc())),
                        ()->assertThat(map1.get("store_code").toString(), equalTo(map.get("storeCode"))),
                        ()->assertThat(map1.get("store_name").toString(), equalTo( map.get("storeName")))
                );
            }
        }
    }

        @DisplayName("新增多个门店成功")
        @Test
        public void addGroupSuccessWithManyStoreCode() throws SQLException {
        AddTdGroupRequest addTdGroupRequest = new AddTdGroupRequest();
        List<Object> list  = new ArrayList<>();
        Map<String,String> mapOne = ImmutableMap.of("storeCode","07087", "storeName","上海联洋");
        Map<String,String> mapTwo = ImmutableMap.of("storeCode","08001", "storeName","上海平凉");
        list.add(mapOne);
        list.add(mapTwo);

//        ImmutableList.of(ImmutableMap.of("storeCode","07087", "storeName","上海联洋"), ImmutableMap.of("storeCode","08001", "storeName","上海平凉"));

        addTdGroupRequest.setStoreList(list);
        addTdGroupRequest.setTdgroupName("自动化测试"+System.currentTimeMillis());
        addTdGroupRequest.setTdgroupCode("TD"+System.currentTimeMillis());
        addTdGroupRequest.setTdgroupDesc(String.valueOf(System.currentTimeMillis()));

        try {
            HttpResult httpResult = AddTdGroupApi.addTdGroupRequest(addTdGroupRequest,token);
            new HttpResultChecker(httpResult).success();
        }catch (Exception e)
        {
            e.printStackTrace();
            log.info("执行异常");
        }

        List<Map<String, Object>> db = qr.query(conn,
                "\n" +
                        "SELECT\n" +
                        "tg.tdgroup_code AS tdgroupCode,\n" +
                        "tg.tdgroup_name AS tdgroupName,\n" +
                        "tg.tdgroup_desc AS tdgroupDesc,\n" +
                        "store_code,\n" +
                        "store_name \n" +
                        "FROM\n" +
                        "tdgroup tg\n" +
                        "LEFT JOIN tdgroup_detail tgd ON tg.tdgroup_code = tgd.tdgroup_code \n" +
                        "WHERE\n" +
                        "tg.tdgroup_code = ?" +
                        "order by tgd.tdgroup_detail_id  ;",
                new MapListHandler(),addTdGroupRequest.getTdgroupCode());


        for(int i =0;i<db.size();i++){

            assertThat(db.get(i).get("tdgroupCode").toString(), equalTo( addTdGroupRequest.getTdgroupCode()));
            assertThat(db.get(i).get("tdgroupName").toString(), equalTo( addTdGroupRequest.getTdgroupName()));
            assertThat(db.get(i).get("tdgroupDesc").toString(), equalTo( addTdGroupRequest.getTdgroupDesc()));

            if (i == 0) {
                assertThat(db.get(i).get("store_code").toString(), equalTo(mapOne.get("storeCode")));
                assertThat(db.get(i).get("store_name").toString(), equalTo(mapOne.get("storeName")));
            }
            else {
                assertThat(db.get(i).get("store_code").toString(), equalTo(mapTwo.get("storeCode")));
                assertThat(db.get(i).get("store_name").toString(), equalTo(mapTwo.get("storeName")));
            }

        }

    }

    @DisplayName("新增通店组失败")
    @Nested
    public class AddGroupFail{

        @DisplayName("参数校验")
        @ParameterizedTest(name = "序号 [{index}]，tdgroupName参数 [{0}]，tdgroupCode参数 [{1}]，tdgroupDesc参数 [{2}]")
        @CsvSource(value = {
                "自动化测试999999999,TD999999999, 123",
                "NIL,TD999999999, 123",
                "自动化测试999999999,NIL, 123"
        },nullValues = "NIL")
        public void addGroupFailedTest(String tdgroupName,String tdgroupCode,String tdgroupDesc){

            AddTdGroupRequest addTdGroupRequest = new AddTdGroupRequest();
            addTdGroupRequest.setTdgroupName(tdgroupName);
            addTdGroupRequest.setTdgroupCode(tdgroupCode);
            addTdGroupRequest.setTdgroupDesc(tdgroupDesc);
            if(addTdGroupRequest.getTdgroupCode() == null ||addTdGroupRequest.getTdgroupName() == null){
                List<Object> list  = new ArrayList<>();
                Map<String,String> mapOne = ImmutableMap.of("storeCode","07087", "storeName","上海联洋");
                list.add(mapOne);
                addTdGroupRequest.setStoreList(list);
            }

            try {
                HttpResult httpResult = AddTdGroupApi.addTdGroupRequest(addTdGroupRequest,token);
//                System.out.println(httpResult.getBody().cache().toString());
                log.info("response:{}\n：",httpResult.getBody().cache().toString());
                new HttpResultChecker(httpResult).fail(httpResult.getBody().cache().toMapper().getString("errCode"),httpResult.getBody().cache().toMapper().getString("errMessage"),false);
            }catch (Exception e)
            {
                e.printStackTrace();
                log.info("执行异常");
            }
        }
    }
}
