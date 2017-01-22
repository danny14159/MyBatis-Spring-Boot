from socket import * # get socket constructor and constants
import json
myHost = '' # '' = all available interfaces on host
myPort = 50007 # listen on a non-reserved port number
sockobj = socket(AF_INET, SOCK_STREAM) # make a TCP socket object
sockobj.bind((myHost, myPort)) # bind it to server port number
sockobj.listen(50) # listen, allow 5 pending connects
while True: # listen until process killed
    connection, address = sockobj.accept() # wait for next client connect
    while True:
        data = connection.recv(10240) # read next line on client socket
        if not data: break # send a reply line to the client
        print (data.decode())
        connection.send(data) # until eof when socket closed
    connection.close()