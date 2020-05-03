// Client.java

package ct414;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Scanner;

public class Client {

    private static int studentid;
    private static long token;
    private static Boolean loggedIn = false;
    private static Boolean retry = true;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String args[]) {
        int registryport = 1099;

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            String name = "ExamServer";
            Registry registry = LocateRegistry.getRegistry(registryport);
            // Connects to remote 'ExamServer' on the default rmi registry port 1099
            ExamServer server = (ExamServer) registry.lookup(name);
            System.out.println("- Connected to Exam Server -");

            login(server);
            // This allows the student to keep retrying MCQ's until they are finished
            while (retry) {
                Assessment chosenAssessment = chooseAssesment(server);
                answerQuestions(server, chosenAssessment);

                // Questions have been answered so now the Assessment is submitted
                server.submitAssessment(token, studentid, chosenAssessment);
                System.out.println("Assessment has been submitted.\n");

                System.out.println("Do you want to complete another? (y/n)");
                String willRetry = scanner.nextLine();
                // "y" for retry, any other key is a no
                if (!willRetry.equals("y")) {
                    retry = false;
                }
            }
            scanner.close();

        } catch (Exception e) {
            System.err.println("Client exception:");
            e.printStackTrace();
        }
    }

    public static void login(ExamServer exam) throws RemoteException, UnauthorizedAccess {
        // Keep allowing them to retry until they input the correct details
        while (!loggedIn) {
            System.out.println("\nEnter StudentID:");
            // Have to convert the input to an integer
            studentid = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter Password:");
            String password = scanner.nextLine();

            // This is our token to be used in future communications with the server, lasts
            // 5 mins
            token = exam.login(studentid, password);
            if (token >= 0) {
                System.out.println("Logged in successfully!");
                loggedIn = true;
            } else if (token < 0) {
                System.out.println("Failed login, try again!");
            }
        }
    }

    public static Assessment chooseAssesment(ExamServer server)
        throws RemoteException, UnauthorizedAccess, NoMatchingAssessment {
        System.out.println("\nHere are your available assignments:");
        // Print out the Assesments aailable to this student
        List<String> available = server.getAvailableSummary(token, studentid);
        available.forEach((assess) -> {
            System.out.println("- " + assess);
        });
        System.out.println("Choose code:");

        // Error checking on Course Code
        Assessment chosenAssessment = null;
        String courseCode = null;
        // Allow them to retry until they chose a correct Assesment code
        while (chosenAssessment == null) {
            courseCode = scanner.nextLine();
            chosenAssessment = server.getAssessment(token, studentid, courseCode);
            if (chosenAssessment == null) {
                System.out.println("Code not valid, try again");
            }
        }
        System.out.println("\n" + courseCode + " MCQ is starting, chose an option a, b, c\n");
        return chosenAssessment;
    }

    public static void answerQuestions(ExamServer server, Assessment chosenAssessment)
        throws InvalidQuestionNumber, InvalidOptionNumber {
        Boolean redo = true;
        // Retreive all the questions and loop through them
        List<Question> questions = chosenAssessment.getQuestions();
        while(redo) {
            for(int i = 0; i < questions.size(); i++) {
                // Print out Question and answers
                System.out.println("Q" + (i + 1)+ ": " + questions.get(i).getQuestionDetail());
                System.out.println("\ta: " + questions.get(i).getAnswerOptions()[0]);
                System.out.println("\tb: " + questions.get(i).getAnswerOptions()[1]);
                System.out.println("\tc: " + questions.get(i).getAnswerOptions()[2]);
                int answer = - 1;

                // Eror handling the answer input
                while(answer == - 1) {
                    String strAnswer = scanner.nextLine();
                    // this turns abc user inputs into 012 
                    answer = "abc".indexOf(strAnswer.toLowerCase());
                    if (answer == -1){
                        System.out.println("invalid Answer, chose a, b or c");
                    }
                }
                // Complete each question with the user input
                chosenAssessment.selectAnswer(i, answer);
                System.out.println("\n");
            }
            // Shows the student what they have answered and allows them to change answers before submitting
            System.out.println("You selected:");
            for(int i = 0; i < questions.size(); i++) {
                String answers = "abc";
                System.out.println("\tQ" + (i + 1) + ": A:" + answers.charAt(chosenAssessment.getSelectedAnswer(i)));
            }

            System.out.println("Would you like to Redo this Assessment before submitting? (y/n)");
            String willRedo = scanner.nextLine();
            // "y" will let them redo, any other key will not
            if (!willRedo.equals("y")) {
                redo = false;
            }
        }
    }
}