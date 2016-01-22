package com.BloodliviyKot.tools.Protocol.Answers;

import com.BloodliviyKot.tools.Protocol.E_MESSID;

public class AnswerTestGoogle
  extends Answer
{
  public boolean google_access = true;
  AnswerTestGoogle(int _ID) throws E_MESSID.MExeption
  {
    super(_ID);
  }
}