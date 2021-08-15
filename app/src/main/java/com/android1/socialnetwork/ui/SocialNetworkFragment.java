package com.android1.socialnetwork.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android1.socialnetwork.MainActivity;
import com.android1.socialnetwork.Navigation;
import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardData;
import com.android1.socialnetwork.data.CardSourceFirebaseImpl;
import com.android1.socialnetwork.data.CardsSource;
import com.android1.socialnetwork.data.CardsSourceImpl;
import com.android1.socialnetwork.data.CardsSourceResponse;
import com.android1.socialnetwork.observer.Observer;
import com.android1.socialnetwork.observer.Publisher;

// RecyclerView командует адаптером
public class    SocialNetworkFragment extends Fragment {

    private CardsSource data;
    private RecyclerView recyclerView;
    private SocialNetworkAdapter adapter;
    private static final int MY_DEFAULT_DURATION = 1000; // Для анимации, класс DefaultItemAnimator

    private Navigation navigation;
    private Publisher publisher;

    private CardData newCardData = null;
    private final long delayForBackScrollJump = 250;
    // Примечание:
    /* На этапе onResume(), последнем этапе добавления фрагмента, вызывается переход на последний элемент.

       ! Признак добавления данных "moveToLastPosition", из методички, а также прыжок к элементу в
       initRecyclerView() - не рабочий вариант.
       Изначальная задумка:
       После того как завершится редактирование элемента в новом фрагменте, мы вернёмся в метод
       обратного вызова наблюдателя Observer.updateCardData(), система начнёт обновлять этот
       фрагмент и вызовет метод onCreateView() повторно. Нам придётся пересоздать все элементы, а
       также адаптер. */

    public static SocialNetworkFragment newInstance() {
        return new SocialNetworkFragment();
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = new CardsSourceImpl(getResources()).init(); */
    /* Здесь получим источник данных для списка
    Поскольку onCreateView запускается каждый раз
    при возврате в фрагмент, данные надо создавать один раз *//*
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_socialnetwork, container,false); // Надуваем layout для этого фрагмента
        initView(view);
        setHasOptionsMenu(true);
        data = new CardSourceFirebaseImpl().init(cardsData -> adapter.notifyDataSetChanged()); // Для установки данных после чтения из Firestore
        adapter.setDataSource(data);
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

    @Override
    public void onResume() {
        super.onResume();
        if (newCardData != null) {
            data.addCardData(newCardData);
            adapter.notifyItemInserted(data.size() - 1); // Говорит адаптеру добавить новый элемент в список

            //TODO: Подобно костылю, но иного выхода ни Владимиром, ни методичкой не предложено
            // Отсрочим действие. getMainLooper - синхронизация с потоком Main
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.smoothScrollToPosition(data.size() - 1);
                }
            }, delayForBackScrollJump); // Отсрочим действие

            newCardData = null;
        }
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


        adapter = new SocialNetworkAdapter(this); // Установим адаптер
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
        return onItemSelected(item.getItemId()) || super.onOptionsItemSelected(item);
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
        return onItemSelected(item.getItemId()) || super.onContextItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    private boolean onItemSelected(int menuItemId){
        switch (menuItemId){
            case R.id.action_add:
                navigation.addFragment(CardFragment.newInstance(), true); // С помощью Navigation открываем новое окно для сбора данных
                publisher.subscribe(new Observer() {
                    @Override
                    public void updateCardData(CardData cardData) {
                        newCardData = cardData;

                        // Содержимое в onResume()

                        // Предыдущий вариант (рукописное добавление):
                        /* data.addCardData(new CardData("Заголовок " + data.size(),
                                "Описание " + data.size(),
                                R.drawable.nature1,
                                false, Calendar.getInstance().getTime()));

                        adapter.notifyItemInserted(data.size() - 1);
                        recyclerView.scrollToPosition(data.size() - 1); // Таргет на соседние элменты, пролистываются лишь они. Хотя, не фига не пролистывает..
                        // Подключим долгое пролистывания для наглядности вводимой анимации действий (реализовано в initRecyclerView())
                        // recyclerView.smoothScrollToPosition(data.size() - 1); // Придётся пролистывать всё-всё до нужной позиции */
                    }
                });
                return true;
            case R.id.action_update:
                final int updatePosition = adapter.getContextPosition();
                navigation.addFragment(CardFragment.newInstance(data.getCardData(updatePosition)), true);
                publisher.subscribe(cardData -> {
                    data.updateCardData(updatePosition, cardData);
                    adapter.notifyItemChanged(updatePosition);
                });

                /*data.updateCardData(position,
                new CardData("Кадр " + position,
                        data.getCardData(position).getDescription(),
                        data.getCardData(position).getPicture(),
                        false, Calendar.getInstance().getTime()));
                adapter.notifyItemChanged(position);*/
                return true;
            case R.id.action_delete:
                int deletePosition = adapter.getContextPosition();
                data.deleteCardData(deletePosition);
                adapter.notifyItemRemoved(deletePosition);
                return true;
            case R.id.action_clear:
                data.clearCardData();
                adapter.notifyDataSetChanged(); // Говорит адаптеру пересобрать список заново RecyclerView
                return true;
        }
        return false;
    }
}