# -*- coding: utf-8 -*-
import requests
import pymongo
from pymongo.errors import PyMongoError
from config import *

MONGO_TABLE = 'jd_address'
client = pymongo.MongoClient(MONGO_URI)
db = client[MONGO_DB]
db.authenticate(USERNAME, PASSWORD)



params = { 
    'province' : '广东',
    'keyword' : '中山大学',
    'city' : '',
    'district' : '',
    'page' : '1',
    'appkey' : '77b95ab8d706104de2c63737eb243f12'
}


def get_all_address(url, totalpage):
    for index in range(1, totalpage):
        params['page'] = str(index+1)
        response = requests.post( url, params )
        data = response.json()
        poi = data["result"]["result"]
        for item in poi:
            yield item


def save_to_db(item):
    try:
        db[MONGO_TABLE].insert(item)
        print("存储到MongoDB成功")
    except PyMongoError:
        print(["存储到MongoDB失败;message:",PyMongoError])


def main():
    url = 'https://way.jd.com/Bigmap/University'
    response = requests.post( url, params )
    data = response.json()
    totalpage = data["result"]["totalPage"]
    for item in get_all_address(url, totalpage):
        save_to_db(item)


if __name__ == '__main__':
    main()