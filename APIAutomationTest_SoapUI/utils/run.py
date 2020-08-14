#!/usr/bin/env python3
# -*- coding:utf-8 -*-



import os

os.chdir('D:\\Program Files')
cmd = ".\SmartBear\SoapUI-Pro-5.1.2\\bin\\testrunner.bat -D 'file.encoding=UTF-8' -a -j -I -f D:\Test\APIAutomationTest_SoapUI\\report/POS_20200813162589 -F HTML -I D:\Test\APIAutomationTest_SoapUI\\testcase\\test\POS\POS.xml"
os.system(cmd)