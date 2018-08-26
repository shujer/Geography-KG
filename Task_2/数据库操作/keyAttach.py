#encoding:utf-8
import pymongo
from bson import ObjectId

MONGO_TABLE = 'address'
MONGO_URI = 'ds223542.mlab.com:23542'
MONGO_DB = 'sysu'
USERNAME = "sysu"
PASSWORD = "sysu2018"

client = pymongo.MongoClient(MONGO_URI)
db = client[MONGO_DB]
db.authenticate(USERNAME, PASSWORD)

result=db["faculty"].find({},{"_id":1, "facultyID":1})
data = []
indexs = []
for item in result:
	print(item["_id"])
	data.append(item["_id"])
	indexs.append(item["facultyID"])

i = 0
for index in indexs:
	db.specialty.update({"facultyID": index},{"$set": {"facultyID.$": data[i]}}, multi=True)
	i = i + 1 


# db["address"].update({'campusID': 1}, {'$set': {'campusID': data[0]}}, multi=True)
# db["address"].update({'campusID': 2}, {'$set': {'campusID': data[1]}}, multi=True)
# db["address"].update({'campusID': 3}, {'$set': {'campusID': data[2]}}, multi=True)
# db["address"].update({'campusID': 4}, {'$set': {'campusID': data[3]}}, multi=True)
# db["address"].update({'campusID': 5}, {'$set': {'campusID': data[4]}}, multi=True)