import sys, http.client
import json
def request(servername,filename):
    server = http.client.HTTPConnection(servername) # connect to http site/server
    server.putrequest('GET', filename) # send request and headers
    server.endheaders() # as do CGI script filenames
    reply = server.getresponse() # read reply headers + data
    if reply.status != 200: # 200 means success
        print('Error sending request', reply.status, reply.reason)
    else:
        data = reply.readlines() # file obj for data received
        reply.close() # show lines with eoln at end
        result = ''
        for line in data: # to save, write data to file
            result += line.decode()
    return json.loads(result)

a =  request('localhost:8000','/polls/')
#a =  request('baidu.com','')
print (a,type(a))
for i in a:
    print (i['id'],i['password'])
