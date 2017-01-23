import pymysql.cursors

# Connect to the database

global connection
connection = pymysql.connect(host='localhost',
                                 user='root',
                                 password='804956748',
                                 db='test',
                                 charset='utf8',
                                 cursorclass=pymysql.cursors.DictCursor,
autocommit=True)

def login(name,password):
    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT * FROM `user` WHERE `name`=%s AND password=%s"
        cursor.execute(sql, (name,password))
        result = cursor.fetchall()
        if len(result) == 0:
            return False,
        return True,result
def register(name,password):
    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT * FROM `user` WHERE `name`=%s"
        cursor.execute(sql, (name))
        result = cursor.fetchall()
    if len(result) != 0:
        return False,"该用户名已经存在"
    with connection.cursor() as cursor:
        # Create a new record
        sql = "INSERT INTO `user` (`name`, `password`) VALUES (%s, %s)"
        cursor.execute(sql, (name, password))

    # connection is not autocommit by default. So you must commit to save
    # your changes.
    connection.commit()
    return True,
def getFriendsList():
    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT * FROM `user`"
        cursor.execute(sql, ())
        return cursor.fetchall()

def getChatLog(name):
    with connection.cursor() as cursor:
        # Read a single record
        sql = "SELECT * FROM `message` WHERE `to`=%s or `to`='' ORDER BY `time`"
        cursor.execute(sql, (name))
        return cursor.fetchall()

def sendMessage(name,message,to):
    with connection.cursor() as cursor:
        # Create a new record
        sql = "INSERT INTO `message` (`name`, `message`,`to`,`time`) VALUES (%s, %s,%s,now())"
        cursor.execute(sql, (name,message,to))
    connection.commit()