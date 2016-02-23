package com.BloodliviyKot.tools.Protocol;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

//Класс запросов на PHP (HTTP) сервер
public class PHP_Poster
  implements ResponseHandler<String>
{
  public static final int TIMEOUT = 1500; //Время ожидания
  private static List<Cookie> cookies; //Cookie для того что бы сессия работала
  //ВНИМАНИЕ (запускать в главном потоке нельзя !!!, т.к. андроид не работает с сетью в ГП)
  public String Post(String url,                //IP или домен страницы сервера
                     List<NameValuePair> params //список POST параметров (имя, значение)
                    ) throws IOException
  /* Example
    List<NameValuePair> POST_Parm = new ArrayList<NameValuePair>(2);
    POST_Parm.add(new BasicNameValuePair("RequestType", "Тестовые параметры"));
    POST_Parm.add(new BasicNameValuePair("Логин", "  Спросили логин"));
    String Url = "http://192.168.10.108";
    PHP_Poster PP = new PHP_Poster();
    String response = PP.post(Url, POST_Parm);
  */
  {
    CookieStore cookieStore = new BasicCookieStore();
    for (int i = 0; cookies != null && i < cookies.size(); i++)
      cookieStore.addCookie(cookies.get(i));
    BasicHttpContext localContext = new BasicHttpContext();
    //Настраиваем параметры HTTP протокола
    HttpParams httpParams = new BasicHttpParams();
    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
    HttpProtocolParams.setContentCharset(httpParams, "UTF-8");
    HttpProtocolParams.setHttpElementCharset(httpParams, "UTF-8");
    httpParams.setBooleanParameter("http.protocol.expect-continue", false);
    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT);
    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT);
    localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

    //Создаем HTTP клиента
    HttpClient httpClient = new DefaultHttpClient(httpParams);
    httpClient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
    httpClient.getParams().setParameter("http.socket.timeout", TIMEOUT);
    httpClient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);

    //Создаем POST запрос
    HttpPost httpPost = new HttpPost(url);
    httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
    //Делаем запрос на сервер который обработает handleResponse
    String result_execute = httpClient.execute(httpPost, this, localContext);
    cookies = cookieStore.getCookies();
    return result_execute;
  }

  //Обработчик ответа с сервера
  @Override
  public String handleResponse(HttpResponse httpResponse)
    throws ClientProtocolException, IOException
  {
    //Из сущности HTTP получаем поток для чтения
    HttpEntity httpEntity = httpResponse.getEntity();
    InputStream is = httpEntity.getContent();
    BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    //В цикле вынимаем все строки и формируем одну итоговую строку
    StringBuilder sb = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null)
    {
      sb.append(line + "\n");
    }
    is.close();
    //Возвращаем результирующую строку
    return sb.toString();
  }

  //Сбрасываем куки Cookie для прерывания текущей сесии
  public static void dropCookie()
  {
    cookies = null;
  }

}
