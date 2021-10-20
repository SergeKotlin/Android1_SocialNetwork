package com.android1.socialnetwork.data;

// Для получения данных для списка карточек работаем через интерфейс, делая возможным изменить поступление данных - например, из БД - а не из локальных ресурсов
// Проще говоря - соединяем массив (данные) с CardData
public interface CardsSource {
    CardData getCardData(int position);
    int size();
}