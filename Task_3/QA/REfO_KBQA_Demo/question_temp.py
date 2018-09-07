# -*- coding: utf-8 -*-  

"""

@author: SimmerChan

@contact: hsl7698590@gmail.com

@file: question_temp.py

@time: 2017/12/20 15:30

@desc:
设置问题模板，为每个模板设置对应的SPARQL语句。demo提供如下模板：


1. 某校区包含哪些校园（多少个）
2. 某校区/校园包含哪些学院（多少个）
3. 某校区/校园/学院包含哪些专业（多少个）
4. 某专业属于哪些（个）学院
5. 某专业/学院/栋楼属于哪些（个）校园
6. 某专业/学院/校园/栋楼属于哪些（个）校区
7. 某校园的通讯地址/面积
8. 某学院的通讯地址
9. 某栋楼的地址/tag
10. 某校园/校区包含哪些楼

读者可以自己定义其他的匹配规则。
"""
from refo import finditer, Predicate, Star, Any, Disjunction
import re

# TODO SPARQL前缀和模板
SPARQL_PREXIX = u"""
PREFIX : <http://www.sysu.com/>
PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
"""

SPARQL_SELECT_TEM = u"{prefix}\n" + \
             u"SELECT DISTINCT {select} FROM <http://www.Graph.com/sysuData> WHERE {{\n" + \
             u"{expression}\n" + \
             u"}}\n"

SPARQL_COUNT_TEM = u"{prefix}\n" + \
             u"SELECT COUNT({select}) FROM <http://www.Graph.com/sysuData> WHERE {{\n" + \
             u"{expression}\n" + \
             u"}}\n"

SPARQL_ASK_TEM = u"{prefix}\n" + \
             u"ASK FROM <http://www.Graph.com/sysuData> {{\n" + \
             u"{expression}\n" + \
             u"}}\n"


class W(Predicate):
    def __init__(self, token=".*", pos=".*"):
        self.token = re.compile(token + "$")
        self.pos = re.compile(pos + "$")
        super(W, self).__init__(self.match)

    def match(self, word):
        m1 = self.token.match(word.token)
        m2 = self.pos.match(word.pos)
        return m1 and m2


class Rule(object):
    def __init__(self, condition_num, condition=None, action=None):
        assert condition and action
        self.condition = condition
        self.action = action
        self.condition_num = condition_num

    def apply(self, sentence):
        matches = []
        for m in finditer(self.condition, sentence):
            i, j = m.span()
            matches.extend(sentence[i:j])

        return self.action(matches), self.condition_num


class KeywordRule(object):
    def __init__(self, condition=None, action=None):
        assert condition and action
        self.condition = condition
        self.action = action

    def apply(self, sentence):
        matches = []
        for m in finditer(self.condition, sentence):
            i, j = m.span()
            matches.extend(sentence[i:j])
        if len(matches) == 0:
            return None
        else:
            return self.action()


class QuestionSet:
    def __init__(self):
        pass

    @staticmethod
    def contain_campus_question(word_objects):
        """
        校区包含哪些校园
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :campus." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def contain_num_campus_question(word_objects):
        """
        校区包含多少个校园
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :campus." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_COUNT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def contain_faculty_question(word_objects):
        """
        校区/校园包含哪些学院
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :faculty." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def contain_num_faculty_question(word_objects):
        """
        校区/校园包含多少个学院
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :faculty." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_COUNT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def contain_specialty_question(word_objects):
        """
        校区/校园/学院包含哪些专业
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :specialty." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def contain_num_specialty_question(word_objects):
        """
        校区/校园/学院包含多少个专业
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :specialty." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_COUNT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def belongTo_faculty_question(word_objects):
        """
        某专业属于哪些（个）学院
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :belongTo ?m." \
                    u"?m rdf:type :faculty." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def belongTo_campus_question(word_objects):
        """
        某专业/学院属于哪些（个）校园
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :belongTo ?m." \
                    u"?m rdf:type :campus." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def belongTo_zone_question(word_objects):
        """
        某专业/学院/校园属于哪些（个）校区
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name \"{entry}\"." \
                    u"?s :belongTo ?m." \
                    u"?m rdf:type :zone." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql

    @staticmethod
    def basic_info_question(word_objects):
        """
        校园的信息：地址/面积
        学院的信息：地址
        楼的信息：地址/tag
        """

        keyword = None
        for r in basic_keyword_rules:
            keyword = r.apply(word_objects)
            if keyword is not None:
                break

        select = u"?x"
        sparql = None
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name '{entry}'." \
                    u"?s {keyword} ?x.".format(entry=w.token.decode('utf-8'), keyword=keyword)

                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX, select=select, expression=e)

                break

        return sparql

    @staticmethod
    def contain_building_question(word_objects):
        """
        校区/校园包含哪些楼
        """
        select = u"?x"
        sparql = None
        
        for w in word_objects:
            if w.pos == pos_entry:
                e = u"?s :name '{entry}'." \
                    u"?s :contain ?m." \
                    u"?m rdf:type :building." \
                    u"?m :name ?x".format(entry=w.token.decode('utf-8'))
                sparql = SPARQL_SELECT_TEM.format(prefix=SPARQL_PREXIX,
                                                  select=select,
                                                  expression=e)
                break
        return sparql



class PropertyValueSet:
    def __init__(self):
        pass

    @staticmethod
    def return_address_value():
        return u':address'

    @staticmethod
    def return_name_value():
        return u':name'

    @staticmethod
    def return_area_value():
        return u':area'

    @staticmethod
    def return_tag_value():
        return u':tag'


# TODO 定义关键词
pos_entry = "nz"

pos_number = "m"

my_entity = (W(pos=pos_entry))
number_entity = (W(pos=pos_number))

zone = W("校区")
campus = W("校园")
faculty = (W("学院") | W("院系") | W("院"))
specialty = (W("专业") | W("系"))
building = (W("楼") | W("建筑"))

several = (W("多少") | W("多少个") | W("几个"))

which = (W("哪些") | W("哪个") | W("什么"))

belongTo = (W("属于") | W("位于") | W("处于") | W("是") + W("的") | W("在"))

contain = (W("包含") | W("包括") | W("涵盖") | W("拥有") | W("有"))

address = (W("哪里") | W("哪儿") | W("何地") | W("何处") | W("在") + W("哪") | W("地址") | W("位置") | W("通讯地址"))

area = (W("占地") | W("多大") | W("面积") | W("占地面积") | W("多")+W("大"))

tag = (W("标签") | W("哪种") | W("类型"))

basic_pro = (address | area | tag)
# TODO 问题模板/匹配规则
"""
1. 某校区包含哪些校园（多少个）
2. 某校区/校园包含哪些学院（多少个）
3. 某校区/校园/学院包含哪些专业（多少个）
4. 某专业属于哪些（个）学院
5. 某专业/学院/栋楼属于哪些（个）校园
6. 某专业/学院/校园/栋楼属于哪些（个）校区
7. 某校园的通讯地址/面积
8. 某学院的通讯地址 || 某栋楼的地址/tag || 某校园/校区包含哪些楼
"""
rules = [

    # 属于
    Rule(condition_num=3,condition=my_entity + Star(Any(), greedy=False) + ((belongTo + Star(Any(), greedy=False) + zone) | (W("是") + Star(Any(), greedy=False) + zone + Star(Any(), greedy=False) + W("的"))) + Star(Any(), greedy=False), action=QuestionSet.belongTo_zone_question),
    Rule(condition_num=3,condition=my_entity + Star(Any(), greedy=False) + ((belongTo + Star(Any(), greedy=False) + campus) | (W("是") + Star(Any(), greedy=False) + campus + Star(Any(), greedy=False) + W("的"))) + Star(Any(), greedy=False), action=QuestionSet.belongTo_campus_question),
    Rule(condition_num=3,condition=my_entity + Star(Any(), greedy=False) + ((belongTo + Star(Any(), greedy=False) + faculty) | (W("是") + Star(Any(), greedy=False) + faculty + Star(Any(), greedy=False) + W("的"))) + Star(Any(), greedy=False), action=QuestionSet.belongTo_faculty_question),

    # 包含
    Rule(condition_num=4,condition=my_entity + Star(Any(), greedy=False) + contain + Star(Disjunction(Any(), several), greedy=False) + campus + Star(Any(), greedy=False), action=QuestionSet.contain_campus_question),
    Rule(condition_num=4,condition=my_entity + Star(Any(), greedy=False) + contain + Star(Disjunction(Any(), several), greedy=False) + faculty + Star(Any(), greedy=False), action=QuestionSet.contain_faculty_question),
    Rule(condition_num=4,condition=my_entity + Star(Any(), greedy=False) + contain + Star(Disjunction(Any(), several), greedy=False) + specialty + Star(Any(), greedy=False), action=QuestionSet.contain_specialty_question),
    Rule(condition_num=4,condition=my_entity + Star(Any(), greedy=False) + contain + Star(Disjunction(Any(), several), greedy=False) + building + Star(Any(), greedy=False), action=QuestionSet.contain_building_question),
    # 多少
    Rule(condition_num=5, condition=my_entity + Star(Any(), greedy=False) + several + Star(Any(), greedy=False) + campus + Star(Any(), greedy=False), action=QuestionSet.contain_num_campus_question),
    Rule(condition_num=5, condition=my_entity + Star(Any(), greedy=False) + several + Star(Any(), greedy=False) + faculty + Star(Any(), greedy=False), action=QuestionSet.contain_num_faculty_question),
    Rule(condition_num=5, condition=my_entity + Star(Any(), greedy=False) + several + Star(Any(), greedy=False) + specialty + Star(Any(), greedy=False), action=QuestionSet.contain_num_specialty_question),
    # 基础信息
    Rule(condition_num=2,condition=my_entity + Star(Any(), greedy=False) + basic_pro + Star(Any(), greedy=False), action=QuestionSet.basic_info_question),
]

# TODO 具体的属性词匹配规则
basic_keyword_rules = [
    KeywordRule(condition=my_entity + Star(Any(), greedy=False) + address + Star(Any(), greedy=False), action=PropertyValueSet.return_address_value),
    KeywordRule(condition=my_entity + Star(Any(), greedy=False) + area + Star(Any(), greedy=False), action=PropertyValueSet.return_area_value),
    KeywordRule(condition=my_entity + Star(Any(), greedy=False) + tag + Star(Any(), greedy=False), action=PropertyValueSet.return_tag_value),
]