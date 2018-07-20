# RoverGPSControl
Robot GPS information, provides the robot with info as it lacks sensers, it tells it to turn and when its there
current use in python


    import socket
    import sys
    import threading
    import time

    turn = 0


    class MyThread(threading.Thread):
        def run(self):
            global turn

            print("{} started!".format(self.getName()))

            # Create a TCP/IP socket
            sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

            # Bind the socket to the port
            server_address = ('192.168.1.107', 9152)
            print >> sys.stderr, 'starting up on %s port %s' % server_address
            sock.bind(server_address)

            # Listen for incoming connections
            sock.listen(1)

            # Wait for a connection
            print >> sys.stderr, 'waiting for a connection'
            connection, client_address = sock.accept()
            print >> sys.stderr, 'connection from', client_address

            while True:
                try:
                    # Receive the data
                    data = connection.recv(1024)
                    print >> sys.stderr, 'received "%s"' % data

                    if not data:
                        break

                    turn = data

                    if turn == "END":
                        connection.close()
                        print "End Transmission"
                        break

                except socket.error:
                    print "Error Occurred"
                    break


    if __name__ == '__main__':
        # print socket.gethostbyname(socket.gethostname())
        mythread = MyThread(name="sumo thread")  # ...Instantiate a thread and pass a unique ID to it
        mythread.start()  # ...Start the thread
