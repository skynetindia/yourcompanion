package pkg.android.skynet.yourcompanion.callbacks;

public interface ReqRespCallBack {
  void onResponse(Object resObj, String resMessage, String resCode);
  void onResponse(String response, String resMessage, String resCode);
}
