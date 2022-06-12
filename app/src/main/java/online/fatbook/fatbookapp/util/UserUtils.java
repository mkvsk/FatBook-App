package online.fatbook.fatbookapp.util;

import online.fatbook.fatbookapp.core.User;
import online.fatbook.fatbookapp.retrofit.RetrofitFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class UserUtils {

    public static final String FILL_ADDITIONAL_INFO = "fill_additional_info";

    public static final String TAG_USER = "user";

    public static final String TAG_FAT = "fat";

    private static String imageURL = "";

    public static SimpleDateFormat regDateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss",Locale.US);

    public static SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd",Locale.US);

    public static final String APP_PREFS = "app_prefs";

    public static final String USER_LOGIN = "user_pid";



}
