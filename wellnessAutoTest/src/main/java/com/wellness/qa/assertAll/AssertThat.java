package com.wellness.qa.assertAll;

public class AssertThat {

    public static void main(String[] args) {

//        一般匹配符
//        assertThat( testedNumber, allOf( greaterThan(8), lessThan(16) ) );
//
//        注释： allOf匹配符表明如果接下来的所有条件必须都成立测试才通过，相当于“与”（&&）
//
//        2、assertThat( testedNumber, anyOf( greaterThan(16), lessThan(8) ) );
//
//        注释：anyOf匹配符表明如果接下来的所有条件只要有一个成立则测试通过，相当于“或”（||）
//        3、assertThat( testedNumber, anything() );
//
//        注释：anything匹配符表明无论什么条件，永远为true
//        4、assertThat( testedString, is( “developerWorks” ) );
//
//        注释： is匹配符表明如果前面待测的object等于后面给出的object，则测试通过
//        5、assertThat( testedString, not( “developerWorks” ) );
//
//        注释：not匹配符和is匹配符正好相反，表明如果前面待测的object不等于后面给出的object，则测试通过
//
//                字符串相关匹配符
//        1、assertThat( testedString, containsString( “developerWorks” ) );
//
//        注释：containsString匹配符表明如果测试的字符串testedString包含子字符串”developerWorks”则测试通过
//
//        2、assertThat( testedString, endsWith( “developerWorks” ) );
//
//        注释：endsWith匹配符表明如果测试的字符串testedString以子字符串”developerWorks”结尾则测试通过
//
//        3、assertThat( testedString, startsWith( “developerWorks” ) );
//
//        注释：startsWith匹配符表明如果测试的字符串testedString以子字符串”developerWorks”开始则测试通过
//
//        4、assertThat( testedValue, equalTo( expectedValue ) );
//
//        注释： equalTo匹配符表明如果测试的testedValue等于expectedValue则测试通过，equalTo可以测试数值之间，字
//        符串之间和对象之间是否相等，相当于Object的equals方法
//
//        5、assertThat( testedString, equalToIgnoringCase( “developerWorks” ) );
//
//        注释：equalToIgnoringCase匹配符表明如果测试的字符串testedString在忽略大小写的情况下等于”developerWorks”则测试通过
//
//        6、assertThat( testedString, equalToIgnoringWhiteSpace( “developerWorks” ) );
//
//        注释：equalToIgnoringWhiteSpace匹配符表明如果测试的字符串testedString在忽略头尾的任意个空格的情况下等
//
//        于”developerWorks”则测试通过，注意：字符串中的空格不能被忽略
//
//                数值相关匹配符
//        1、assertThat( testedDouble, closeTo( 20.0, 0.5 ) );
//
//        注释：closeTo匹配符表明如果所测试的浮点型数testedDouble在20.0±0.5范围之内则测试通过
//
//        2、assertThat( testedNumber, greaterThan(16.0) );
//
//        注释：greaterThan匹配符表明如果所测试的数值testedNumber大于16.0则测试通过
//
//        3、assertThat( testedNumber, lessThan (16.0) );
//
//        注释：lessThan匹配符表明如果所测试的数值testedNumber小于16.0则测试通过
//
//        4、assertThat( testedNumber, greaterThanOrEqualTo (16.0) );
//
//        注释： greaterThanOrEqualTo匹配符表明如果所测试的数值testedNumber大于等于16.0则测试通过
//
//        5、assertThat( testedNumber, lessThanOrEqualTo (16.0) );
//
//        注释：lessThanOrEqualTo匹配符表明如果所测试的数值testedNumber小于等于16.0则测试通过
//
//                collection相关匹配符
//        1、assertThat( mapObject, hasEntry( “key”, “value” ) );
//
//        注释：hasEntry匹配符表明如果测试的Map对象mapObject含有一个键值为”key”对应元素值为”value”的Entry项则测试通过
//
//        2、assertThat( iterableObject, hasItem ( “element” ) );
//
//        注释：hasItem匹配符表明如果测试的迭代对象iterableObject含有元素“element”项则测试通过
//
//        3、assertThat( mapObject, hasKey ( “key” ) );
//
//        注释： hasKey匹配符表明如果测试的Map对象mapObject含有键值“key”则测试通过
//
//        4、assertThat( mapObject, hasValue ( “key” ) );
//
//        注释：hasValue匹配符表明如果测试的Map对象mapObject含有元素值“val
    }
}
