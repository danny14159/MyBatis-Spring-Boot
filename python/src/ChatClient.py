# -*- coding: utf-8 -*-
"""
Created on Thu Oct 24 17:35:50 2013

"""

import socket
import threading
import json,datetime

inString = ''
outString = ''

def DealIn(s,callback):
    global inString
    while True:
        try:
            inString = s.recv(1024)
            print ('Receive',inString)
            obj = json.loads(inString.decode())
            obj['time'] = datetime.datetime.now()
            callback(obj)
        except Exception as e:
            print(e)
            break

def clientConnect(ip,callback):
    global sock
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.connect((ip, 8888))

    thin = threading.Thread(target = DealIn, args = (sock,callback))
    thin.start()