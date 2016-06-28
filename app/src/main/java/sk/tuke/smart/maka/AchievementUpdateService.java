package sk.tuke.smart.maka;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class AchievementUpdateService extends IntentService {

    private AchievementDatabaseHandler adb;

    private Result result;

    private List<Achievement> achievementList;

    public AchievementUpdateService() {
        super("AchievementUpdateService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        adb = new AchievementDatabaseHandler(this,null,null,1);

        result = adb.getValues();

        achievementList = adb.getAchievements();

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        for(Achievement achievement : achievementList) {
            if (matchAchievement(achievement, result)){
                adb.upDateAchievement(achievement.getName());

                NotificationCompat.Builder notification =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Achievement unlocked!")
                                .setContentText(achievement.getName()+"\n"+achievement.getDescription());
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                notificationManager.notify(0, notification.build());

            }
        }




    }

    private boolean matchAchievement(Achievement achievement, Result result){
        String pattern = "mm:ss.SSS";

        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date resultT;
        Date achievementT;
        boolean time = false;
        try {
            resultT = format.parse(result.getTime());
            achievementT = format.parse(achievement.getTime());
            time = achievementT.getTime() > 0 && resultT.getTime()> achievementT.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }





        boolean distance = Float.parseFloat(achievement.getPace()) > 0 && Float.parseFloat(result.getPace()) > Float.parseFloat(achievement.getPace());

        boolean pace = Float.parseFloat(achievement.getPace()) > 0 && Float.parseFloat(result.getPace()) > Float.parseFloat(achievement.getPace());

        return time || distance || pace;
    }

}
