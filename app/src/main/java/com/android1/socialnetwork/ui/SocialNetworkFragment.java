package com.android1.socialnetwork.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android1.socialnetwork.MainActivity;
import com.android1.socialnetwork.Navigation;
import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardData;
import com.android1.socialnetwork.data.CardsSource;
import com.android1.socialnetwork.data.CardsSourceImpl;
import com.android1.socialnetwork.observer.Observer;
import com.android1.socialnetwork.observer.Publisher;

import java.util.Calendar;

// RecyclerView командует адаптером
public class    SocialNetworkFragment extends Fragment {

    private CardsSource data;
    private RecyclerView recyclerView;
    private SocialNetworkAdapter adapter;
    private static final int MY_DEFAULT_DURATION = 1000; // Для анимации, класс DefaultItemAnimator

    private Navigation navigation;
    private Publisher publisher;
    private boolean moveToLastPosition; /* Признак, что при повторном открытии фрагмента
                                           (возврате из фрагмента,  добавляющего запись)
                                           надо прыгнуть на последнюю запись */
    // Примечание к moveToLastPosition:
    /* После того как завершится редактирование элемента в новом фрагменте, мы вернёмся в метод
       обратного вызова наблюдателя Observer.updateCardData(), система начнёт обновлять этот
       фрагмент и вызовет метод onCreateView() повторно. Нам придётся пересоздать все элементы, а
       также адаптер. Поэтому вводится признак moveToLastPosition, означающий, что мы только что
       добавляли данные, чтобы перепрыгнуть на последний элемент. В методе initRecyclerView()
       вызывается переход на последний элемент. */

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new CardsSourceImpl(getResources()).init(); /* Получим источник данных для списка
                                                              Поскольку onCreateView запускается каждый раз
                                                              при возврате в фрагмент, данные надо создавать один раз */
    }

    public static SocialNetworkFragment newInstance() {
        return new SocialNetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_socialnetwork, container,false); // Надуваем layout для этого фрагмента
        initView(view);
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivity activity = (MainActivity)context;
        navigation = activity.getNavigation();
        publisher = activity.getPublisher();
    }

    @Override
    public void onDetach() {
        navigation = null;
        publisher = null;
        super.onDetach();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recycler_view_lines);
//        data = new CardsSourceImpl(getResources()).init(); // Получим источник данных для списка
        initRecyclerView();
    }

    // RecyclerView - размещает элементы списка, через Менеджера, а также делает запросы к Адаптеру на получение этих данных. Т.о. командует адаптером
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView(){
        recyclerView.setHasFixedSize(true); // Установка для повышения производительности системы (все эл-ты списка одинаковые по размеру, обработка ускорится)

        // Работаем со встроенным менеджером
        // Можно просто объявить менеджер в соотв-щем макете app:layoutManager="LinearLayoutManager" (ЕСЛИ менеджер стандартный)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new SocialNetworkAdapter(data, this); // Установим адаптер
        recyclerView.setAdapter(adapter);

        //  Добавим разделитель карточек
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator,null));
        recyclerView.addItemDecoration(itemDecoration);

        // Установим анимацию. А чтобы было хорошо заметно, сделаем анимацию долгой. В onOptionsItemSelected() - recyclerView.smoothScrollToPosition(data.size() - 1);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);

        if (moveToLastPosition){
            recyclerView.smoothScrollToPosition(data.size() - 1);
            moveToLastPosition = false;
        }

        adapter.SetOnItemClickListener((view, position) -> // Установим слушателя
                toastOnItemClickListener(position));
    }

    @SuppressLint("DefaultLocale")
    private void toastOnItemClickListener(int position) {
        Toast.makeText(getContext(), String.format("Позиция - %d", position), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.cards_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                navigation.addFragment(CardFragment.newInstance(), true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardData(CardData cardData) {
                        data.addCardData(cardData);
                        adapter.notifyItemInserted(data.size() - 1);
                        moveToLastPosition = true; // это сигнал, чтобы вызванный метод onCreateView перепрыгнул на конец списка
                    }
                });

                /*data.addCardData(new CardData("Заголовок " + data.size(),
                        "Описание " + data.size(),
                        R.drawable.nature1,
                        false, Calendar.getInstance().getTime()));
                adapter.notifyItemInserted(data.size() - 1);
                recyclerView.scrollToPosition(data.size() - 1); // Таргет на соседние элменты, пролистываются лишь они. Хотя, не фига не пролистывает..
                // Подключим долгое пролистывания для наглядности вводимой анимации действий (реализовано в initRecyclerView())
                // recyclerView.smoothScrollToPosition(data.size() - 1); // Придётся пролистывать всё-всё до нужной позиции*/
                return true;
            case R.id.action_clear:
                data.clearCardData();
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.card_context, menu);
    }
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int position = adapter.getContextPosition();
        switch(item.getItemId()) {
            case R.id.action_update:
                navigation.addFragment(CardFragment.newInstance(data.getCardData(position)),true);
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardData(CardData cardData) {
                        data.updateCardData(position, cardData);
                        adapter.notifyItemChanged(position);
                    }
                });

                /*data.updateCardData(position,
                        new CardData("Кадр " + position,
                                data.getCardData(position).getDescription(),
                                data.getCardData(position).getPicture(),
                                false, Calendar.getInstance().getTime()));
                adapter.notifyItemChanged(position);*/
                return true;
            case R.id.action_delete:
                data.deleteCardData(position);
                adapter.notifyItemRemoved(position);
                return true;
        }
        return super.onContextItemSelected(item);
    }
}