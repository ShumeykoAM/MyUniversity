package com.BloodliviyKot.OurBudget.Dialogs;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

//Параметры выбранного типа
public class DialogParamsSelectedType
  implements Parcelable
{
  public long id_type;
  public long id_unit;
  public Double count;

  public DialogParamsSelectedType(long _id_type, boolean isFiction)
  {
    id_type = _id_type;
    if(!isFiction)
    {
      //Подкачаем значения на основе данных из базы (по последним или по средним и т.д.)

      count = new Double( 23.54);
    }
  }


  //Копаратор для быстрого описка в дереве, сравниваем id_шники -----------------
  public static Comparator comparator = new Comparator<DialogParamsSelectedType>()
  {
    @Override
    public int compare(DialogParamsSelectedType lhs, DialogParamsSelectedType rhs)
    {
      int result = 0;
      if(lhs.id_type > rhs.id_type)
        result = 1;
      else if(lhs.id_type < rhs.id_type)
        result = -1;
      return result;
    }
  };

  //Ниже реализуем Parcelable ---------------------------------------------------
  private DialogParamsSelectedType(Parcel in)
  {
    id_type = in.readLong();
    id_unit = in.readLong();
    if(in.readInt() == 1)
      count = in.readDouble();
  }
  @Override
  public int describeContents()
  {
    return 0;
  }
  public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
  {
    public DialogParamsSelectedType createFromParcel(Parcel in)
    {
      return new DialogParamsSelectedType(in);
    }
    public DialogParamsSelectedType[] newArray(int size)
    {
      return new DialogParamsSelectedType[size];
    }
  };
  @Override
  public void writeToParcel(Parcel dest, int flags)
  {
    dest.writeLong(id_type);
    dest.writeLong(id_unit);
    if(count != null)
    {
      dest.writeInt(1);
      dest.writeDouble(count);
    }
    else
      dest.writeInt(0);
  }
}
