#!/usr/bin/env python3
# -*-coding:utf-8 -*-

# Author:wangwaiwai~
# Date:2019/06/11

'''根据项目和版本获取周报数据'''

import time
import pymysql
import os

priority_dict = {"1": "P0", "2": "P1", "3": "P2", "4": "P3"}
bug_score = {"1": 5, "2": 3, "3": 2, "4": 1}
stage_list = ["API测试", "功能测试", "UAT回归测试", "集成测试"]
invalid_reason = ["不是问题", "发布问题", "数据问题", "无法重现", "第三方问题", "设计如此", "配置问题", "重复问题", "需求问题"]


class GetProjectBugNum(object):
    def __init__(self, p_name, s_time, e_time):
        self.p_name = p_name
        self.s_time = s_time
        self.e_time = e_time
        self.conn = pymysql.connect("rm-uf6mxk6ei2tdz62ahto.mysql.rds.aliyuncs.com", "jira_r", "M4JzaJqBtkvKMY9", "jira")
        self.cursor = self.conn.cursor()

    # 根据项目和日期获取项目总Bug
    def get_total_bug(self):
        sql = """SELECT aa.priority,COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,project c WHERE a.project=c.id 
            AND c.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.created BETWEEN 
            '{s_time}' AND '{e_time}') AS aa GROUP BY aa.priority""".format(p_name=self.p_name, s_time=self.s_time, e_time=self.e_time)
        self.cursor.execute(sql)
        data = self.cursor.fetchall()
        total = 0
        des = "共发现Bug{0}个，其中"
        for i in range(len(data)):
            total = total + data[i][1]
            des = des + "{0} Bug {1}个，".format(priority_dict[data[i][0]], data[i][1])
        return des.format(total)

    # 获取固定用户提的Bug
    def get_total_bug_by_user(self, user):
        sql = """SELECT COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,project c WHERE a.project=c.id 
            AND c.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.created BETWEEN 
            '{s_time}' AND '{e_time}' AND creator in {user}) AS aa """.format(p_name=self.p_name, s_time=self.s_time, e_time=self.e_time, user=user)
        self.cursor.execute(sql)
        data = self.cursor.fetchone()
        return data

    # 根据发现阶段统计bug
    def get_stage_bug(self, stage):
        sql = """SELECT aa.priority,COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,customfieldvalue c,customfieldoption d,project e 
            WHERE a.project=e.id AND e.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.id = c.issue AND c.CUSTOMFIELD = "10118" 
            AND c.stringvalue = d.id  AND d.customvalue = '{stage}' AND a.created BETWEEN '{s_time}' AND '{e_time}') AS aa 
            GROUP BY aa.priority""".format(p_name=self.p_name, s_time=self.s_time,e_time=self.e_time, stage=stage)
        self.cursor.execute(sql)
        data = self.cursor.fetchall()
        total = 0
        des = "    {0}共发现Bug{1}个，其中"
        if len(data) != 0:
            for i in range(len(data)):
                total = total + data[i][1]
                des = des + "{0} Bug {1}个，".format(priority_dict[data[i][0]], data[i][1])
            return des.format(stage, total)

    # 分类统计无效Bug
    def get_invalid_bug(self, reason):
        sql = """SELECT aa.priority,COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,customfieldvalue c,customfieldoption d,project e 
            WHERE a.project=e.id AND e.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.id = c.issue AND c.CUSTOMFIELD = "10103" 
            AND c.stringvalue = d.id  AND d.customvalue = '{reason}' AND a.created BETWEEN '{s_time}' AND '{e_time}') AS aa 
            GROUP BY aa.priority""".format(p_name=self.p_name, s_time=self.s_time,e_time=self.e_time, reason=reason)
        self.cursor.execute(sql)
        data = self.cursor.fetchall()
        total = 0
        des = "    {0}：Bug{1}个,其中"
        if len(data) != 0:
            for i in range(len(data)):
                total = total + data[i][1]
                des = des + "{0} Bug {1}个，".format(priority_dict[data[i][0]], data[i][1])
            return des.format(reason, total)

    # 统计所有无效bug数量
    def get_invalid_bug_total(self):
        sql = """SELECT COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,customfieldvalue c,customfieldoption d,project e 
            WHERE a.project=e.id AND e.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.id = c.issue AND c.CUSTOMFIELD = "10103" 
            AND c.stringvalue = d.id  AND d.customvalue in ("不是问题","发布问题","数据问题","无法重现",
           "第三方问题","设计如此","配置问题","重复问题","需求问题") AND a.created BETWEEN '{s_time}' AND '{e_time}')
            as aa """.format(p_name=self.p_name, s_time=self.s_time,e_time=self.e_time)
        self.cursor.execute(sql)
        data = self.cursor.fetchone()
        return "无效Bug共{0}个".format(data[0])    

    # 统计有效bug分值
    def get_effective_bug(self):
        sql = """SELECT aa.priority,COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,customfieldvalue c,customfieldoption d,project e 
            WHERE a.project=e.id AND e.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.id = c.issue AND c.CUSTOMFIELD = "10103" 
            AND c.stringvalue = d.id  AND d.customvalue in ("代码逻辑","已解决","暂不解决") AND a.created BETWEEN '{s_time}' AND '{e_time}')
            as aa GROUP BY aa.priority""".format(p_name=self.p_name, s_time=self.s_time,e_time=self.e_time)
        self.cursor.execute(sql)
        data = self.cursor.fetchall()
        des = "项目得分："
        total = 0
        for i in range(len(data)):
            des = des + "{0}*{1}+".format(data[i][1], bug_score[data[i][0]])
            total = total + int(data[i][1])*int(bug_score[data[i][0]])
        return [des[0:-1], total]

    # 未修复Bug数统计
    def get_unrepair_bug(self):
        sql = """SELECT aa.priority,COUNT(*) FROM (SELECT DISTINCT a.* FROM jiraissue a,
            project b,project c WHERE a.project=c.id 
            AND c.pname='{p_name}' AND a.ISSUETYPE='10105' AND a.created BETWEEN 
            '{s_time}' AND '{e_time}' and a.issuestatus != '6') AS aa GROUP BY aa.priority""".format(p_name=self.p_name, s_time=self.s_time, e_time=self.e_time)
        self.cursor.execute(sql)
        data = self.cursor.fetchall()
        total = 0
        des = "未修复Bug{0}个,其中"
        for i in range(len(data)):
            total = total + data[i][1]
            des = des + "{0} Bug {1}个,".format(priority_dict[data[i][0]], data[i][1])
        return des.format(total)

    # Reopen Bug数量
    def get_reopen_bug(self):
        sql = """SELECT COUNT(issueid) FROM changegroup WHERE id IN (SELECT 
        groupid FROM changeitem WHERE newstring='reopened') AND issueid IN 
        (SELECT DISTINCT a.id FROM jiraissue a,project b,project c WHERE 
        a.project=c.id AND c.pname='{p_name}' AND a.ISSUETYPE='10105' AND 
        a.created BETWEEN  '{s_time}' AND '{e_time}')""".format(p_name=self.p_name, s_time=self.s_time, e_time=self.e_time)
        self.cursor.execute(sql)
        data = self.cursor.fetchone()
        return data[0]
    
    # 关闭数据库链接
    def close_conn(self):
        self.conn.close()


if __name__ == "__main__":
    p_name = os.getenv("p_name")
    s_time = os.getenv("s_time") + " 00:00:00"
    e_time = os.getenv("e_time") + " 23:59:59"
    ui_user = "('caoyn','zhouf')"
    project = GetProjectBugNum(p_name, s_time, e_time)

    # 项目BUG情况
    print(project.get_total_bug())
    # 项目BUG分类统计
    for i in range(len(stage_list)):
        if project.get_stage_bug(stage_list[i]):
            print(project.get_stage_bug(stage_list[i]))
    # 无效BUG总数
    print(project.get_invalid_bug_total())
    # 无效BUG分类统计
    for i in range(len(invalid_reason)):
        if project.get_invalid_bug(invalid_reason[i]):
            print(project.get_invalid_bug(invalid_reason[i]))
    # Reopen Bug数量
    reopen_num = project.get_reopen_bug()
    print("Reopen Bug数 {0}个；".format(reopen_num))
    # 项目得分
    print(project.get_effective_bug())
    # UI提出的问题
    ui_bug_num = project.get_total_bug_by_user(ui_user)[0]
    print("UI或UED Bug数：{0}个".format(ui_bug_num))
    project.close_conn()
