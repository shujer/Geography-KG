#coding=utf-8
  
import xlrd
import sys
import json
import pymongo
from pymongo import MongoClient
import demjson
  
#连接数据库
client=MongoClient("ds223542.mlab.com", 23542)
db=client.sysu
col=db.specialty
db.authenticate("sysu","sysu2018")

data=xlrd.open_workbook("E:/资料/大三下/实训/ABOX_CAMPUS/old/specialty.xls")
table=data.sheets()[0]
#读取excel第一行数据作为存入mongodb的字段名
rowstag=table.row_values(0)
nrows=table.nrows
#ncols=table.ncols
#print rows
returnData={}
for i in range(1,nrows):
  #将字段名和excel数据存储为字典形式，并转换为json格式
  # returnData[i]=json.dumps(dict(zip(rowstag,table.row_values(i)))
  returnData[i]=json.dumps(dict(zip(rowstag,[table.row_values(i)[0],demjson.decode(table.row_values(i)[1])])))
  #通过编解码还原数据
  returnData[i]=json.loads(returnData[i])
  # print()
  #print returnData[i]
  col.insert(returnData[i])