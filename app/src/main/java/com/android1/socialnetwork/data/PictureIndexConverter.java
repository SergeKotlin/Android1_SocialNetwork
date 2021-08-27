package com.android1.socialnetwork.data;

import com.android1.socialnetwork.R;

import java.util.Random;

public class PictureIndexConverter {

    // Работа с изображениями
    /* Пока просто сделаем преобразование идентификатора изображения в порядковый номер и обратно.
    Это сделано, чтобы точно установить, какая картинка будет на карточке. Потому что студия
    не гарантирует, что идентификаторы не будут меняться от версии к версии приложения. */

    private static final Random rnd = new Random();
    private static final Object syncObj = new Object();

    private static int[] picIndex = {R.drawable.nature1,
        R.drawable.nature2,
        R.drawable.nature3,
        R.drawable.nature4,
        R.drawable.nature5,
        R.drawable.nature6,
        R.drawable.nature7,
    };

    public static int randomPictureIndex(){
        synchronized (syncObj){
            return rnd.nextInt(picIndex.length);
        }
    }

    public static int getPictureByIndex(int index){
        if (index < 0 || index >= picIndex.length){
            index = 0;
        }
        return picIndex[index];
    }

    public static int getIndexByPicture(int picture){
        for(int i = 0; i < picIndex.length; i++){
            if (picIndex[i] == picture){
                return i;
            }
        }
        return 0;
    }
}
