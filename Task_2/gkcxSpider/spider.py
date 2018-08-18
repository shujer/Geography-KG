import requests
from config import *
from user_agent import *
from requests.exceptions import RequestException
import pymongo
from lxml import etree
import random
import time
from pymongo.errors import PyMongoError

MONGO_TABLE = 'school'
client = pymongo.MongoClient(MONGO_URI)
db = client[MONGO_DB]
db.authenticate(USERNAME, PASSWORD)

headers = {
'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36',
}


def get_schoolid(url):
    for i in range(1, 6):
        print("第%s页=====================" % str(i))
        headers['User-Agent'] = random.choice(USER_AGENT)
        try:
            response = requests.get(url=url,
                                headers=headers,
                                params={
                                    "messtype": "json",
                                    "province": PROVINCE,
                                    "page": str(i),
                                    "size": "50",
                                    "callback": "jQuery183021895111913533682_1534496621961",
                                    "_": "1534496622226"})
            if response.status_code == 200:
                data = response.json()
                print("每一页信息条数——>", len(data['school']))
                print("全部信息条数——>", data["totalRecord"]['num'])
                for data in data['school']:
                    yield {
                        "schoolname" : data['schoolname'],
                        "schoolid": data['schoolid'],
                        "website": data['guanwang'],
                        "schooltype": data['schooltype'],
                        "membership": data['membership'],
                        "schoolproperty": data['schoolproperty']
                    }
        except RequestException:
            print('请求索引页出错')


def get_specialtylist(schoolid):
    url = "https://gkcx.eol.cn/schoolhtm/specialty/specialtyList/specialty" + str(schoolid) + ".htm"
    try:
        headers['User-Agent'] = random.choice(USER_AGENT)
        response = requests.get(url=url, headers=headers, timeout=10)
        if response.status_code == 200:
            selector = etree.HTML(response.content)
            list = selector.xpath('//div[@class="left w-670"]/div[@class="content news"]/ul/li/a/text()')
            print("获取专业列表")
            return {
                "specialtylist": list
            }
    except RequestException:
        print('请求索引页出错')
        return None


def get_baseinfo(schoolid):
    url = "https://gkcx.eol.cn/schoolhtm/schoolInfo/"+str(schoolid)+"/10056/detail.htm"
    try:
        headers['User-Agent'] = random.choice(USER_AGENT)
        response = requests.get(url=url, headers=headers, timeout=10)
        if response.status_code == 200:
            selector = etree.HTML(response.content)
            list1 = selector.xpath('//div[@class="li-collegeHome"]/div[@class="li-collegeUl"]/ul[@class="li-collegeInfo"]/li//span/text()')
            location = list1[1]
            tel = list1[3]
            list2 = selector.xpath('//div[@class="li-collegeHome"]/div[@class="li-collegeUl"]/ul[@class="li-collegeInfo li-ellipsis"]/li//span/text()')
            email = list2[0]
            address = list2[1]
            data = {
                "location": location,
                "tel": tel,
                "email": email,
                "address":address
            }
            print("获取所在地、电话、邮件、地址")
            return data
    except RequestException:
        print('请求索引页出错')
        return None


def save_to_db(schoolid, refer, baseinfo, specialtylist):
    try:
        if db[MONGO_TABLE].find_one({'schoolid': schoolid}):
            db[MONGO_TABLE].update({'schoolid': schoolid}, {'$set': refer})
        else:
            db[MONGO_TABLE].insert(refer)
        db[MONGO_TABLE].update({'schoolid': schoolid}, {'$set': {
            "location": baseinfo['location'],
            "email": baseinfo["email"],
            "address": baseinfo['address'],
            "tel": baseinfo['tel'],
            "specialtylist": specialtylist['specialtylist']
        }})
        print("存储到MongoDB成功")
    except PyMongoError:
        print(["存储到MongoDB失败;message:",PyMongoError])


def main():
    url = "http://data.api.gkcx.eol.cn/soudaxue/queryschool.html"
    for refer in get_schoolid(url):
        schoolid = refer['schoolid']
        time.sleep(random.random() * 3)
        specialtylist = get_specialtylist(schoolid)
        baseinfo = get_baseinfo(schoolid)
        save_to_db(schoolid, refer, baseinfo, specialtylist)


if __name__ == '__main__':
    main()