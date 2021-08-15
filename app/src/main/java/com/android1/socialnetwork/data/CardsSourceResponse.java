package com.android1.socialnetwork.data;

// Для асинхронных операций чтения из облака Firestore
public interface CardsSourceResponse {
    void initialized(CardsSource cardsData); // Интерфейс с методом обратного вызова. Будет вызываться, когда данные проинициализируются и будут готовы
}
