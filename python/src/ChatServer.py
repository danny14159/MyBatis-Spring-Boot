import socket
import db
import json
import threading

con = threading.Condition()
HOST = '' # Symbolic name meaning all available interfaces
PORT = 8888 # Arbitrary non-privileged port

s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(10)
print ('Socket now listening')

#Function for handling connections. This will be used to create threads
def clientThreadIn(conn):
    global data
    #infinite loop so that function do not terminate and thread do not end.
    while True:
        #Receiving from client
        try:
            data = conn.recv(1024)
            if not data:
                conn.close()
                return
            print ('Receive',data)
            obj = json.loads(data.decode())
            db.sendMessage(obj['name'],obj['message'],obj['to'])
            NotifyAll()
        except:
            print ('Exit',data)
            return

            #came out of loop

def NotifyAll():
    global data
    if con.acquire():
        print('Notify',data)
        con.notifyAll()
        con.release()

def ClientThreadOut(conn):
    global data
    while True:
        if con.acquire():
            con.wait()
            if data:
                try:
                    conn.send(data)
                    con.release()
                except:
                    con.release()
                    return


while 1:
    #wait to accept a connection - blocking call
    conn, addr = s.accept()
    print ('Connected with ' + addr[0] + ':' + str(addr[1]))
    #send only takes string
    #start new thread takes 1st argument as a function name to be run, second is the tuple of arguments to the function.
    threading.Thread(target = clientThreadIn , args = {conn}).start()
    threading.Thread(target = ClientThreadOut , args = {conn}).start()

s.close()