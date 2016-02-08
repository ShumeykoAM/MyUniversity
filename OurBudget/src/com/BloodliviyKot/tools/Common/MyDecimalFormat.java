package com.BloodliviyKot.tools.Common;

import java.text.DecimalFormat;



public class MyDecimalFormat
  extends DecimalFormat
{
  public MyDecimalFormat(String format_string)
  {
    super(format_string);
  }

  public final String double_format(double value)
  {
    return super.format(value).replaceAll("[,]",".");
  }
}
