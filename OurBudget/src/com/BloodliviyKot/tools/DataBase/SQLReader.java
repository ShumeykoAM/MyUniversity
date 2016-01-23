package com.BloodliviyKot.tools.DataBase;

import android.content.res.Resources;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/*
* Класс парсер *.sql и *.ddl файлов ресурсов, возвращает список строк - sql запросов.
* Разделителем запросов в *.sql файле должна быть пустая строка.
*/
public class SQLReader
{
  Resources resources;
  public SQLReader(Resources _resources)
  {
    resources = _resources;
  }

  private static final Pattern CLEAR_PATTERN = Pattern.compile("[\\s]+");
  public List<String> getQueris(int id_sql_res/*R.raw.*/)
  {
    List<String> result = new ArrayList<String>();
    try
    {
      InputStream is = resources.openRawResource(id_sql_res);
      InputStreamReader isr = new InputStreamReader(is, "UTF-8");
      BufferedReader buffreader = new BufferedReader(isr);
      String sline, query = "";
      while((sline = buffreader.readLine()) != null)
      {
        if(sline.length() == 0)
        {
          if(query.length() != 0)
            result.add(query);
          query = "";
        }
        else
        {
          //Удалим все коментарии
          int index = sline.indexOf("--");
          if(index != -1)
            sline = sline.substring(0, index);
          //Удалим все лишние пробелы
          sline = CLEAR_PATTERN.matcher(sline).replaceAll(" ").trim();
          if(query.length() != 0)
            query += " ";
          query += sline;
        }
      }
      if(query != "")
        result.add(query);
    }
    catch(Exception e)
    {    }

    return result;
  }

}
