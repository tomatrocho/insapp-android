package fr.insapp.insapp.notifications

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import fr.insapp.insapp.R
import java.util.*

/**
 * Created by thomas on 18/07/2017.
 */

class FirebaseMessaging : FirebaseMessagingService() {

    private val randomNotificationId: Int
        get() = Random().nextInt(9999 - 1000) + 1000

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        Log.d(FirebaseService.TAG, "From: " + remoteMessage!!.from!!)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(FirebaseService.TAG, "Data: " + remoteMessage.data)

            val notification = remoteMessage.notification
            sendNotification(notification?.title, notification?.body, notification?.clickAction, remoteMessage.data)
        }
    }

    private fun sendNotification(title: String?, body: String?, clickAction: String?, data: Map<String, String>?) {
        val intent = Intent(clickAction)
        data?.forEach { key, value -> intent.putExtra(key, value) }

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        val manager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, "channel");

        builder
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setAutoCancel(true)
                .setLights(Color.RED, 500, 1000)
                .setColor(ContextCompat.getColor(applicationContext, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent);

        manager.notify(randomNotificationId, builder.build());
    }

    /*
    when (notification.type) {
        "eventTag", "tag" -> {
            val call1 = ServiceGenerator.create().getUserFromId(notification.sender)
            call1.enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        val user = response.body()

                        val id = resources.getIdentifier(Utils.drawableProfileName(user!!.promotion, user.gender), "drawable", App.getAppContext().packageName)
                        Glide
                            .with(App.getAppContext())
                            .asBitmap()
                            .load(id)
                            .into(notificationTarget)
                    } else {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                }
            })
        }

        "post" -> {
            val call2 = ServiceGenerator.create().getClubFromId(notification.post.association)
            call2.enqueue(object : Callback<Club> {
                override fun onResponse(call: Call<Club>, response: Response<Club>) {
                    if (response.isSuccessful) {
                        val club = response.body()

                        Glide
                                .with(App.getAppContext())
                                .asBitmap()
                                .load(ServiceGenerator.CDN_URL + club!!.profilePicture)
                                .into(notificationTarget)
                    } else {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Club>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                }
            })
        }

        "event" -> {
            val call3 = ServiceGenerator.create().getClubFromId(notification.event.association)
            call3.enqueue(object : Callback<Club> {
                override fun onResponse(call: Call<Club>, response: Response<Club>) {
                    if (response.isSuccessful) {
                        val club = response.body()

                        Glide
                                .with(App.getAppContext())
                                .asBitmap()
                                .load(ServiceGenerator.CDN_URL + club!!.profilePicture)
                                .into(notificationTarget)
                    } else {
                        Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Club>, t: Throwable) {
                    Toast.makeText(App.getAppContext(), "FirebaseMessaging", Toast.LENGTH_LONG).show()
                }
            })
        }

        else -> {
        }
    }
    */

    companion object {

        fun subscribeToTopics() {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("news")
            com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("events")
        }

        fun unsubscribeFromTopics() {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic("news")
            com.google.firebase.messaging.FirebaseMessaging.getInstance().unsubscribeFromTopic("events")
        }
    }
}