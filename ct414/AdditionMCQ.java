package ct414;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AdditionMCQ implements Assessment {

  private Date closingDate = new Date(1593561600000L);// midnight 31st June
  private List<Question> questions = new ArrayList<>();
  private int[] selectedAnswers = new int[2];
  private int studentid;
  

  public AdditionMCQ (int studentid) {
    this.studentid = studentid;
    MCQQuestion question1 = new MCQQuestion(0, "5 + 6?", new String[]{"-20", "11", "400"});
    MCQQuestion question2 = new MCQQuestion(1, "9 + 10?", new String[]{"19", "0", "40"});
    questions.addAll(Arrays.asList(question1, question2));
  }

  @Override
  public String getInformation() {
    return "MA101";
  }

  @Override
  public Date getClosingDate() {
    return closingDate;
  }

  @Override
  public List<Question> getQuestions() {
    return questions;
  }

  @Override
  public Question getQuestion(int questionNumber) throws
    InvalidQuestionNumber {

    return questions.get(questionNumber);
  }

  @Override
  public void selectAnswer(int questionNumber, int optionNumber) throws
    InvalidQuestionNumber, InvalidOptionNumber {

    selectedAnswers[questionNumber] = optionNumber;
  }

  @Override
  public int getSelectedAnswer(int questionNumber) {
    return selectedAnswers[questionNumber];
  }

  @Override
  public int getAssociatedID() {
    return studentid;
  }

}