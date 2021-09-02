# microservices-kafka-cassandra-producer
implementation of microservices using kafka ,java sprinboot and cassandra

## Employee producer

* This project runs on port 8001 by default you can configure it in application.properties
* create cassandra instance has keyspace created with the name bootcamp
* the code will take care of creation of table in keyspace when the code is run for the first time
* By default, this project uses cassandra container running on docker with the name **cassandra-node**. If you are running cassandra locally make sure to change the spring.data.cassandra.contact-points property to **localhost** during runtime
* Have topic with the name **app_updates** created in your kafka cluster before running the app
* The executable jar file can be found in the root path with the name employee-producer.jar
* Java 8 should be configured in your system to run the jar file

#### Steps to run the executable jar file are
* Clone the project
* Navigate to the project's root folder
* Run the following command 
```bash
java -jar employee-producer.jar
```

### REST Endpoints exposed are:

```bash
POST - http://localhost:{port}/createEmployee
```
```bash
GET - http://localhost:{port}/findEmpSkillset
```
