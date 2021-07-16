package com.android1.socialnetwork.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardsSource;
import com.android1.socialnetwork.data.CardsSourceImpl;

public class SocialNetworkFragment extends Fragment {

    public static SocialNetworkFragment newInstance() {
        return new SocialNetworkFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_socialnetwork, container,false); // Надуваем layout для этого фрагмента
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view_lines);
//        String[] data = getResources().getStringArray(R.array.titles);
        CardsSource data = new CardsSourceImpl(getResources()).init(); // Получим источник данных для списка
        initRecyclerView(recyclerView, data);
        return view;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initRecyclerView(RecyclerView recyclerView, CardsSource data){
        recyclerView.setHasFixedSize(true); // Установка для повышения производительности системы (все эл-ты списка одинаковые по размеру, обработка ускорится)

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext()); // Работаем со встроенным менеджером
        recyclerView.setLayoutManager(layoutManager);

        SocialNetworkAdapter adapter = new SocialNetworkAdapter(data); // Установим адаптер
        recyclerView.setAdapter(adapter);

        //  Добавим разделитель карточек
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator,null));
        recyclerView.addItemDecoration(itemDecoration);

        /*Не забываем объявить менеджер в макете app:layoutManager="LinearLayoutManager" (если менеджер стандартный)*/

        adapter.SetOnItemClickListener((view, position) -> // Установим слушателя
                toastOnItemClickListener(position));
    }

    @SuppressLint("DefaultLocale")
    private void toastOnItemClickListener(int position) {
        Toast.makeText(getContext(), String.format("Позиция - %d", position), Toast.LENGTH_SHORT).show();
    }
}