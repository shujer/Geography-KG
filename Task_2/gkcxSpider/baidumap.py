import requests
from requests.exceptions import RequestException
import pymongo
from user_agent import *
import random
import demjson
from pymongo.errors import PyMongoError

MONGO_TABLE = 'address'
MONGO_URI = 'ds223542.mlab.com:23542'
MONGO_DB = 'sysu'
USERNAME = "sysu"
PASSWORD = "sysu2018"

client = pymongo.MongoClient(MONGO_URI)
db = client[MONGO_DB]
db.authenticate(USERNAME, PASSWORD)

headers = {
'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36',
}


def get_page(url, keyword, index):
    headers['User-Agent'] = random.choice(USER_AGENT)
    for page in range(0, 100):
        params={
            "qt": "s",
            "wd": keyword,
            "rn": "50",
            "ie":"utf-8",
            "pn" : str(page),
            "fromproduct":"jsapi",
            "res":"api",
            "ak": "E4805d16520de693a3fe707cdc962045"}
        try:
            response = requests.get(url=url,headers=headers,params=params)
            if response.status_code == 200:
                html = response.content
                data = demjson.decode(html)
                total = data["result"]["total"]
                print(total)
                if(total <= 50*(page-1)):
                    break
                else:
                    contents = data["content"]
                    for content in contents:
                        ext = content["ext"];
                        if(isinstance(ext,dict)):
                            item = ext["detail_info"]
                            # print(item)
                            print([item["name"], item["poi_address"], content["std_tag"]])
                            yield {
                                "name": item["name"],
                                "address": item["poi_address"],
                                "tag": content["std_tag"],
                                "campusID": index
                            }
                        else:
                            pass
        except RequestException as e:
            print('请求索引页出错')
            


def save_to_db(item):
    try:
        if db[MONGO_TABLE].find_one({'name': item["name"]}):
            db[MONGO_TABLE].update({'name': item["name"]}, {'$set': item})
        else:
            db[MONGO_TABLE].insert(item)
            print("存储到MongoDB成功")
    except PyMongoError:
        print(["存储到MongoDB失败;message:",PyMongoError])


def main():
    url = "http://api.map.baidu.com"
    # keywords = ['中山大学南校区'，'中山大学北校区'，'中山大学东校区'， '中山大学珠海校区'，'中山大学深圳校区']
    keyword = "中山大学深圳校区"
    index = 5
    for item in get_page(url,keyword, index):
        save_to_db(item)


if __name__ == '__main__':
    main()
