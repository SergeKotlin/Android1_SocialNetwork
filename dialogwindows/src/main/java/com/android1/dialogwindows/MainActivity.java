package com.android1.dialogwindows;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button alert1;
    private Button alert3;
    private Button alertList;
    private Button alertSingleList;
    private Button alertMultiList;
    private Button alertCustom;

    private String[] items;
    private int chosen = -1; // Здесь будет храниться выбранный пункт для списка
    private final boolean[] chosenMulti = {false, false, false}; // Здесь будет храниться информация для мульти-списка

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initResource();
        toControlAlerts();
    }

    private void initView() {
        alert1 = findViewById(R.id.alertDialog1btn);
        alert3 = findViewById(R.id.alertDialog3bts);
        alertList = findViewById(R.id.alertDialogList);
        alertSingleList = findViewById(R.id.alertDialogListSingle);
        alertMultiList = findViewById(R.id.alertDialogListMulti);
        alertCustom = findViewById(R.id.alertDialogCustom);
    }

    private void initResource() {
        items = getResources().getStringArray(R.array.choose);
    }

    private void toControlAlerts() {
        alert1.setOnClickListener(clickAlertDialog1);
        alert3.setOnClickListener(clickAlertDialog3);
        alertList.setOnClickListener(clickAlertDialogList);
        alertSingleList.setOnClickListener(clickAlertDialogListSingle);
        alertMultiList.setOnClickListener(clickAlertDialogListMulti);
        alertCustom.setOnClickListener(clickAlertDialogCustom);
    }

    private final View.OnClickListener clickAlertDialog1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Создаём билдер и передаём контекст приложения
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            // В билдере указываем заголовок окна. Можно указывать как ресурс, так и строку
            builder.setTitle(R.string.exclamation)
                    // Указываем сообщение в окне. Также есть вариант со  строковым параметром
                    .setMessage(R.string.press_button)
                    .setIcon(R.mipmap.ic_launcher_round) // Можно указать и пиктограмму
                    .setCancelable(false) // Из этого окна нельзя выйти кнопкой Back
                    // Устанавливаем кнопку. Название кнопки также можно задавать строкой
                    .setPositiveButton(R.string.button,
                            // Ставим слушатель, нажатие будем обрабатывать
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(MainActivity.this, "Мы " +
                                            "продолжаем", Toast.LENGTH_SHORT).show();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();

            Toast.makeText(MainActivity.this, "Диалог открыт",
                    Toast.LENGTH_SHORT).show();
        }
    };

    private final View.OnClickListener clickAlertDialog3 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle(R.string.exclamation)
                    .setMessage("Я с твоей мамой гулял")
                    .setCancelable(false)
                    // Устанавливаем отрицательную кнопку
                    .setNegativeButton(R.string.no,
                            (dialog, id) -> Toast.makeText(MainActivity.this, "Как бы не так!",
                                    Toast.LENGTH_SHORT).show())
                    // Устанавливаем нейтральную кнопку
                    .setNeutralButton(R.string.dunno,
                            (dialog, id) -> Toast.makeText(MainActivity.this, "Без понятия, о чём вы",
                                    Toast.LENGTH_SHORT).show())
                    // Устанавливаем позитивную кнопку
                    .setPositiveButton(R.string.yes,
                            (dialog, id) -> Toast.makeText(MainActivity.this, "Да",
                                    Toast.LENGTH_SHORT).show());

            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private final View.OnClickListener clickAlertDialogList = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Какого цвета у мамы волосы?")
                    .setCancelable(true)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override @SuppressLint("DefaultLocale")
                        public void onClick(DialogInterface dialogInterface, int item) {
                            Toast.makeText(MainActivity.this,
                                    String.format("Выбран тип волос %d", item + 1),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private final View.OnClickListener clickAlertDialogListSingle = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Какого цвета у мамы волосы?")
                    // Добавляем список элементов
                    .setSingleChoiceItems(items, chosen, new DialogInterface.OnClickListener() {
                        @Override
                        @SuppressLint("DefaultLocale")
                        public void onClick(DialogInterface dialogInterface, int item) {
                            // chosen — выбранный элемент. Если по умолчанию (= -1), то ни один не выбран
                            chosen = item;
                        }
                    })
                    .setNegativeButton("Я не буду отвечать",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "Без ответа",
                                            Toast.LENGTH_SHORT).show();
                            }
                    })
                    .setPositiveButton("Обозначить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (chosen == -1) {
                                Toast.makeText(MainActivity.this,
                                        "Ты не гулял с мамой!",
                                        Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(MainActivity.this, String.format(
                                    "Ок, выбран тип '%s'!",
                                    items[chosen]), Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private final View.OnClickListener clickAlertDialogListMulti = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Какого цвета у мамы волосы?")
                    // Добавляем список элементов
                    .setMultiChoiceItems(items, chosenMulti, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                            // булев chosenMulti — массив с выбранными элементами
                            chosenMulti[i] = b; // При переключении обновляем ячейку в массиве
                        }
                    })
                    .setNegativeButton("Я не буду отвечать",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(MainActivity.this, "Без ответа",
                                            Toast.LENGTH_SHORT).show();
                                }
                            })
                    .setPositiveButton("Обозначить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Собираем выбранные элементы в строку
                            byte num = 0;
                            StringBuilder sb = new StringBuilder();
                            for (int index = 0; index < chosenMulti.length; index++) {
                                if (chosenMulti[index]) {
                                    if (num != 0) sb.append("; ");
                                    sb.append(items[index]);
                                    num++;
                                }
                            }
                            Toast.makeText(MainActivity.this, String.format(
                                    (num == 1) ? "Ок, выбран тип '%s'!" :
                                            "Не гулял ты с мамой! Она не м.б %s",
                                    sb.toString()), Toast.LENGTH_SHORT).show();
                        }
                    });

            AlertDialog alert = builder.create();
            alert.show();
        }
    };

    private final View.OnClickListener clickAlertDialogCustom = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            final View contentView = getLayoutInflater().inflate(R.layout.dialog_custom, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Какого цвета у мамы волосы?")
                    .setView(contentView)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    EditText editText = contentView.findViewById(R.id.editText);
                                    String answer = editText.getText().toString();
                                    Toast.makeText(MainActivity.this, answer,
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    };
}