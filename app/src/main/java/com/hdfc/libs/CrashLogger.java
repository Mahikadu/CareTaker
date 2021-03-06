package com.hdfc.libs;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.NotificationCompat;

import com.hdfc.caretaker.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Random;

/**
 * Catches uncaught exceptions to allow user to mail the exception report to an email ID specified in res/strings.xml > error_email
 *
 * @author Aravind Baskaran <aravind@cloudpact.com>
 */
@SuppressWarnings("deprecation")
public class CrashLogger implements Thread.UncaughtExceptionHandler {
    public static final String CRASH_LOG_MAIL_SUBJECT = " Crash Log";
    public static final String CRASH_LOG_MAIL_BODY = "Following is the crash log info of the application.";
    private static CrashLogger mCrashLogger;
    private String
            VersionName,
            PackageName,
            FilePath,
            PhoneModel,
            AndroidVersion,
            Board,
            Brand,
            CPU_ABI,
            Device,
            Display,
            FingerPrint,
            Host,
            ID,
            Manufacturer,
            Model,
            Product,
            Tags,
            Type,
            User;
    private long Time;
    private Thread.UncaughtExceptionHandler defaultHandler;
    private Context context;

    public static CrashLogger getInstance() {
        if (mCrashLogger == null) {
            mCrashLogger = new CrashLogger();
        }
        return mCrashLogger;
    }

    public void init(Context ctx) {
        context = ctx;
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        collectDeviceInformation();
    }

    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    private void collectDeviceInformation() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi;
            // Version
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            VersionName = pi.versionName;
            // Package name
            PackageName = pi.packageName;
            // Files dir for storing the stack traces
            //FilePath = Configurator.getInstance(context).getLogsDirectory().getAbsolutePath();
            FilePath = context.getFilesDir().getAbsolutePath();
            // Device model
            PhoneModel = android.os.Build.MODEL;
            // Android version
            AndroidVersion = android.os.Build.VERSION.RELEASE;

            Board = android.os.Build.BOARD;
            Brand = android.os.Build.BRAND;
            CPU_ABI = android.os.Build.CPU_ABI;
            Device = android.os.Build.DEVICE;
            Display = android.os.Build.DISPLAY;
            FingerPrint = android.os.Build.FINGERPRINT;

            Host = android.os.Build.HOST;
            ID = android.os.Build.ID;
            Manufacturer = android.os.Build.MANUFACTURER;
            Model = android.os.Build.MODEL;
            Product = android.os.Build.PRODUCT;
            Tags = android.os.Build.TAGS;
            Time = android.os.Build.TIME;
            Type = android.os.Build.TYPE;
            User = android.os.Build.USER;
        } catch (NameNotFoundException e) {
        }
    }

    public String createCrashLog() {
        StringBuffer logBuf = new StringBuffer();
        logBuf.append("Version : " + VersionName);
        logBuf.append("\n");
        logBuf.append("Package : " + PackageName);
        logBuf.append("\n");
        logBuf.append("FilePath : " + FilePath);
        logBuf.append("\n");
        logBuf.append("Phone Model" + PhoneModel);
        logBuf.append("\n");
        logBuf.append("Android Version : " + AndroidVersion);
        logBuf.append("\n");
        logBuf.append("Board : " + Board);
        logBuf.append("\n");
        logBuf.append("Brand : " + Brand);
        logBuf.append("\n");
        logBuf.append("CPU ABI : " + CPU_ABI);
        logBuf.append("\n");
        logBuf.append("Device : " + Device);
        logBuf.append("\n");
        logBuf.append("Display : " + Display);
        logBuf.append("\n");
        logBuf.append("Finger Print : " + FingerPrint);
        logBuf.append("\n");
        logBuf.append("Host : " + Host);
        logBuf.append("\n");
        logBuf.append("ID : " + ID);
        logBuf.append("\n");
        logBuf.append("Manufacturer : " + Manufacturer);
        logBuf.append("\n");
        logBuf.append("Model : " + Model);
        logBuf.append("\n");
        logBuf.append("Product : " + Product);
        logBuf.append("\n");
        logBuf.append("Tags : " + Tags);
        logBuf.append("\n");
        logBuf.append("Time : " + Time);
        logBuf.append("\n");
        logBuf.append("Type : " + Type);
        logBuf.append("\n");
        logBuf.append("User : " + User);
        logBuf.append("\n");
        logBuf.append("Total Internal memory : " +
                getTotalInternalMemorySize());
        logBuf.append("\n");
        logBuf.append("Available Internal memory : " +
                getAvailableInternalMemorySize());
        logBuf.append("\n");
        return logBuf.toString();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        StringBuffer reportBuf = new StringBuffer();

        Date CurDate = new Date();
        reportBuf.append("Error Report collected on : " + CurDate.toString());
        reportBuf.append("\n\nCrash Info :\n--------------\n\n");
        reportBuf.append(createCrashLog());

        reportBuf.append("\n\nStack:\n-------\n");
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        reportBuf.append(stacktrace);

        reportBuf.append("\nCause:\n------- \n");
        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            reportBuf.append(result.toString());
            cause = cause.getCause();
        }
        printWriter.close();

        reportBuf.append("****  End of Report ***");

        writeLogToFile(reportBuf.toString());
        createNotification(context, reportBuf.toString());

        defaultHandler.uncaughtException(t, e);
    }

    private void writeLogToFile(String log) {
        try {
            Random generator = new Random();
            int random = generator.nextInt(99999);
            String logFilePath = this.FilePath + "/log-" + random + ".ecl";
            FileOutputStream trace = new FileOutputStream(logFilePath);
            trace.write(log.getBytes());
            trace.close();
        } catch (Exception e) {
        }
    }

    private String[] getLogFilesList() {
        File dir = new File(this.FilePath);

        // Filter for ".stacktrace" files
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".ecl");
            }
        };

        return dir.list(filter);
    }

    private boolean checkLogExists() {
        return getLogFilesList().length > 0;
    }

    public void checkAndSendLogs(final Context context) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    if (checkLogExists()) {
                        StringBuffer cummulativeLogBuf = new StringBuffer();
                        String[] ErrorFileList = getLogFilesList();
                        int curIndex = 0;
                        // Send reports as a set of 5
                        // More than 5 crashes, user may not use the app.
                        final int MaxSendMail = 10;
                        for (String curString : ErrorFileList) {
                            if (curIndex++ <= MaxSendMail) {
                                cummulativeLogBuf.append(
                                        "Log " + curIndex + ":\n-----\n");
                                String filePath = FilePath + "/" + curString;
                                BufferedReader input =
                                        new BufferedReader(
                                                new FileReader(
                                                        new File(filePath)), 8092);
                                String line;
                                while ((line = input.readLine()) != null) {
                                    cummulativeLogBuf.append(line + "\n");
                                }
                                input.close();
                            }

                            // delete log
                            File curFile = new File(FilePath + "/" + curString);
                            curFile.delete();
                        }
                    }
                } catch (Exception e) {
                }
                return false;
            }
        };
    }

    public void createNotification(Context context, String log) {

        String applicationName = context.getString(R.string.app_name);
        //NotificationManager mNotificationManager = (NotificationManager)
        //context.getSystemService(Context.NOTIFICATION_SERVICE);

        int icon = R.mipmap.notification;
        CharSequence tickerText = applicationName + " error";
        long when = System.currentTimeMillis();
        //Notification notification = new Notification(icon, tickerText, when);
        ///notification.flags = Notification.FLAG_AUTO_CANCEL;

        String subject = applicationName + CRASH_LOG_MAIL_SUBJECT;
        String body = CRASH_LOG_MAIL_BODY + "\n" + log;

        Intent sendIntent = new Intent(Intent.ACTION_SEND);

        //todo change email id
        sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"balamurugan@adstringo.in"});
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        sendIntent.setType("message/rfc822");

        PendingIntent contentIntent =
                PendingIntent.getActivity(context, 0,
                        Intent.createChooser(sendIntent, tickerText), 0);

        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.icon_notification);

        NotificationCompat.Builder b = new NotificationCompat.Builder(context);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(when)
                .setLargeIcon(largeIcon)
                .setSmallIcon(icon)
                .setTicker(tickerText)
                .setContentTitle(tickerText + " report. Click to review and send.")
                .setContentText("from " + applicationName + " Service")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            b.setColor(context.getResources().getColor(R.color.colorPrimaryDark));
        }


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

        checkAndSendLogs(context);

        try {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(homeIntent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}