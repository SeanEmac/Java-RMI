package ct414;

public class MCQQuestion implements Question {

  private int questionNumber;
  private String questionDetail;
  private String[] answerOptions = new String[3];

  public MCQQuestion(int questionNumber, String questionDetail, String[] answerOptions) {
      this.questionNumber = questionNumber;
      this.questionDetail = questionDetail;
      this.answerOptions = answerOptions;
  }

  @Override
  public int getQuestionNumber() {
    return questionNumber;
  }

  @Override
  public String getQuestionDetail() {
    return questionDetail;
  }

  @Override
  public String[] getAnswerOptions() {
    return answerOptions;
  }
}