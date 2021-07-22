package com.android1.socialnetwork.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android1.socialnetwork.MainActivity;
import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardData;
import com.android1.socialnetwork.observer.Publisher;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

// Фрагмент для корректировки данных
public class CardFragment extends Fragment {

    private static final String ARG_CARD_DATA = "Param_CardData";

    private CardData cardData; // Данные по карточке
    private Publisher publisher; // Паблишер, с его помощью обмениваемся

    private TextInputEditText title;
    private TextInputEditText description;
    private DatePicker datePicker;

// «Методы ранней подготовки» (начало)

    // Для редактирования данных
    // Важное примечание!:
    /* Сложный вариант метода, т.к метод статичный - нужно осуществить передачу данных, через аргуметы,
       в настоящий фрагмент - т.е в действителньный объект.
       Конструкторы во Фрагменте лучше НЕ ДЕЛАТЬ  - заметил сэнсэй Владимир.
       Конструктор принимает на вход много информации, context.. & etc - так что его переопределение крайне не рекомендовано. */
    public static CardFragment newInstance(CardData cardData) {
        CardFragment fragment = new CardFragment();
        Bundle args = new Bundle();
        // Важное примечание!:
        /* Аргументы могут передаваться либо через args.putParcelable(), либо через .putSerializable(),
           просто так нельзя чё-то передать,типа через ссылку. Просто так - даже кошки не рождаются ;) */
        args.putParcelable(ARG_CARD_DATA, cardData);
        fragment.setArguments(args);
        return fragment;
    }

    // Для добавления новых данных
    public static CardFragment newInstance() {
        CardFragment fragment = new CardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardData = getArguments().getParcelable(ARG_CARD_DATA); // Забираем данные через аргументы
        }
    }

    // Цепляем фрагмент к активити
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        publisher = activity.getPublisher(); // Забираем publisher из Activity
    }
// Конец «Методов ранней подготовки».

    @Override
    public void onDetach() {
        publisher = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_card, container, false);
        initView(view);
        if (cardData != null) { // если cardData пустая, то это добавление
            populateView();
        }
        return view;
    }

    // Здесь соберём данные из views
    @Override
    public void onStop() {
        super.onStop();
        cardData = collectCardData();
    }

    // Здесь передадим данные в паблишер
    @Override
    public void onDestroy() {
        super.onDestroy();
        publisher.notifySingle(cardData);
    }

    private void initView(View view) {
        title = view.findViewById(R.id.inputTitle);
        description = view.findViewById(R.id.inputDescription);
        datePicker = view.findViewById(R.id.inputDate);
    }

    private void populateView(){
        title.setText(cardData.getTitle());
        description.setText(cardData.getDescription());
        initDatePicker(cardData.getDate());
    }

    // Установка даты в DatePicker
    private void initDatePicker(Date date) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.setTime(date);
        // По идее, ограничивает выбор нижней даты.. На деле нет datePicker.setMinDate(calendar.getTimeInMillis()); // доп. опция - установка минимально  даты
        this.datePicker.init(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
//                null);
                // Сэнсэй Владимир указывает на доп. возможности DatePicker, при прослушке действий на нём:
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        //TODO: react for picker change

                        // view. *разные методы DatePicker*
                    }
                });
    }

    private CardData collectCardData(){ // Соберём данные со всех views
        String title = this.title.getText().toString();
        String description = this.description.getText().toString();
        Date date = getDateFromDatePicker();
        int picture;
        boolean like;
        if (cardData != null){
            picture = cardData.getPicture();
            like = cardData.isLike();
        } else {
            picture = R.drawable.nature1;
            like = false;
        }
        return new CardData(title, description, picture, like, date);
    }

    // Получение даты из DatePicker
    private Date getDateFromDatePicker() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.set(Calendar.YEAR, this.datePicker.getYear());
        cal.set(Calendar.MONTH, this.datePicker.getMonth());
        cal.set(Calendar.DAY_OF_MONTH, this.datePicker.getDayOfMonth());
        return cal.getTime();
    }

}