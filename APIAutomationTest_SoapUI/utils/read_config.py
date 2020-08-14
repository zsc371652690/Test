#!/usr/bin/env python3
# -*- coding:utf-8 -*-
"""
 * author: wangwaiwai~
 * date: 2019/02/23
"""

import os
import configparser

base_dir = os.path.abspath(os.path.dirname(os.path.dirname(__file__)))
config_file = os.path.join(base_dir, 'config/config.ini')


class ReadConfig(object):

    """从config.ini文件读取配置"""

    def __init__(self):
        self.config = configparser.ConfigParser()
        self.config.read(config_file, encoding="utf-8-sig")

    def get_email(self, name):
        value = self.config.get('MAIL', name)
        return value


