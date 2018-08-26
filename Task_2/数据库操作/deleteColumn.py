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

db["campus"].update({},{'$unset':{'campusID':""}},multi = True)