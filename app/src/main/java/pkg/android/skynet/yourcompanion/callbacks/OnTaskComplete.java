package pkg.android.skynet.yourcompanion.callbacks;

public interface OnTaskComplete {
  void onTaskComplete(int res_code, String res_message, String res_result, String webService);
}
