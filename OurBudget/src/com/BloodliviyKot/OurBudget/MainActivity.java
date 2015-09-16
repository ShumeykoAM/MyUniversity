package com.BloodliviyKot.OurBudget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity
    extends Activity
    implements View.OnClickListener
{

  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    findViewById(R.id.b_create).setOnClickListener(this);

  }

  @Override
  public void onClick(View v)
  {
    switch(v.getId())
    {
      case R.id.b_create:
        Intent intent = new Intent(this, AuthenticationCreate.class);
        startActivity(intent);
        break;
    }
  }
}


































