from tkinter import *
root = Tk()
root.title("登录")

fields = '用户名', '密码'
def fetch(entries):
    for entry in entries:
        print('Input => "%s"' % entry.get()) # get text

def makeform(root, fields):
    entries = []
    for field in fields:
        row = Frame(root) # make a new row
        lab = Label(row, width=5, text=field) # add label, entry
        ent = Entry(row)
        row.pack(side=TOP, fill=X) # pack row on top
        lab.pack(side=LEFT)
        ent.pack(side=RIGHT, expand=YES, fill=X) # grow horizontal
        entries.append(ent)
    return entries

ents = makeform(root, fields)
def sendMessage():
    print(msgbox.get())
    pass

def showChat():
    global msgbox
    fetch(ents)
    root.destroy()
    chat = Tk()
    chat.title("开始聊天")
    row = Frame(chat) #聊天输入框
    text = Frame(chat) #聊天记录显示框
    friends = Frame(chat) #右侧好友列表
    friends.config(width=300)
    friends.config(height=500)
    friends.config(bg='green')
    friends.pack(fill=Y,side=RIGHT)

    text.config(bg='black')
    text.config(height=450)
    text.pack(side=TOP,fill=X)
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
    print("xxx")
    root.mainloop()

def showRegister():
    root.destroy()
    reg = Tk()
    reg.title("注册")
    btn = Button(reg,text="登录",command=returnToLogin)
    btn.pack()


labelfont = ('times', 20, 'bold') # family, size, style
widget = Label(root, text='Welcome!')
widget.config(font=labelfont) # use a larger font
widget.config(height=5, width=20) # initial size: lines,chars
widget.pack(expand=YES, fill=BOTH)

button = Button(root,text="登录",command=showChat)
button.config(padx=100)
button.config(pady=5)
button.pack()

button2 = Button(root,text="注册",command=showRegister)
button2.config(padx=100)
button2.config(pady=5)
button2.pack()
root.mainloop()