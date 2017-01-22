import socketserver
global reqs
reqs = []
class MyTCPHandler(socketserver.BaseRequestHandler):
    """
    The RequestHandler class for our server.

    It is instantiated once per connection to the server, and must
    override the handle() method to implement communication to the
    client.
    """

    def handle(self):
        reqs.append(self.request)
        # self.request is the TCP socket connected to the client
        self.data = self.request.recv(1024).strip()
        #print( "{} wrote:".format(self.client_address[0]))
        #print( self.data)      # just send back the same data, but upper-cased
        for req in reqs:
            try:
                req.sendall(self.data)
            except Exception:
                pass
if __name__ == "__main__":
    HOST, PORT = "localhost", 50007

# Create the server, binding to localhost on port 9999
    server = socketserver.TCPServer((HOST, PORT), MyTCPHandler)

# Activate the server; this will keep running until you
# interrupt the program with Ctrl-C
    server.serve_forever()