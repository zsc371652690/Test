#!/usr/bin/env python3
# -*- coding:utf-8 -*-
"""
 @ author: wangwaiwai~
 @ date: 2019/02/23
"""

import smtplib
from email.mime.text import MIMEText
from email.mime.multipart import MIMEMultipart
from utils.read_config import *
from utils.log import *


# 获取邮箱账号密码、发件人、收件人等信息
from utils.read_config import ReadConfig

config = ReadConfig()
server = config.get_email('server')
port = config.get_email('port')
user = config.get_email('user')
passwd = config.get_email('passwd')
to_list = config.get_email('toList')
sender = config.get_email('sender')


class Email(object):

    """获取config.ini中的邮件配置，发送邮件"""

    def __init__(self, title, file_name=None):
        """
          * title: 邮件标题
          * file_name: 附件绝对路径
          * msg: 邮件正文内容
        """

        self.title = title
        self.msg = MIMEMultipart('mixed')
        self.file_name = file_name

    def send(self):
        self.msg = MIMEText(open(self.file_name, 'rb').read(), 'html', 'gbk')
        self.msg['Subject'] = self.title
        self.msg['From'] = sender
        self.msg['To'] = to_list
        logger.info("******开始发送邮件******")
        # smtp = smtplib.SMTP_SSL(server, port)
        smtp = smtplib.SMTP(server, port)
        smtp.login(user, passwd)
        smtp.sendmail(sender, to_list.split(','), self.msg.as_string())
        smtp.quit()
        logger.info("******发送邮件成功******")
