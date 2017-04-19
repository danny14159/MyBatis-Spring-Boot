1.在db.py中配置数据库连接，运行test.sql初始化数据库
2.运行TCPServer.py启动服务端
3.运行chat.py启动聊天客户端，可以运行多个，注意配置服务端ip

数据库：MySQL
UI库：python tkinter
数据传输格式：json
数据通信：tcp socket
db.py作用：执行数据库连接，数据库相关操作
- login 登录
- register 注册
- getFriendsList 获取好友列表
- getChatLog 获取聊天记录
- sendMessage 发送消息，将消息存入数据库

ChatClient.py:socket通信的客户端，用于监听socket端口，设置回调函数。客户端监听到有消息，就在消息列表上增加一条记录。
ChatServer.py:socket通信的服务端，收到客户端的消息之后会将消息发给所有客户端。客户端判断消息是不是给自己的，不是就丢掉。
ScrolledList.py:UI组件，用于制作好友列表，带滚动功能
ScrolledText.py:UI组件，用于显示聊天记录
chat.py:核心布局和功能构件，包括登录注册的UI，数据库连接和socket通信的整合
