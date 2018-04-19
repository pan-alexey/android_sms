package panda.smsgateway.database;

/**
 * Created by alexey on 30.09.2017.
 */

/*
Класс-помощник DbHelper отвечает за создание и обновление базы данных. Здесь мы помещаем два ключевых метода onCreate() и onUpgrade(). Первый срабатывает при первом создании базы данных. Если вдруг версия базы данных меняется (константа DATABASE_VERSION), то будет запущен метод onUpgrade(). В нашем случае при обновлении базы мы просто удаляем текущую базу данных и вызываем метод onCreate() для создания новой.
*/
// В методах обновления информации вызываются события для обновления статистики
// Метод отдается в активность. Связь с активность передается в сервис


/*
STATUS:
null - Не обработан
0 -  BUSY - обработан (передан в конвеер)

*/


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DbHelper extends SQLiteOpenHelper {

    public static String LOG_TAG = "DbHelper";

    private final Context m_ctx;
    private static final String DATABASE_NAME = "SMS_INTERFACE.db";
    private static final int DATABASE_VERSION = 1;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        m_ctx = context;
    }

    // Метод вызывается в случае если БД тоько создается
    // Если БД не существует, то вызывается этот метод
    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("create table );");
        Log.e(LOG_TAG, "--- onCreate database ---");
        db.execSQL(
                "CREATE TABLE `SMS_QUEUE` (" +
                       "`ID` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE," +
                       "`SYSTEM_ID` INTEGER UNIQUE," +
                       "`TEXT` INTEGER," +
                       "`PHONE` INTEGER" +
                 ");"
        );
    }
    //--------------------------------------------------------------------------------------------//
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //this.onCreate(db);
    }
    //--------------------------------------------------------------------------------------------//
    public long insertSmsQuery(Context context,  ContentValues contentValues){
        long result = -1;
        DbHelper dbh = null;
        SQLiteDatabase db = null;
        try {
            dbh = new DbHelper(context);
            db = dbh.getWritableDatabase();
            result = db.insert("SMS_QUEUE", null, contentValues);
            //############### Шлем событие о том, что данные обновлились ##################
            //#############################################################################
        } catch (Throwable e) {
            Log.e("--", "isAnyInfoAvailable(): Caught - " + e.getClass().getName(), e);
        } finally {
            if (null != db)
                db.close();
            if (null != dbh)
                dbh.close();
        }
        return result;
    }


    //--------------------------------------------------------------------------------------------//
    public HashMap<String, String> pullFromSmsQuery(Context context){
        HashMap<String, String> mapResult = new HashMap<String, String>();
        DbHelper dbh = null;
        SQLiteDatabase db = null;
        String QUERY = "SELECT * FROM SMS_QUEUE WHERE 1 ORDER BY ID DESC LIMIT 0, 1";
        try {
            dbh = new DbHelper(context);
            db = dbh.getWritableDatabase();
            //result = DbHelper.is_any_info_available(db);
            Cursor cursor = db.rawQuery(QUERY, null);
            if (cursor.moveToFirst()) {
                for (String ColName : cursor.getColumnNames()) {
                    mapResult.put(ColName, cursor.getString(cursor.getColumnIndex(ColName))  );
                }
                String ROW_ID = cursor.getString(cursor.getColumnIndex("ID"));
                int DEL_COUNT = db.delete("SMS_QUEUE", "id = " + ROW_ID, null);
                cursor.close();
            }
            if(cursor != null) cursor.close();
        } catch (Throwable e) {
            Log.e("-----------------", "isAnyInfoAvailable(): Caught - " + e.getClass().getName(), e);
        }  finally {
            if (null != db)
                db.close();
            if (null != dbh)
                dbh.close();
        }
        return mapResult;
    }
    //--------------------------------------------------------------------------------------------//



}





/*
--#####################################--

CREATE TABLE `SMS_QUEUE` (
	`ID`	INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, -- ID В ПРИЛОЖЕНИИ
	`EXTERNAL_ID`	INTEGER UNIQUE, -- ID В СИСТЕМЕ (ПЕРЕДАЕТ СЕРВЕР)
	`UNIX_TIMESTAMP`	INTEGER, -- ВРЕМЯ ПОСТУПЛЕНИЯ СМС В СИСТЕМУ (ПЕРЕДАЕТ СЕРВЕР)
	`PHONE_NUMBER`	TEXT, -- ТЕЛЕФОН ДЛЯ СМС
	`SMS_TEXT`	TEXT, -- ТЕКСТ ДЛЯ СМС
	`PREW_SMS_ID`	INTEGER  DEFAULT NULL, -- ПРЕДЫДУЩИЙ ID ДЛЯ ТЕКУЩЕЙ СМС, НЕОБХОДИМ ЧТОБЫ СМС БЫЛ ПО ПОРЯДКУ (ПЕРЕДАЕТ СЕРВЕР)
	`STATUS`	INTEGER  DEFAULT NULL, -- СТАТУС СМС В ПРИЛОЖЕНИИ
	`STATUS_SEND`	INTEGER  DEFAULT NULL, -- СТАТУС ОТПРАВКИ СМС В ПРИЛОЖЕНИИ
	`STATUS_DELIVER`	INTEGER  DEFAULT NULL -- СТАТУС ДОСТАВКИ СМС В ПРИЛОЖЕНИИ
);
--#####################################--

CREATE TABLE `SMS_SEND` (
	`ID` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, -- ID В ПРИЛОЖЕНИИ
	`EXTERNAL_ID`	INTEGER, -- ID В СИСТЕМЕ
	`SEND_UNIX_TIMESTAMP`	INTEGER, -- ВРЕМЯ ОТПРАВКИ СООБЩЕНИЯ В ПРИЛОЖЕНИИ
	`STATUS`	INTEGER -- СТАТУС СООБЩЕНИЯ
);

--#####################################--

CREATE TABLE `SMS_DELIVER` (
	`ID` INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, -- ID СООБЩЕНИЯ В ПРИЛОЖЕНИИ
	`EXTERNAL_ID`	INTEGER,  -- ID СООБЩЕНИЯ В СИСТЕМЕ
	`SEND_UNIX_TIMESTAMP`	INTEGER, -- ВРЕМЯ ПРИНЯТИЯ СООБЩЕНИЯ В СИСТЕМУ
	`STATUS`	INTEGER -- СТАТУС СООБЩЕНИЯ
);

--#######  Запись в очереди сообщений  ######--Se
INSERT INTO `SMS_QUEUE`(`EXTERNAL_ID`,`UNIX_TIMESTAMP`,`PHONE_NUMBER`,`SMS_TEXT`, `PREW_SMS_ID`)
VALUES (9,1992,89283331431,'Текст смс 2',NULL);




SELECT
	SMS_QUEUE.ID AS ID
	,SMS_QUEUE.EXTERNAL_ID AS EXTERNAL_ID
	,SMS_QUEUE.STATUS AS STATUS
	,SMS_QUEUE.PHONE_NUMBER AS PHONE_NUMBER
	,SMS_QUEUE.SMS_TEXT AS SMS_TEXT
	,SMS_QUEUE.STATUS_DELIVER AS STATUS_DELIVER
	,SMS_QUEUE.STATUS_SEND AS STATUS_SEND
	,SMS_QUEUE.UNIX_TIMESTAMP AS UNIX_TIMESTAMP
	,SMS_QUEUE.PREW_SMS_ID AS PREW_SMS_ID
	,SMS_QUEUE__JOIN_PREW.STATUS_DELIVER AS PREW_STATUS_DELIVER
FROM
	SMS_QUEUE
	LEFT OUTER JOIN
	SMS_QUEUE SMS_QUEUE__JOIN_PREW
		ON SMS_QUEUE.PREW_SMS_ID = SMS_QUEUE__JOIN_PREW.EXTERNAL_ID
WHERE
	SMS_QUEUE.STATUS IS NULL
	AND (
		(SMS_QUEUE__JOIN_PREW.STATUS_DELIVER IS NULL)
		OR ((SMS_QUEUE.PREW_SMS_ID IS NOT NULL)
			 AND (SMS_QUEUE__JOIN_PREW.STATUS_DELIVER = 1 )
		)
	)
ORDER BY UNIX_TIMESTAMP ASC
LIMIT 0, 1
*/