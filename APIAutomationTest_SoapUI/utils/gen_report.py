#!/usr/bin/env python3
# -*- coding:utf-8 -*-
"""
 * author: wangwaiwai~
 * date: 2019/02/23
"""

from lxml import html
import os
from utils.log import *
import datetime


class GenReport(object):

    """生成邮件中显示的html文件"""
    def __init__(self, project, path_list):
        self.path_list = path_list
        self.project = project
        self.now_time = datetime.datetime.now().strftime('%Y%m%d%H%M%S')

    def gen_report(self):
        base_dir = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))
        report = base_dir + "\\report\\{0}_{1}".format(self.project, self.now_time) + ".html"
        if os.path.exists(report):
            os.remove(report)
        # 读取overview-summary.html文件中的概要信息，写入html文件
        logger.info("******开始生成{0}邮件模版******".format(self.project))
        with open(report, 'w') as f:
            f.write("""
            <!DOCTYPE html> 
                <html>
                <head>
                <style>
                .div-td{}
                .div-td table th{padding:5px 15px 5px 15px;}  
                .div-td table td{padding:5px 15px 5px 15px;} 				
                </style>
                </head>
                <body>""")
            f.write("""
                <h2 style="margin-left:20px">下面为{0}接口测试报告：</h2>
                <div class= "div-td">
                <table border="1px solid" style="margin-left:80px;margin-top:10px;text-align:center;border-collapse: collapse;" >
                <tr valign="top">
                <th>项目</th><th>测试用例数</th><th>失败</th><th>错误</th><th>通过率</th>
                <th>时长(s)</th><th>报告</th></tr>
                """.format(self.project))
            for file_dir in self.path_list:
                try:
                    report_name = file_dir.split('/')[-1]
                    ftp_path = os.path.join("ftp://10.1.15.61\\APIAutomationTest_SoapUI\\report", report_name)
                    file = os.path.join(file_dir, "overview-summary.html")
                    with open(file, 'r') as f1:
                        data = f1.read()
                    content = html.etree.HTML(data)
                    project_name = file_dir.split('/')[-1].split('_')[0]  # 项目名称
                    all_case_num = content.xpath('//a[@title="Display all tests"]')[0].text  # case数量
                    fail_case_num = content.xpath('//a[@title="Display all failures"]')[0].text  # 失败case数量
                    error_case_num = content.xpath('//a[@title="Display all errors"]')[0].text  # 错误case数量
                    success_rate = content.xpath('//td')[3].text  # 成功case占比
                    time = content.xpath('//td')[4].text  # 执行总费时
                    detail_url = os.path.join(ftp_path, "index.html")  # 完整报告链接(ftp路径)
                    # detail_url = os.path.join(file_dir, "index.html")  # 完整报告链接(本地路径)
                    f.write("""
                    <tr valign="top">
                    <td>{0}</td><td>{1}</td><td style="color:red">{2}</td><td>{3}</td><td>{4}</td><td>{5}</td><td>
                    <a href='{6}'>查看报告</a></td></tr>
                    """.format(project_name, all_case_num, fail_case_num, error_case_num, success_rate, time, detail_url))
                except Exception as e:
                    logger.error(e)
            f.write("""</table>
                    </div>
                    </body>
                    </html>""")
            logger.info("******生成{0}邮件模版成功******".format(self.project))
        return report
