DistributedSessionManager
=========================
This project implemented a distributed session manager based on zookeeper. We usually use Memchached to manager our sessions, but when the Memcached server crashed, the data will be lost. To solve this problem, I tried the zookeeper. But zookeeper is designed for system that has a lot of reading operations, So wether or not it's appropriate to manager the session data, which has a lot of writing operations, is a question.

Frame.jpg is a simple frame fig for this project(I draw it with my hand because of lacking software).

 
This project is far far away from finished, like session listener, delete the expired session node, and compatibility across servlet container, etc.
