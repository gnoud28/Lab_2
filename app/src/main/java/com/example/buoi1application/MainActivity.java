package com.example.buoi1application;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends Activity {
    private TextView resultTextView;
    private HashingTask hashingTask;

    private static final int MESSAGE_RESULT = 101;
    private static final int MESSAGE_NOT_FOUND = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.result_textview);

        hashingTask = new HashingTask();
        hashingTask.execute();
    }

    private static class HashingWorker {
        public static String sha256(final String base) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hash = digest.digest(base.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xFF & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class HashingTask extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... params) {
            String targetHash = "a918c858d2dd1a3c69163267468804bdcd67daf50de8899183efe63e8412438a";
            int maxLength = 8;
            int time = 700000000; // Số lần lặp
            Flag flag = new Flag();

            return findMatchingHash(targetHash, maxLength, time, flag);
        }

        private String findMatchingHash(String targetHash, int maxLength, int time, Flag flag) {
            String[] characters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                    "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

            // Vòng lặp để thử từng chuỗi
            for (int i = time; i > 0; i--) {
                Random random = new Random();
                List<String> result = new ArrayList<>();

                while (result.size() < maxLength) {
                    int randomIndex = random.nextInt(characters.length);
                    String element = characters[randomIndex];
                    result.add(element);
                }

                if (result.size() == maxLength) {
                    String value = listToString(result);

                    if (flag.isDone()) {
                        break;
                    }
                    if (HashingWorker.sha256(value.trim()).equals(targetHash)) {
                        flag.setDone();
                        return value;
                    }
                }

            }

            return null;
        }

        private String listToString(List<String> list) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < list.size(); i++) {
                stringBuilder.append(list.get(i));
            }
            return stringBuilder.toString();
        }

        private class Flag {
            private boolean done = false;

            public synchronized boolean isDone() {
                return done;
            }

            public synchronized void setDone() {
                done = true;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                handler.sendMessage(handler.obtainMessage(MESSAGE_RESULT, result));
            } else {
                handler.sendEmptyMessage(MESSAGE_NOT_FOUND);
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_RESULT:
                    String result = (String) msg.obj;
                    resultTextView.setText("input: " + result);
                    Log.d("TAG", "handleMessage: " + result);
                    break;
                case MESSAGE_NOT_FOUND:
                    resultTextView.setText("k tìm thấy input");
                    break;
                default:
                    resultTextView.setText("Undefined message");
                    break;
            }
            return true;
        }
    });
}
