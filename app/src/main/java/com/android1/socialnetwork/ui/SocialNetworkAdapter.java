package com.android1.socialnetwork.ui;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardData;
import com.android1.socialnetwork.data.CardsSource;

// Класс адаптера. Соединяет данные с их отображением. Через встроенный класс ViewHolder показывает данные в пользовательском интерфейсе
// Всё, что связано с пользовательским интерфейсом, будем хранить в пакадже ui
public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.ViewHolder> {

    private final static String TAG = "SocialNetworkAdapter";
    private CardsSource dataSource; // Любая списковская структура данных, и элемент списка во вьюхе м.б любым - кроме фрагментов, они не допускаются
    private final Fragment fragment;
    private OnItemClickListener itemClickListener; // Слушатель, устанавливается извне

    public SocialNetworkAdapter(CardsSource dataSource, Fragment fragment) { // Передаём в конструктор источник данных (массив. А м.б и запрос к БД)
        this.dataSource = dataSource;
        this.fragment = fragment;
    }

    // Создадим новый пользовательский элемент
    @Override @NonNull
    public SocialNetworkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) { // Запускается менеджером
        View v = LayoutInflater.from(viewGroup.getContext()) // Создаём новый элемент пользовательского интерфейса
                .inflate(R.layout.item, viewGroup, false);
        Log.d(TAG, "onCreateViewHolder");
        /* Здесь можно установить всякие параметры */
        return new ViewHolder(v);
    }

    // Заменим данные в пользовательском интерфейсе
    @Override
    public void onBindViewHolder(@NonNull SocialNetworkAdapter.ViewHolder viewHolder, int i) { // Вызывается менеджером, подгружает данные и заполняет представления
        // Получить элемент из источника данных (БД, интернет...) и вывести на экран
        viewHolder.setData(dataSource.getCardData(i));
        Log.d(TAG, "onBindViewHolder");
    }

    @Override
    public int getItemCount() { // Вызывается менеджером. Возвращает размер данных
//        return dataSource.length;
        return dataSource.size();
    }

    // Сеттер слушателя нажатий
    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    // Интерфейс обработки нажатий (как в ListView)
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    // Этот класс хранит связь между данными и элементами View (сложные данные могут потребовать несколько View на один пункт списка)
    public class ViewHolder extends RecyclerView.ViewHolder {

//        private TextView textView;
        private TextView title;
        private TextView description;
        private AppCompatImageView image;
        private CheckBox like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.imageView);
            like = itemView.findViewById(R.id.like);

            registerContextMenu(itemView); // Регестрируем Context menu

            // Обработчик нажатий на этом ViewHolder
            image.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition()+1);
                }
            });

            // Необходимо запоминать изменение таких параметров, как CheckBox. Т.е отображать их состояние и в хранимо массиве -
            // чтобы ненароком выбор пользователя не потерялся при переиспользовании элементов списка
            //TODO Не нашёл, как проверить. Даже при списке в 70 элементов - будто бы ничто не перерисовывается ещё
            //КАК ЖЕ! Не нашёл. А логи мы для чего делали? private final static String TAG = "SocialNetworkAdapter"

            //Как же
            like.setOnCheckedChangeListener((buttonView, isChecked) -> {
                dataSource.getCardData(getAdapterPosition()).setLike(isChecked);
            });

            // Обработчик нажатий на картинке
            image.setOnLongClickListener(new View.OnLongClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                public boolean onLongClick(View v) {
                    itemView.showContextMenu(10, 10); // ! У меня была API 22, а showContextMenu() поддерживается с 24 версии.
                                                            // Я чё-т натыркал, чтобы AVD привести сразу к API 30.. (24 недоступна в списке)
                    return true;
                }
                /* Примечание!:
                Если мы попробуем открыть такое меню на кликабельном элементе (нашей картинке), то
                ничего не получится. Придётся вызывать меню при помощи метода showContextMenu().
                Сделаем такую специальную обработку на изображении */
            });

            ((Activity)itemView.getContext()).registerForContextMenu(title); // Регистрируем контекстное меню
        }

        public void setData(CardData cardData){
            title.setText(cardData.getTitle());
            description.setText(cardData.getDescription());
            like.setChecked(cardData.isLike());
            image.setImageResource(cardData.getPicture());
        }

        private void registerContextMenu(@NonNull View itemView) {
            if (fragment != null){
                fragment.registerForContextMenu(itemView); // Регестрируем Context menu
            }
        }
    }
}