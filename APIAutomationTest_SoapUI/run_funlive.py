#!/usr/bin/env python3
# -*- coding:utf-8 -*-
"""
 @ author: wangwaiwai~
 @ date: 2019/02/23
"""

import os
import datetime
from utils.gen_report import *
from utils.send_mail import *
from utils.run_case import *
from utils.log import *

# 定义项目名称，需要和testcase文件夹下的目录名称一致
env = os.getenv("env")
project = os.getenv("Project")
sub_project = os.getenv("sub_project")

if env is None:
    env = "uat"
if sub_project:
    sub_project_list = sub_project.split(",")

project_list = []
file_dir = os.path.join("D:\\Test\\APIAutomationTest_SoapUI\\testcase\\{0}\\".format(env), project)

if sub_project:
    file_list = []
    for file in os.listdir(file_dir):
        if file.split(".")[-1] == "xml":
            file_list.append(file.split(".")[0])

    for sub_project_name in sub_project_list:
        if sub_project_name in file_list:
            sub_project_full_name = sub_project_name + '.xml'
            project_list.append(os.path.join(file_dir, sub_project_full_name))

else:
    for file in os.listdir(file_dir):
        if file.split(".")[-1] == "xml":
            project_list.append(os.path.join(file_dir, file))
try:
    # 执行SoapUI脚本，返回报告列表(项目下可能有多个脚本)
    report_path_lsit = RunCase(project_list=project_list).run_case()

    # 根据SoapUI报告生成邮件中概要信息
    report = GenReport(project=project, path_list=report_path_lsit).gen_report()

    # 发送邮件
    now_date = datetime.datetime.now().strftime('%Y%m%d')
    title = "{0}({1})接口测试报告".format(project, now_date)
    email = Email(title=title, file_name=report)
    email.send()
except Exception as e:
    logger.error(e)
