package com.android1.socialnetwork.ui;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android1.socialnetwork.R;
import com.android1.socialnetwork.data.CardData;
import com.android1.socialnetwork.data.CardsSource;

// Класс адаптера. Соединяет данные с их отображением. Через встроенный класс ViewHolder показывает данные в пользовательском интерфейсе
// Всё, что связано с пользовательским интерфейсом, будем хранить в пакадже ui
public class SocialNetworkAdapter extends RecyclerView.Adapter<SocialNetworkAdapter.ViewHolder> {

    private final static String TAG = "SocialNetworkAdapter";
//    private String[] dataSource;
    private CardsSource dataSource; // Любая списковская структура данных, и элемент списка во вьюхе м.б любым - кроме фрагментов, они не допускаются
    private OnItemClickListener itemClickListener; // Слушатель, устанавливается извне

    public SocialNetworkAdapter(CardsSource dataSource) { // Передаём в конструктор источник данных (массив. А м.б и запрос к БД)
        this.dataSource = dataSource;
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
//        viewHolder.getTextView().setText(dataSource[i]);
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
//            textView = (TextView) itemView;
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            image = itemView.findViewById(R.id.imageView);
            like = itemView.findViewById(R.id.like);

            // Обработчик нажатий на этом ViewHolder
            image.setOnClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition()+1);
                }
            });

            // Необходимо запоминать изменение таких параметров, как CheckBox. Т.е отображать их состояние и в хранимо массиве -
            // чтобы ненароком выбор пользователя не потерялся при переиспользовании элементов списка
            //TODO Не нашёл, как проверить. Даже при списке в 70 элементов - будто бы ничто не перерисовывается ещё
            like.setOnCheckedChangeListener((buttonView, isChecked) -> {
                dataSource.getCardData(getAdapterPosition()).setLike(isChecked);
            });

            ((Activity)itemView.getContext()).registerForContextMenu(title); // Регистрируем контекстное меню
        }

        public void setData(CardData cardData){
            title.setText(cardData.getTitle());
            description.setText(cardData.getDescription());
            like.setChecked(cardData.isLike());
            image.setImageResource(cardData.getPicture());
        }

//        public TextView getTextView() {
//            return textView;
//        }
    }
}