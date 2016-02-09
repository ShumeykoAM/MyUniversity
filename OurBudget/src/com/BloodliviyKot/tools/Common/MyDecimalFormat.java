package com.BloodliviyKot.tools.Common;

import java.math.RoundingMode;
import java.text.DecimalFormat;



public class MyDecimalFormat
  extends DecimalFormat
{
  public MyDecimalFormat(String format_string)
  {
    super(format_string);
    setRoundingMode(RoundingMode.DOWN); //Отсекаем лишние знаки после запятой
  }

  public final String double_format(double value)
  {
    return super.format(value).replaceAll("[,]",".");
  }
}
