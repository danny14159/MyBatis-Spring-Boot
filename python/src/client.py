import sys
import threading
from socket import * # portable socket interface plus constants
serverHost = 'localhost' # server name, or: 'starship.python.net'
serverPort = 50007 # non-reserved port used by the server
# requires bytes: b'' or str,encode()
def sendSocketMessage(message):
    global sockobj

    sockobj.send(message.encode()) # send line to server over socket
    #data = sockobj.recv(10240) # receive line from server: up to 1k
    #print('Client received:', data.decode()) # bytes are quoted, was `x`, repr(x)

#client端要开一个线程接受服务器的消息
def recv():
    global sockobj
    sockobj.connect((serverHost, serverPort)) # connect to server machine + port
    try:
        while True:
            data = sockobj.recv(10240)
            print('Client received:', data.decode())
    finally:
        sockobj.close()
def beginLoop(callable):
    sockobj = socket(AF_INET, SOCK_STREAM) # make a TCP/IP socket object
    t = threading.Thread(target=recv)
    t.setDaemon(True)
    t.start()