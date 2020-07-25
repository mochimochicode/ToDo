package com.example.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView txtMemokekka;

    //ListViewToDo専用のArrayList
    List<Map<String, String>> ListToDo = new ArrayList<>();
    //ListViewFinTask専用のArrayList
    List<Map<String, String>> ListFinTask = new ArrayList<>();

    //Mapオブジェクトの生成。
    Map<String, String> data = new HashMap<>();

    List<String> ToDoArray = new ArrayList<>();
    List<String> DeadlineArray = new ArrayList<>();

    //SimpleAdapter第4引数from用データの用意。
    String[] from = {"ToDo", "Deadline"};
    //SimpleAdapter第5引数to用データの用意。
    int[] to = {android.R.id.text1, android.R.id.text2};

    String catchStr = "";
    int kazu = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView lvToDo = findViewById(R.id.lvToDo);
        ListView lvFinTask = findViewById(R.id.lvFinTask);

        //戻るボタンの部品idを取得する。
        Button complete = findViewById(R.id.complete);
        complete.setOnClickListener(new Complete());

        registerForContextMenu(lvToDo);

        //読み込んだデータを取得するための器を準備
        StringBuilder str1 = new StringBuilder();
        //memo.datを開く
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                openFileInput("memo.dat")))) {
            //memo.datから行単位に読み込み、その内容をStringBufferに保存
            String line;
            while((line = reader.readLine()) != null) {
                str1.append(line);
                str1.append(System.getProperty("line.separator"));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] strArray = str1.toString().split("");

        /**
         * 分解作業開始
         */
        //文字列を可視化
        for(int i = 0; i < strArray.length; i++) {
            System.out.print(strArray[i]);
        }

        for(int i = 0; i < (strArray.length - 2); i++) {
            for(int p = i; !(strArray[p].equals(",")); p++) {
                catchStr = catchStr + strArray[p];
                System.out.println(strArray[p]);
                i = p;
            }
            i += 2;
            ToDoArray.add(catchStr);
            catchStr = "";
            for(int p = i; !(strArray[p].equals("!")); p++) {
                catchStr = catchStr + strArray[p];
                System.out.println(strArray[p]);
                i = p;
            }
            kazu += 1;
            i += 1;
            DeadlineArray.add(catchStr);
            catchStr = "";
        }

        //Mapにデータを格納していく
        Map<String, String> finalMap = new HashMap<>();
        for(int i = 0; i < kazu; i++) {
            finalMap.put("ToDo", ToDoArray.get(i));
            finalMap.put("Deadline", DeadlineArray.get(i));
            ListToDo.add(finalMap);

            finalMap = new HashMap<>();
        }

        //SimpleAdapterを生成。
        SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, ListToDo, android.R.layout.simple_list_item_2, from, to);
        //アダプタの登録。
        lvToDo.setAdapter(adapter);


        //読み込んだデータを取得するための器を準備
        StringBuilder str2 = new StringBuilder();
        //memo.tadを開く
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(
                openFileInput("memo.tad")))) {
            //memo.tadから行単位に読み込み、その内容をStringBufferに保存
            String line;
            while((line = reader.readLine()) != null) {
                str2.append(line);
                str2.append(System.getProperty("line.separator"));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] strArray2 = str2.toString().split("");

        //初期化
        catchStr = "";
        kazu = 0;
        ToDoArray.clear();
        DeadlineArray.clear();
        finalMap = new HashMap<>();

        /**
         * 分解作業開始
         */

        for(int i = 0; i < (strArray2.length - 2); i++) {
            for(int p = i; !(strArray2[p].equals(",")); p++) {
                catchStr = catchStr + strArray2[p];
                System.out.println(strArray2[p]);
                i = p;
            }
            i += 2;
            ToDoArray.add(catchStr);
            catchStr = "";
            for(int p = i; !(strArray2[p].equals("!")); p++) {
                catchStr = catchStr + strArray2[p];
                System.out.println(strArray2[p]);
                i = p;
            }
            kazu += 1;
            i += 1;
            DeadlineArray.add(catchStr);
            catchStr = "";
        }


        for(int i = 0; i < kazu; i++) {
            finalMap.put("ToDo", ToDoArray.get(i));
            finalMap.put("Deadline", DeadlineArray.get(i));
            ListFinTask.add(finalMap);

            finalMap = new HashMap<>();
        }

        //SimpleAdapterを生成。
        SimpleAdapter adapter2 = new SimpleAdapter(MainActivity.this, ListFinTask, android.R.layout.simple_list_item_2, from, to);
        //アダプタの登録。
        lvFinTask.setAdapter(adapter2);

    }

    /**
     *オプションメニュー表示を実装
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //メニューインフレーターの取得。
        MenuInflater inflater = getMenuInflater();
        //オプションメニュー用.xmlファイルをインフレート。
        inflater.inflate(R.menu.menu_options_select, menu);
        //親クラスの同名メソッドを呼び出し、その戻り値を返却。
        return super.onCreateOptionsMenu(menu);
    }

    /**
    * オプションメニュー選択時処理メソッド
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //選択されたメニューIDを取得。
        int itemId = item.getItemId();
        //IDのR値による処理の分岐。
        switch(itemId) {
            //「タスク完了をクリア」が選択された場合の処理。
            case R.id.OptionTaskClear :
                ListView lvFinTask = findViewById(R.id.lvFinTask);
                ListFinTask.clear();
                //SimpleAdapterを生成。
                SimpleAdapter adapter2 = new SimpleAdapter(MainActivity.this, ListFinTask, android.R.layout.simple_list_item_2, from, to);
                //アダプタの登録。
                lvFinTask.setAdapter(adapter2);

                //memo.tadへの書き込みを準備

                /**
                 * 現在のArrayList型ListFinTaskの内容を書き出す
                 */

                Iterator<Map<String, String>> it = ListFinTask.iterator();


                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput("memo.tad", Context.MODE_PRIVATE)))) {
                    while(it.hasNext()) {
                        Map<String, String> fin_task = it.next();
                        //ToDo名を取得。
                        String fin_name = (String) fin_task.get("ToDo");
                        //期限を取得。
                        String fin_deadline = (String) fin_task.get("Deadline");

                        writer.write(fin_name + "," + fin_deadline + "!");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

             //「キャンセル」が選択された場合の処理。
            case R.id.OptionCancel :
                //何もしない
                break;
        }
        //親クラスの同名メソッドを呼び出し、その戻り値を返却。
        return super.onOptionsItemSelected(item);
    }
    /**
     * コンテキストメニュー表示を実装
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu,View view, ContextMenu.ContextMenuInfo menuInfo) {
        //親クラスの同名メソッドの呼び出し。
        super.onCreateContextMenu(menu, view, menuInfo);
        //メニューインフレーターの取得。
        MenuInflater inflater = getMenuInflater();
        //コンテキストメニュー用.xmlファイルをインフレート。
        inflater.inflate(R.menu.menu_context_menu_list, menu);
        //コンテキストメニューのヘッダタイトルを設定。
        menu.setHeaderTitle(R.string.menu_list_context_header);
    }

    /**
     * コンテキストメニュー選択時処理メソッドを追加する
     */

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        ListView lvToDo = findViewById(R.id.lvToDo);
        ListView lvFinTask = findViewById(R.id.lvFinTask);

        //長押しされたレビューに関する情報が格納されたオブジェクトを取得。
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //長押しされたリストのポジションを取得。
        int listPosition = info.position;

        //選択されたメニューIDを取得。
        int itemId = item.getItemId();
        //IdのR値による処理の分岐。
        switch(itemId) {
            //「タスク完了」メニューが選択されたときの処理。
            case R.id.menuListContextMove :

                //ポジションから長押しされたメニュー情報(todo)からMapオブジェクトを取得。
                Map<String, String> todo = ListToDo.get(listPosition);

                //ToDo名を取得。
                String todo_name = (String) todo.get("ToDo");
                //期限を取得。
                String deadline = (String) todo.get("Deadline");

                System.out.println("ToDo名：" + todo_name + "　期限：" + deadline);

                //消す作業を行う
                ListToDo.remove(listPosition);

                //SimpleAdapterを生成。
                SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, ListToDo, android.R.layout.simple_list_item_2, from, to);
                //アダプタの登録。
                lvToDo.setAdapter(adapter);

                data = new HashMap<>();

                data.put("ToDo", todo_name);
                data.put("Deadline", deadline);
                ListFinTask.add(data);

                //SimpleAdapterを生成。
                SimpleAdapter adapter2 = new SimpleAdapter(MainActivity.this, ListFinTask, android.R.layout.simple_list_item_2, from, to);
                //アダプタの登録。
                lvFinTask.setAdapter(adapter2);

                //memo.tadへの書き込みを準備

                /**
                 * 現在のArrayList型ListFinTaskの内容を書き出す
                 */

                Iterator<Map<String, String>> it = ListFinTask.iterator();


                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput("memo.tad", Context.MODE_PRIVATE)))) {
                    while(it.hasNext()) {
                        Map<String, String> fin_task = it.next();
                        //ToDo名を取得。
                        String fin_name = (String) fin_task.get("ToDo");
                        //期限を取得。
                        String fin_deadline = (String) fin_task.get("Deadline");

                        writer.write(fin_name + "," + fin_deadline + "!");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                //memo.datへの書き込みを準備

                /**
                 * 現在のArrayList型ListToDoの内容を書き出す
                 */

                Iterator<Map<String, String>> it2 = ListToDo.iterator();


                try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        openFileOutput("memo.dat", Context.MODE_PRIVATE)))) {
                    while(it2.hasNext()) {
                        Map<String, String> todo1 = it2.next();
                        //ToDo名を取得。
                        String todo_name1 = (String) todo1.get("ToDo");
                        //期限を取得。
                        String deadline1 = (String) todo1.get("Deadline");

                        writer.write(todo_name1 + "," + deadline1 + "!");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }



                break;

            //「キャンセル」メニューが選択されたときの処理。
            case R.id.menuListContextCancel :
                //何もしない
                break;
        }
        return super.onContextItemSelected(item);
    }
    /**
     * 「ToDoを登録」をクリックしたときのリスナクラス。
     */
    private class Complete implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            //SimpleAdapter第4引数from用データの用意。
            String[] from = {"ToDo", "Deadline"};
            //SimpleAdapter第5引数to用データの用意。
            int[] to = {android.R.id.text1, android.R.id.text2};

            ListView lvToDo = findViewById(R.id.lvToDo);
            ListView lvFinTask = findViewById(R.id.lvFinTask);

            //「ToDo」入力蘭であるEditTextオブジェクトを取得。
            EditText inputToDo = findViewById(R.id.inputToDo);
            //「月」入力欄であるEditTextオブジェクトを取得。
            EditText inputDeadline_Month = findViewById(R.id.inputDeadline_Month);
            //「日」入力欄であるEditTextオブジェクトを取得。
            EditText inputDeadline_Day = findViewById(R.id.inputDeadline_Day);


            /**
             * 入力された情報を文字型で取得する。
             */
            String ToDo = inputToDo.getText().toString();
            String Month = inputDeadline_Month.getText().toString();
            String Day = inputDeadline_Day.getText().toString();

            data.put("ToDo", ToDo);
            data.put("Deadline", Month + "月" + Day + "日");
            ListToDo.add(data);

            //SimpleAdapterを生成。
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this, ListToDo, android.R.layout.simple_list_item_2, from, to);
            //アダプタの登録。
            lvToDo.setAdapter(adapter);

            inputToDo.setText("");
            inputDeadline_Month.setText("");
            inputDeadline_Day.setText("");

            data = new HashMap<>();

            /**
             * 実験開始!!
             */

            //memo.datへの書き込みを準備

            /**
             * 現在のArrayList型ListToDoの内容を書き出す
             */

            Iterator<Map<String, String>> it = ListToDo.iterator();


            try(BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    openFileOutput("memo.dat", Context.MODE_PRIVATE)))) {
                while(it.hasNext()) {
                    Map<String, String> todo = it.next();
                    //ToDo名を取得。
                    String todo_name = (String) todo.get("ToDo");
                    //期限を取得。
                    String deadline = (String) todo.get("Deadline");

                    writer.write(todo_name + "," + deadline + "!");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

}
