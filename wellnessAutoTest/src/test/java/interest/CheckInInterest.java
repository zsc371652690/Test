package interest;

import com.ejlchina.okhttps.HttpResult;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellness.qa.api.interest.OpenInterestApi;
import com.wellness.qa.apiUtils.db.JdbcUtil;
import com.wellness.qa.apiUtils.db.MyBeanProcessor;
import com.wellness.qa.db.DBUtil;
import com.wellness.qa.request.interest.OpenInterestRequest;
import com.wellness.qa.util.HttpResultChecker;
import com.wellness.qa.util.TokenUtil;
import com.wellness.qa.util.common.DateUtil;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

@DisplayName("智能前台权益激活")
@Epic("智能前台权益")
@Feature("智能前台权益激活")
@Slf4j
public class CheckInInterest {

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
        token =  TokenUtil.getToken(userId,7);

    }

    @AfterAll
    static void afterAll() throws SQLException {
        JdbcUtil.close(conn);
    }


    @DisplayName("激活成功")
    @Nested
    public class InterestActivate{


        @DisplayName("待激活权益激活成功")
        @Test
        public  void interestActivateSuccessWithUnactivatedInterest() throws SQLException {

        //初始化数据
            qr.update(conn,"UPDATE user_interest \n" +
                    "SET start_time = NULL,\n" +
                    "end_time = NULL,\n" +
                    "`status` = 0 \n" +
                    "WHERE\n" +
                    "user_id = '1000038001' \n" +
                    "AND interest_class = 'admission' \n" +
                    "LIMIT 1;");

        //设置入参
            OpenInterestRequest openInterestRequest = new OpenInterestRequest();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(new Date());
            openInterestRequest.setActiveDate(dateStr);

            BigInteger interestId =qr.query(conn,"SELECT\n" +
                    "user_interest_id \n" +
                    "FROM\n" +
                    "user_interest \n" +
                    "WHERE\n" +
                    "user_id = '1000038001' \n" +
                    "AND interest_class = 'admission' \n" +
                    "AND `status` = 0 \n" +
                    "LIMIT 1;",new ScalarHandler<>());
            openInterestRequest.setUserInterestId(String.valueOf(interestId));
            try{
                HttpResult httpResult = OpenInterestApi.openInterestRequest(openInterestRequest,token);
                new HttpResultChecker(httpResult).success();
                Map<String,Object> map = qr.query(conn,"SELECT\n" +
                        "\tduration_type AS durationType,\n" +
                        "\tduration_amount AS durationAmount \n" +
                        "FROM\n" +
                        "\twellness_interests.interest \n" +
                        "WHERE\n" +
                        "\tinterest_id =(\n" +
                        "\tSELECT\n" +
                        "\t\tinterest_id \n" +
                        "\tFROM\n" +
                        "\t\twellness_interests.user_interest \n" +
                        "\tWHERE\n" +
                        "\tuser_interest_id =?);",new MapHandler(),interestId);

                Map<String,Object> mapInterest = qr.query(conn,"SELECT\n" +
                        "DATE_FORMAT(start_time,\"%Y-%m-%d\") AS startTime,\n" +
                        "DATE_FORMAT(end_time,\"%Y-%m-%d\") AS endTime\n" +
                        "FROM\n" +
                        "wellness_interests.user_interest \n" +
                        "WHERE\n" +
                        "user_interest_id = ? \n" +
                        "LIMIT 1;",new MapHandler(),interestId);

                switch (map.get("durationType").toString()){
                    case "0":
                        String endDateOne = DateUtil.dateAddDays(dateStr,Integer.parseInt(map.get("durationAmount").toString())-1);
                        Assertions.assertEquals(endDateOne,mapInterest.get("endTime"));
                        Assertions.assertEquals(dateStr,mapInterest.get("startTime"));
                        break;
                    case "1":
                        String endDateTwoStr = DateUtil.dateAddMonths(dateStr,Integer.parseInt(map.get("durationAmount").toString()));
                        String endDateTwo =DateUtil.dateAddDays(endDateTwoStr,-1);
                        Assertions.assertEquals(endDateTwo,mapInterest.get("endTime"));
                        Assertions.assertEquals(dateStr,mapInterest.get("startTime"));
                        break;
                    case "2":
                        String endDateThreeStr = DateUtil.dateAddYears(dateStr,Integer.parseInt(map.get("durationAmount").toString()));
                        String endDateThree =DateUtil.dateAddDays(endDateThreeStr,-1);
                        Assertions.assertEquals(endDateThree,mapInterest.get("endTime"));
                        Assertions.assertEquals(dateStr,mapInterest.get("startTime"));
                        break;
                    default:
                        log.info("durationType枚举值错误");

                }
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行异常");
            };
        }

        @DisplayName("暂停权益激活成功")
        @Test
        public  void interestActivateSuccessWithPauseInterest() throws SQLException, ParseException {

            int interestId = 9764;

            OpenInterestRequest openInterestRequest = new OpenInterestRequest();
            int random =new Random().nextInt(100);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(new Date());
            String dateStart = DateUtil.dateAddDays(dateStr,-random)+" 00:00:00";
            String dateEnd = DateUtil.dateAddDays(dateStr, random+100)+" 23:59:59";
            String pauseStart = DateUtil.dateAddDays(dateStr,-10)+" 00:00:00";

            //初始化数据
            qr.update(conn,"UPDATE user_interest \n" +
                    "SET start_time = ?,\n" +
                    "end_time = ?,\n" +
                    "`status` = -3 \n" +
                    "WHERE user_id = '1000038001' \n" +
                    "AND interest_class = 'admission' \n" +
                    "AND user_interest_id = ? ;",dateStart,dateEnd,interestId);

            qr.insert(conn,"INSERT INTO `wellness_interests`.`user_interest_pause` ( `user_interest_id`, `pause_status`, `pause_time`, `resume_time`, `pause_days`, `create_time`, `update_time`, `is_active` )\n" +
                    "VALUES\n" +
                    "\t( ?, 2, ?, NULL, NULL, ?, ?, 1 );",new ScalarHandler<Number>(),interestId,pauseStart,pauseStart,pauseStart);


            openInterestRequest.setUserInterestId(String.valueOf(interestId));
            try{
                HttpResult httpResult = OpenInterestApi.openInterestRequest(openInterestRequest,token);
                new HttpResultChecker(httpResult).success();

                Map<String,Object> mapInterest = qr.query(conn,"SELECT\n" +
                        "DATE_FORMAT(start_time,\"%Y-%m-%d %H:%i:%s\") AS startTime,\n" +
                        "DATE_FORMAT(end_time,\"%Y-%m-%d %H:%i:%s\") AS endTime\n" +
                        "FROM\n" +
                        "wellness_interests.user_interest \n" +
                        "WHERE\n" +
                        "user_interest_id = ? \n" +
                        "LIMIT 1;",new MapHandler(),interestId);

                String endDate = DateUtil.dateAddDays(dateEnd,10) +" 23:59:59";
                Assertions.assertEquals(endDate,mapInterest.get("endTime"));
                Assertions.assertEquals(dateStart,mapInterest.get("startTime"));
            }catch (Exception e){
                e.printStackTrace();
                log.info("执行异常");
            };
        }
    }

    @DisplayName("参数校验")
    @Nested
    public class InterestParameterCheck{

        @DisplayName("空值校验")
        @ParameterizedTest(name = "序号 [{index}]，interestId参数 [{0}]，activeDate参数 [{1}],message参数[{2}]]")
        @CsvSource(value = {
                "NIL,2023-02-11,激活权益id不能为空",
        },nullValues = "NIL")
        public void parameterIsNull(String interestId,String activeDate,String message) throws SQLException {

            //初始化数据
            qr.update(conn,"UPDATE user_interest \n" +
                    "SET start_time = NULL,\n" +
                    "end_time = NULL,\n" +
                    "`status` = 0 \n" +
                    "WHERE\n" +
                    "user_id = '1000038001' \n" +
                    "AND interest_class = 'admission' \n" +
                    "LIMIT 1;");

            OpenInterestRequest openInterestRequest = new OpenInterestRequest();
            openInterestRequest.setUserInterestId(interestId);
            openInterestRequest.setActiveDate(activeDate);
            HttpResult httpResult =  OpenInterestApi.openInterestRequest(openInterestRequest,token);
            new HttpResultChecker(httpResult).success(message,false);
        }

        @DisplayName("参数不存在")
        @ParameterizedTest(name = "序号 [{index}]，interestId参数 [{0}]，activeDate参数 [{1}],message参数[{2}]")
        @CsvSource(value = {
                "9999999999,2023-02-11,未查询到权益",
        },nullValues = "NIL")
        public void parameterIsError(String interestId,String activeDate,String message){
            OpenInterestRequest openInterestRequest = new OpenInterestRequest();
            openInterestRequest.setUserInterestId(interestId);
            openInterestRequest.setActiveDate(activeDate);
            HttpResult httpResult =  OpenInterestApi.openInterestRequest(openInterestRequest,token);
            new HttpResultChecker(httpResult).success(message,false);
        }

        @DisplayName("权益状态不正确")
        @ParameterizedTest(name = "序号 [{index}]，status参数 [{1}]")
        @CsvSource(value = {
                "9764,2023-02-10 00:00:00,-1",
                "9764,2023-02-10 00:00:00,-2",
                "9764,2023-02-10 00:00:00,-4",
        })
        public void interestStatusIsError(String interestId, String activeDate, int status) throws SQLException {

            //初始化数据
            qr.update(conn,"UPDATE user_interest \n" +
                    "SET start_time = '2023-01-01 00:00:00',\n" +
                    "end_time = '2099-01-01 23:59:59',\n" +
                    "`status` = ? \n" +
                    "WHERE\n" +
                    "user_id = '1000038001' \n" +
                    "AND interest_class = 'admission' \n" +
                    "and user_interest_id = ?;",status,interestId);

            OpenInterestRequest openInterestRequest = new OpenInterestRequest();
            openInterestRequest.setUserInterestId(interestId);
            openInterestRequest.setActiveDate(activeDate);
            HttpResult httpResult =  OpenInterestApi.openInterestRequest(openInterestRequest,token);
//            new HttpResultChecker(httpResult).success(message,false);

        }
    }
}