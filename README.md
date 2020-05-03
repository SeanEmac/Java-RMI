# How to run RMI Assignment

- Start in the directory above ct414
    
        pwd to get current path

        mine is /home/sean/Desktop/4thYear/Sem2/DistSystems/Assignment1

- Compile java code into classes

        javac ct414/*.java

- Create new tab/window in terminal
       
        rmiregistry

- this command runs the server

        java -cp /home/sean/Desktop/4thYear/Sem2/DistSystems/Assignment1 -Djava.rmi.server.codebase=file:/home/sean/Desktop/4thYear/Sem2/DistSystems/Assignment1 -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy ct414/ExamEngine

- this command runs the client

        java -cp /home/sean/Desktop/4thYear/Sem2/DistSystems/Assignment1 -Djava.rmi.server.codebase=file:/home/sean/Desktop/4thYear/Sem2/DistSystems/Assignment1 -Djava.rmi.server.hostname=localhost -Djava.security.policy=client.policy ct414/Client

- login with 1234 'password or 4321 'secret'.
- chose an Assessment and complete mcq.
- client times out after 5 mins