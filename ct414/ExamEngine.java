
package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamEngine implements ExamServer {

    private Map<Integer, String> logins = new HashMap<>();
    private Map<Integer, String> students = new HashMap<>();
    private Map<String, List<String>> degrees = new HashMap<>();
    // Constructor is required
    public ExamEngine() {
        super();
        // Map student ids to their passwords
        logins.put(1234, "password");
        logins.put(4321, "secret");

        // Map student ids to their degree
        students.put(1234, "CS");
        students.put(4321, "BA");

        // Map degrees to their available courses, both share MA101
        degrees.put("CS", Arrays.asList("MA101", "CT101"));
        degrees.put("BA", Arrays.asList("MA101", "EN101"));
    }

    public long login(int studentid, String password) throws 
                UnauthorizedAccess, RemoteException {

        try {
            // Check the username and password exists in our logins Map
            if(logins.containsKey(studentid) && logins.get(studentid).equals(password)) {
                System.out.println(studentid + " has logged in.");
                long now = new Date().getTime();
                // Give student a timestamp token of when they logged in
                return now;
            } else {
                throw new UnauthorizedAccess("Login details are incorrect");
            }
        } catch (UnauthorizedAccess e){
            System.out.println(e.getMessage());
            // Token will be -1 if login failed
            return -1;
        }	
    }

    public List<String> getAvailableSummary(long token, int studentid) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        try {
            // Always check if their session is still valid
            if (expired(token)) {
                throw new UnauthorizedAccess("Session has expired");
            } else {
                System.out.println("Getting available summary for " + studentid);
                // find this students degree and return the courses available to them
                String degree = students.get(studentid);
                return degrees.get(degree);
            }
        } catch (UnauthorizedAccess e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Assessment getAssessment(long token, int studentid, String courseCode) throws
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {

        try {
            if (expired(token)) {
                throw new UnauthorizedAccess("Session has expired");
            } else {
                // Not case sensitive
                courseCode = courseCode.toUpperCase();
                System.out.println("Getting the assignment for " + courseCode);
                // Find the courses available to this student
                String degree = students.get(studentid);
                List<String> availableCourses = degrees.get(degree);
                Assessment toReturn = null;

                // Check that their choice is valid
                // Stops a BA student trying to take a CS module or vise versa
                if (availableCourses.contains(courseCode)) {
                    // Switch on their choice, return them the assessment they want
                    if(courseCode.equals("MA101")){
                        toReturn = new AdditionMCQ(studentid);
                    } else if (courseCode.equals("CT101")) {
                        toReturn = new ComputerMCQ(studentid);
                    } else if (courseCode.equals("EN101")) {
                        toReturn = new EnglishMCQ(studentid);
                    }
                    
                    // If now is after the closing date then dont return the assessment
                    if(toReturn.getClosingDate().after(new Date())) {
                        return toReturn;
                    } else {
                        throw new NoMatchingAssessment(courseCode + " is out of date");
                    }
                } else {
                    throw new NoMatchingAssessment(courseCode + " is not part of " + studentid + "'s degree");
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public void submitAssessment(long token, int studentid, Assessment completed) throws 
                UnauthorizedAccess, NoMatchingAssessment, RemoteException {
        // A completed assessment has been submitted by a student
        // Assessment could now be graded but I will not implement this as per assignment requirements
        try {
            if (expired(token)) {
                throw new UnauthorizedAccess("Session has expired");
            } else {
                System.out.println("\nAn assessment has been submitted and is ready for grading");
                System.out.println("Student " + studentid + " course " + completed.getInformation());
            }
        } catch (UnauthorizedAccess e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean expired(long token) {
        // This function checks if the students token is more than 5 minutes old
        long now = new Date().getTime();
        int timeLimit = 300000; // 5 minutes

        if(now > (token + timeLimit)) {
            return true;
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        // 1099 is the default rmi registry port
        int registryport = 1099;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }
        try {
            String name = "ExamServer";
            ExamServer engine = new ExamEngine();
            ExamServer stub =
                (ExamServer) UnicastRemoteObject.exportObject(engine, 0);
            Registry registry = LocateRegistry.getRegistry(registryport);
            registry.rebind(name, stub);
            System.out.println("- ExamEngine is Running -");
        } catch (Exception e) {
            System.err.println("ExamEngine exception:");
            e.printStackTrace();
        }
    }
}
