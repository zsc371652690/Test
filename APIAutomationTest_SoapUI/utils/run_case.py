#!/usr/bin/env python3
# -*- coding:utf-8 -*-
"""
 * author: wangwaiwai~
 * date: 2019/02/23
"""

import os
import datetime
from utils.log import logger


class RunCase(object):

    """执行SoapUI脚本，返回报告文件目录列表"""

    def __init__(self, project_list):
        self.project_list = project_list
        self.base_dir = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))
        self.now_time = datetime.datetime.now().strftime('%Y%m%d%H%M%S')
        self.file_path_lsit = []

    def run_case(self):
        for file in self.project_list:
            report_dir_name = file.split("\\")[-1].split(".")[0] + '_' + self.now_time
            report_path = os.path.join(self.base_dir, 'report/{0}'.format(report_dir_name))
            bat_path = ".\\SmartBear\\SoapUI-Pro-5.1.2\\bin"  # SoapUI中testrunner.bat脚本位置
            cmd = """{0}\\testrunner.bat -D 'file.encoding=UTF-8' -a -j -I -f {1} -F HTML -I {2}""".format(
                bat_path, report_path, file)   # cmd中执行的命令

            try:
                os.chdir('D:\\Program Files')  #SOAPUI根目录
                logger.info("******开始执行{0}******".format(file))
                os.system(cmd)  # 执行命令，生成报告
                self.file_path_lsit.append(report_path)
                logger.info("******执行{0}成功******".format(file))
            except Exception as e:
                logger.error(e)
        return self.file_path_lsit
