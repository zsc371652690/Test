package com.wellness.checkin.autopass;

import com.ejlchina.okhttps.HttpResult;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellness.qa.api.checkIn.AutoPassAPi;
import com.wellness.qa.apiUtils.db.JdbcUtil;
import com.wellness.qa.apiUtils.db.MyBeanProcessor;
import com.wellness.qa.db.DBUtil;
import com.wellness.qa.request.checkIn.AddCourseScheduleRequest;
import com.wellness.qa.request.checkIn.AutoPassRequest;
import com.wellness.qa.util.RandomUtils;
import com.wellness.qa.util.TokenUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("智能门禁进场")
@Epic("智能门禁")
@Feature("智能门禁进场")
@Slf4j
public class CheckIn {

    private static long userId = 1000329002L;

    private static long interestUserId = 1000038001L;

    private static String mId = "M00100611";

    private static Connection conn;

    private static QueryRunner qr;

    private static MyBeanProcessor bean;

    private static RowProcessor processor ;

    private static ObjectMapper mapper;

    private static String token;

    @BeforeAll
    public static void beforeAll() throws SQLException {
//
        conn = DBUtil.getConnection("interest");
        qr = new QueryRunner();
        bean = new MyBeanProcessor();
        processor = new BasicRowProcessor(bean);
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        token =  TokenUtil.getToken(userId,7);

        qr.update(conn,"UPDATE 1012wellness_user.employees_info SET mobile = '15895251238' WHERE mobile = '15895251237';");
        qr.update(conn,"UPDATE wellness_interests.user_interest SET user_id = '10000380011' WHERE user_id = '1000038001';");
        qr.update(conn,"UPDATE 1012wellness_user.member_info_wellness SET user_id = '10000380011' WHERE user_id = '1000038001';");
        qr.update(conn,"UPDATE 1012wellness_gymnaestrada.blank_company_member SET is_active = 1 WHERE sub_mobile = '15895251237' ;");
        qr.update(conn,"UPDATE 1012wellness_user.supreme_card_user_relation SET is_active = 0 WHERE slave_user_id = '1000038001' ;");

    }
    @AfterAll
    public static void  afterAll(){
        JdbcUtil.close(conn);
    }

    @Nested
    @DisplayName("纯ERP会员checkIn")
    public class CheckInWithERP{

        @DisplayName("重置用户信息")
        @BeforeEach
        public  void beforeEach() throws SQLException, InterruptedException {
            qr.update(conn,"UPDATE 1012wellness_user.member_info_wellness SET user_id = '1000038001' WHERE user_id = '10000380011';");
            qr.update(conn,"UPDATE wellness_checkin.wellness_door_control_record \n" +
                    "SET action_time = DATE_SUB( action_time, INTERVAL 1 DAY ) \n" +
                    "WHERE\n" +
                    "(user_id = '1000038001' \n" +
                    "OR\n" +
                    "member_code = 'M00100611') \n" +
                    "AND \n" +
                    "DATE_FORMAT( action_time, '%Y-%M-%d' ) = DATE_FORMAT( NOW(), '%Y-%M-%d' );");
        }

        @DisplayName("还原用户数据")
        @AfterEach
        public void afterEach() throws SQLException {
            qr.update(conn,"UPDATE 1012wellness_user.member_info_wellness SET user_id = '10000380011' WHERE user_id = '1000038001';");
        }
        @DisplayName("纯ERP会员UID进场")
        @Test
        public void checkInWithUId() throws SQLException {

            qr.update(conn,"UPDATE 1012wellness_user.member_info_wellness SET user_id = '10000380011' WHERE user_id = '1000038001';");
            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setMemberCode(mId);
            autoPassRequest.setPassTicket("U"+interestUserId);
            autoPassRequest.setPassTicketType(8);
            autoPassRequest.setUserId(interestUserId);
            autoPassRequest.setStoreCode("08001");

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(2)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }

        @DisplayName("纯ERP会员未绑卡M号进场")
        @Test
        public void checkInWithMId(){

            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setMemberCode(mId);
            autoPassRequest.setPassTicket(mId);
            autoPassRequest.setPassTicketType(7);
            autoPassRequest.setStoreCode("08001");

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(2)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }

        @DisplayName("纯ERP会员绑卡M号进场")
        @Test
        public void checkInWithoutUId(){

            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setMemberCode(mId);
            autoPassRequest.setPassTicket(mId);
            autoPassRequest.setPassTicketType(7);
            autoPassRequest.setUserId(interestUserId);
            autoPassRequest.setStoreCode("08001");

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(2)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }

        @DisplayName("纯ERP会员会员码进场")
        @Test
        public void checkInWithMemCode(){


            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setPassTicket(RandomUtils.randomEnc(interestUserId,System.currentTimeMillis()));
            autoPassRequest.setPassTicketType(2);
            autoPassRequest.setStoreCode("08001");
            autoPassRequest.setMemberCode(mId);

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(2)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }
    }

    @Nested
    @DisplayName("纯权益会员checkIn")
    public class CheckInWithInterest{

        @DisplayName("重置用户信息")
        @BeforeEach
        public  void beforeEach() throws SQLException, InterruptedException {
            //激活纯权益会员
            qr.update(conn,"UPDATE wellness_interests.user_interest \n" +
                    "SET user_id = '1000038001' \n" +
                    "WHERE\n" +
                    "user_interest_id =(\n" +
                    "SELECT\n" +
                    "id \n" +
                    "FROM\n" +
                    "(\n" +
                    "SELECT\n" +
                    "ui.user_interest_id as  id\n" +
                    "FROM\n" +
                    "wellness_interests.user_interest ui\n" +
                    "LEFT JOIN wellness_interests.tdgroup_detail td ON ui.tdgroup_code = td.tdgroup_code \n" +
                    "WHERE\n" +
                    "td.store_code = '08001' \n" +
                    "AND user_id = '10000380011' \n" +
                    "AND interest_class = 'admission' \n" +
                    "AND `status` = 1 \n" +
                    "AND end_time > NOW() \n" +
                    "GROUP BY\n" +
                    "ui.user_interest_id \n" +
                    "ORDER BY\n" +
                    "RAND() \n" +
                    "LIMIT 1 \n" +
                    ") a)");
            //清除当天进出场记录
            qr.update(conn,"UPDATE wellness_checkin.wellness_door_control_record \n" +
                    "SET action_time = DATE_SUB( action_time, INTERVAL 1 DAY ) \n" +
                    "WHERE\n" +
                    "(user_id = '1000038001' \n" +
                    "OR\n" +
                    "member_code = 'M00100611') \n" +
                    "AND \n" +
                    "DATE_FORMAT( action_time, '%Y-%m-%d' ) = DATE_FORMAT( NOW(), '%Y-%m-%d' );");
            //因20秒内再次进场不判断会籍权益

        }

        @DisplayName("还原用户数据")
        @AfterEach
        public void afterEach() throws SQLException {
            qr.update(conn,"UPDATE wellness_interests.user_interest SET user_id = '10000380011' WHERE user_id = '1000038001';");
        }

        @DisplayName("纯权益会员UID进场")
        @Test
        public void checkInWithUId(){

            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setPassTicket("U"+interestUserId);
            autoPassRequest.setPassTicketType(8);
            autoPassRequest.setUserId(interestUserId);
            autoPassRequest.setStoreCode("08001");

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(1)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }

        @DisplayName("纯权益会员会员码进场")
        @Test
        public void checkInWithMemCode(){


            AutoPassRequest autoPassRequest = new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setPassTicket(RandomUtils.randomEnc(interestUserId,System.currentTimeMillis()));
            autoPassRequest.setPassTicketType(2);
            autoPassRequest.setStoreCode("08001");
            autoPassRequest.setUserId(interestUserId);

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(1)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }
        }


    }

    @Nested
    @DisplayName("员工进场")
    public class CheckInWithEmployee{

        @DisplayName("重置用户信息")
        @BeforeEach
        public void beforeEach() throws SQLException {
            qr.update(conn,"UPDATE 1012wellness_user.employees_info SET mobile = '15895251237' WHERE mobile = '15895251238';");
            //清除当天进出场记录
            qr.update(conn,"UPDATE wellness_checkin.wellness_door_control_record \n" +
                    "SET action_time = DATE_SUB( action_time, INTERVAL 1 DAY ) \n" +
                    "WHERE\n" +
                    "(user_id = '1000038001' \n" +
                    "OR\n" +
                    "member_code = 'M00100611') \n" +
                    "AND \n" +
                    "DATE_FORMAT( action_time, '%Y-%M-%d' ) = DATE_FORMAT( NOW(), '%Y-%M-%d' );");

        }

        @DisplayName("还原用户数据")
        @AfterEach
        public void afterEach() throws SQLException {
            qr.update(conn,"UPDATE wellness_interests.user_interest SET user_id = '10000380011' WHERE user_id = '1000038001';");
        }

        @DisplayName("正式员工进场")
        @Test
        public void checkInWithEmployee(){
           AutoPassRequest autoPassRequest =new AutoPassRequest();
           autoPassRequest.setPassKind(1);
           autoPassRequest.setForced(false);
           autoPassRequest.setPassTicket("U"+interestUserId);
           autoPassRequest.setPassTicketType(8);
           autoPassRequest.setUserId(interestUserId);
           autoPassRequest.setStoreCode("08001");

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(3)
                                .getString("success"), true),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }

        }

        @DisplayName("团操编外进场")
        @Test
        public void checkInWithGY() throws SQLException {

            //清除当天进出场记录
            qr.update(conn,"UPDATE wellness_checkin.wellness_door_control_record \n" +
                    "SET action_time = DATE_SUB( action_time, INTERVAL 1 DAY ) \n" +
                    "WHERE\n" +
                    "user_id = '1000276001' \n" +
                    "AND \n" +
                    "DATE_FORMAT( action_time, '%Y-%m-%d' ) = DATE_FORMAT( NOW(), '%Y-%m-%d' );");

            //清除测试用户下已有团操课
            qr.update(conn,"UPDATE 1012wellness_gymnaestrada.course_schedule \n" +
                    "SET `status` = 7 \n" +
                    "WHERE\n" +
                    "\tid IN (\n" +
                    "\tSELECT\n" +
                    "\t\ta.id \n" +
                    "\tFROM\n" +
                    "\t\t(\n" +
                    "\t\tSELECT\n" +
                    "\t\t\tcs.id AS id \n" +
                    "\t\tFROM\n" +
                    "\t\t\t1012wellness_gymnaestrada.course_schedule cs\n" +
                    "\t\t\tLEFT JOIN 1012wellness_gymnaestrada.course_schedule_coach csc ON cs.id = csc.course_sehedule_id \n" +
                    "\t\tWHERE\n" +
                    "\t\t\tcsc.employee_code = '80001158' \n" +
                    "\t\tAND cs.course_date = DATE_FORMAT( NOW(), '%Y-%m-%d' )\n" +
                    "\t\t) a \n" +
                    "\t);");


            AddCourseScheduleRequest addCourseScheduleRequest = new AddCourseScheduleRequest();
            addCourseScheduleRequest.setCourseId(1);
            addCourseScheduleRequest.setCapacity("20");
            addCourseScheduleRequest.setCategoryId(1);
            addCourseScheduleRequest.setChargesNature("1");
            addCourseScheduleRequest.setCategoryName("训练类课程");
            addCourseScheduleRequest.setChargesNatureCode("会员免费");
            addCourseScheduleRequest.setCoachId("1000276001");
            addCourseScheduleRequest.setCoachName("碧咸");
            addCourseScheduleRequest.setCourseDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            addCourseScheduleRequest.setCourseDuration(30);
            addCourseScheduleRequest.setCourseName("身体充电");
            addCourseScheduleRequest.setCourseNature("1");
            addCourseScheduleRequest.setCourseTime("23:00:00");
            addCourseScheduleRequest.setFieldCode("P8001002");
            addCourseScheduleRequest.setFieldName("团操房");
            addCourseScheduleRequest.setStoreCode("08001");
            addCourseScheduleRequest.setStoreName("上海平凉会所");

            //手动添加团操课
            AutoPassAPi.addCourseSchedule(addCourseScheduleRequest,TokenUtil.getToken(userId,5));

            //审核通过课表
            long ids = qr.query(conn,"SELECT\n" +
                    "\tcs.id \n" +
                    "FROM\n" +
                    "\t1012wellness_gymnaestrada.course_schedule cs\n" +
                    "\tLEFT JOIN 1012wellness_gymnaestrada.course_schedule_coach csc ON cs.id = csc.course_sehedule_id \n" +
                    "WHERE\n" +
                    "\tcsc.employee_code = '80001158' \n" +
                    "\tAND cs.`status` = 2 \n" +
                    "\tAND cs.course_date = DATE_FORMAT( NOW(), '%Y-%m-%d' );",new ScalarHandler<Long>());
            AutoPassAPi.batchAudit(ids,TokenUtil.getToken(userId,5));

            //团操编外进场入参设置
            AutoPassRequest autoPassRequest =new AutoPassRequest();
            autoPassRequest.setPassKind(1);
            autoPassRequest.setForced(false);
            autoPassRequest.setPassTicket("U"+"1000276001");
            autoPassRequest.setPassTicketType(8);
            autoPassRequest.setStoreCode("08001");
            autoPassRequest.setUserId(1000276001L);

            try {
                HttpResult httpResult = AutoPassAPi.autoPassApi(autoPassRequest,token);

                assertAll(
                        //快速获取response某个属性
                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(4)
                                .getString("success"), true),

                        ()->assertThat(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getArray("unitResponses")
                                .getMapper(4)
                                .getString("trace"), equalTo("团操编外校验:当日团操教练,校验通过")),

                        ()->assertThat("CHECKIN",equalTo(httpResult.getBody().cache()
                                .toMapper()
                                .getMapper("data")
                                .getString("passKind")))
                );
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行失败");
            }

        }
    }


}
