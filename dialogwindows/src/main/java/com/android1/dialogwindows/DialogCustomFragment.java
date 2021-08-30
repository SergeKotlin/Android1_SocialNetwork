package com.android1.dialogwindows;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class DialogCustomFragment extends DialogFragment {

    private EditText editText;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.dialogfragment_custom, null); // Подключаем макет
        view.findViewById(R.id.button).setOnClickListener(listener); // Устанавливаем слушателя
        editText = view.findViewById(R.id.editText);
        setCancelable(false); // Запретить выход из диалога, ничего не выбрав
        return view;
    }

    private final View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dismiss(); // Закрываем диалог
            // Передаём в activity информацию о нажатой кнопке
            String answer = editText.getText().toString();
            ((MainActivity) requireActivity()).onDialogResult(answer);
        }
    };
}