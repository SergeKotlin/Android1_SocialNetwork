package com.android1.socialnetwork.data;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.android1.socialnetwork.R;

import java.util.ArrayList;
import java.util.List;

public class CardsSourceImpl implements CardsSource {

    private List<CardData> dataSource;
    private Resources resources; // ресурсы приложения

    public CardsSourceImpl(Resources resources) {
        dataSource = new ArrayList<>(7);
        this.resources = resources;
    }

    public CardsSourceImpl init(){
        String[] titles = resources.getStringArray(R.array.titles); // строки заголовков из ресурсов
        String[] descriptions = resources.getStringArray(R.array.descriptions); // строки описаний из ресурсов
        int[] pictures = getImageArray(); // изображения

        for (int i = 0; i < descriptions.length; i++) { // заполнение источника данных
            dataSource.add(new CardData(titles[i], descriptions[i], pictures[i],false));
        }
        return this;
    }

    // Механизм вытаскивания идентификаторов картинок (https://stackoverflow.com/questions/5347107/creating-integer-array-of-resource-ids)
    private int[] getImageArray() {
        TypedArray pictures = resources.obtainTypedArray(R.array.pictures);
        int length = pictures.length();
        int[] answer = new int[length];
        for (int i = 0; i < length; i++) {
            answer[i] = pictures.getResourceId(i, 0);
        }
        return answer;
    }

    public CardData getCardData(int position) {
        return dataSource.get(position);
    }

    public int size(){
        return dataSource.size();
    }

    @Override
    public void deleteCardData(int position) {
        dataSource.remove(position);
    }
    @Override
    public void updateCardData(int position, CardData cardData) {
        dataSource.set(position, cardData);
    }
    @Override
    public void addCardData(CardData cardData) {
        dataSource.add(cardData);
        /* Примечание!:
        При выборе пункта добавления вызываем метод источника данных addCardData(),
        который добавит данные в список. Затем обязательно надо предупредить RecyclerView.Adapter,
        что мы добавили новую запись, чтобы эта запись была отрисована на экране, методом
        adapter.notifyItemInserted() с указанием позиции этого элемента.
        Метод RecyclerView.scrollToPosition() прыгнет на заданную позицию, таким образом мы увидим
        вновь добавленные данные. Если надо мягко перекрутить весь список, используем метод
        smoothScrollToPosition(), но надо помнить, что в этом случае RecyclerView прочитает все
        элементы до конца. Если же использовать scrollToPosition(), то прочитаются только элементы,
        находящиеся рядом с нужной позицией.*/
    }
    @Override
    public void clearCardData() {
        dataSource.clear();
    }
}
