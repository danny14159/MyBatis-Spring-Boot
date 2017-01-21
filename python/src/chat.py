from tkinter import *
import ScrolledList
import ScrolledText
from tkinter.messagebox import *
import db
import json
import client

#showwarning('确认', '')
fields = '用户名', '密码'
reg_fields = '用户名',' 密码','确认密码'
def fetch(entries):
    return list(map(lambda x:x.get(),entries))


def makeform(root, fields):
    entries = []
    for field in fields:
        row = Frame(root) # make a new row
        lab = Label(row, width=10, text=field) # add label, entry
        ent = Entry(row)
        row.pack(side=TOP, fill=X) # pack row on top
        lab.pack(side=LEFT)
        ent.pack(side=RIGHT, expand=YES, fill=X) # grow horizontal
        entries.append(ent)
    return entries



def sendMessage():
    msg = msgbox.get()
    client.sendSocketMessage(json.dumps({'name':current_user,'message':msg,'to':to_user}))
    global write
    write.delete(0,END)

global to_user
to_user=''
def update_titie(frame):
    if to_user:
        msg = "，正在与"+to_user+"聊天"
    else:
        msg = "，加入当前群聊"
    frame.title("欢迎"+current_user+msg+" -- 双击好友昵称与好友聊天")

def showMessageLog():
    str = ''
    for item in db.getChatLog(current_user):
        str += item['time']+' '+item['name']+':'+item['message']
    stext.settext(text=str)
def changeCharUser(selected):
    global to_user
    global current_user
    if selected == current_user:
        showwarning('错误','您不能与自己聊天')
        return
    to_user = selected
    update_titie(chat)
def showChat():
    up = fetch(ents)
    loginResult = db.login(up[0],up[1])
    if not loginResult[0]:
        showwarning('警告', '用户名或者密码错误')
        return
    global current_user
    current_user = loginResult[1][0]['name']
    global msgbox
    root.destroy()
    global chat
    chat = Tk()
    update_titie(chat)
    row = Frame(chat) #聊天输入框
    text = Frame(chat) #聊天记录显示框
    friends = Frame(chat) #右侧好友列表
    friends.config(width=300)
    friends.config(height=500)
    friends.config(bg='white')
    friends.pack(fill=Y,side=RIGHT)
    flist = db.getFriendsList()

    ScrolledList.ScrolledList(parent=friends,options=list(map(lambda x:x['name'],flist)),fun=changeCharUser).pack(side=BOTTOM)
    Label(friends, text='好友列表').pack(side=TOP,fill=X)

    text.config(bg='black')
    text.config(height=450)
    text.pack(side=TOP,fill=X)
    global stext
    stext = ScrolledText.ScrolledText(parent=text,text='')
    showMessageLog()
    global write
    write = Entry(row)
    msgbox = write
    send = Button(row,text="发送",command=sendMessage)
    row.config(padx=10)
    row.config(pady=10)

    write.config(width=50)
    row.pack(side=BOTTOM,fill=X)
    write.pack(side=LEFT)
    send.config(padx=12)
    send.pack(side=RIGHT)
    chat.mainloop()
def returnToLogin():
    reg.destroy()
    login()

def submitRegister():
    res = fetch(reg_ents)
    res = db.register(res[0],res[1])
    if not res[0]:
        showwarning('警告', res[1])
        return
    reg.destroy()
    login()

def showRegister():
    root.destroy()
    global reg
    reg = Tk()
    reg.title("注册")
    btn = Button(reg,text="登录",command=returnToLogin)
    submit = Button(reg,text="提交",command=submitRegister)
    global reg_ents
    reg_ents = makeform(reg,reg_fields)
    btn.config(padx=50)
    btn.config(pady=5)
    submit.config(padx=100)
    submit.config(pady=5)
    btn.pack(side=RIGHT)
    submit.pack(side=LEFT)

def login():
    global root
    global ents
    root = Tk()
    ents = makeform(root, fields)
    root.title("登录")
    labelfont = ('times', 20, 'bold') # family, size, style
    widget = Label(root, text='Welcome!')
    widget.config(font=labelfont) # use a larger font
    widget.config(height=5, width=20) # initial size: lines,chars
    widget.pack(expand=YES, fill=BOTH)

    btn_login = Button(root,text="登录",command=showChat)
    btn_login.config(padx=100)
    btn_login.config(pady=5)
    btn_login.pack()

    btn_reg = Button(root,text="注册",command=showRegister)
    btn_reg.config(padx=100)
    btn_reg.config(pady=5)
    btn_reg.pack()
    root.mainloop()

login()