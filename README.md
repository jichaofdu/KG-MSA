Set Up:

mvn clean package;
docker-compose build;
docker-compose up

After waiting the system set up:

Visit: http://YourIpAddress:9411/zipkin/ Like: http://10.141.212.134:9411/zipkin/
But you may see nothing in Zipkin. Because there is not trace.
Try login and do some search in TrainTicket, and you will see the trace in Zipkin.



Close the system:

docker-compose down

Clear the mirror:

docker volume rm $(docker volume ls -qf dangling=true)
docker images|grep none|awk '{print $3 }'|xargs docker rmi