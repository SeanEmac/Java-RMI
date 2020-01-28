// Client.java

package ct414;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {
    public static void main(String args[]) {
        int registryport = 1099;

        if (args.length > 0)
           registryport = Integer.parseInt(args[0]);

        System.out.println("RMIRegistry port = " + registryport);

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry(registryport);
            ExamServer exam = (ExamServer) registry.lookup(name);
            int a = exam.login(163, "password");
            System.out.println(a);
        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
    }    
}