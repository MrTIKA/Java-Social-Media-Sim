Compilation:

javac Server.java

javac User.java


Usage:

java Server <port number> <max number of users>

java User <host name (or ip)> <port number>


Server.java:

Uses socket programming to communicate with up-to and including <max number of users>. Uses protocol messages described
in above document to communicate with user(s). Server accept status updates from user and lets each other  user aware of
other users joining, leaving or updating status using multi-threading.  



Client.java:

Client side application to connect, send status and terminate connection to above sever. Uses protocol messages described
in above document to communicate. They also print out new users joining leaving or posting new statuses to console.



Possible improvements/Known bugs:

The only error checking done by the server and client is checking of legit protocol prefix. If either of them 
Receives something unexpected, they will terminate connection. Better analysis of the possible problem
can be added to inform user what went wrong.

Multiple users with same name can be added. Although in this part this does not cause a problem for the programs main function 
it can be addressed, possibly by using user-ids in the backend.

Neither the server or the clients are designed to handle unexpected loss of communication to each other. Connection 
always assumed to be stable. This can be addressed by error checking on the connection.

System is designed as proof of concept, no security what so ever is present in any step.


--------------
Code based on java skeleton code from: http://www.cs.bu.edu/fac/matta/Teaching/cs455/F17/SM-handout
Please use those instructions while interacting with the code. 


