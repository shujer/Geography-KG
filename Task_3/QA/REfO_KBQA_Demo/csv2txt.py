# encoding=utf-8

"""

@author: SimmerChan

@contact: hsl7698590@gmail.com

@file: jena_sparql_endpoint.py

@time: 2017/12/20 17:42


cmd命令：
mongoexport --host ds223542.mlab.com:23542 -u sysu -p sysu2018 -d sysu -c zone -f name --csv -o G:\暑假-传统建筑\QA\KG-demo-for-movie\kg_demo_movie\KB_query\myData\zone.csv
mongoexport --host ds223542.mlab.com:23542 -u sysu -p sysu2018 -d sysu -c campus -f name --csv -o G:\暑假-传统建筑\QA\KG-demo-for-movie\kg_demo_movie\KB_query\myData\campus.csv
mongoexport --host ds223542.mlab.com:23542 -u sysu -p sysu2018 -d sysu -c faculty -f name --csv -o G:\暑假-传统建筑\QA\KG-demo-for-movie\kg_demo_movie\KB_query\myData\faculty.csv
mongoexport --host ds223542.mlab.com:23542 -u sysu -p sysu2018 -d sysu -c specialty -f name --csv -o G:\暑假-传统建筑\QA\KG-demo-for-movie\kg_demo_movie\KB_query\myData\specialty.csv
mongoexport --host ds223542.mlab.com:23542 -u sysu -p sysu2018 -d sysu -c building -f name --csv -o G:\暑假-传统建筑\QA\KG-demo-for-movie\kg_demo_movie\KB_query\myData\building.csv


@desc:
把从MongoDB导出的csv文件按照jieba外部词典的格式转为txt文件。
nz代表校园/校区/学院/楼的专名

"""
import pandas as pd
import os

def read_files(file_dir):
    for root, dirs, files in os.walk(file_dir):
        for file in files:
            if os.path.splitext(file)[1] == '.csv': # csv文件
                df = pd.read_csv(os.path.join(root, file))
                title = df['name'].values
                file_name = os.path.splitext(file)[0]
                print file_name
                with open(os.path.join(root, file_name) + '.txt', 'a') as f:
                    for t in title[0:]:
                        f.write(t + ' ' + 'nz' + '\n')

if __name__ == '__main__':
    myFile = './myData/'
    read_files(myFile)
