# FCM XMPP Connection Server

This is the Wifiesta Firebase Cloud Messaging (FCM) XMPP Connection Server. This server sends data to a client app via the FCM CCS Server using the XMPP protocol.
 
For more information must read the following documentation: 
 
 * [Firebase Cloud Messaging](https://firebase.google.com/docs/cloud-messaging/): There you will see introduction to FCM.
* [FCM Server](https://developers.google.com/cloud-messaging/server): There you will see the technical requirements for this application server.
* [FCM XMPP Connection Server](https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref): There you will see the syntax of upstream messages and downstream messages.
* [About FCM Messages](https://firebase.google.com/docs/cloud-messaging/concept-options): There you will see the structure of thetype of messages.
* [Send Messages](https://firebase.google.com/docs/cloud-messaging/send-message): There you will see how to send messages to FCM CSS.
 
 
##Architecture
 
 1. **Downstream Messages:** server-to-device through FCM
 ![Downstream Message](http://wedevol.com/fcm-sources/downstream.png)
 
 2. **Upstream Messages:** device-to-server through FCM
  ![Upstream Message](http://wedevol.com/fcm-sources/upstream.png)

##How it works?

 * First, you set up a java server which connects to FCM using XMPP protocol.
 * Then, from a device you send an upstream message to FCM who then sends that upstream to your server (FCM XMPP Connection Server).
 * Then, within that server you handle that upstream message to send a downstream message to the targeted device(s) through FCM. (You can handle in the way you want. Here I provide 3 action types: register, message and echo).
 * Finally, on the device side you handle those downstream messages being received to give a push notification. (This part need to be developed in the android or iOS device)

##Libraries used

 * [Smack](http://www.igniterealtime.org/projects/smack/)
 * [Json-simple](https://code.google.com/archive/p/json-simple/)
 
##How to start the server
The magic is done in the `server` package. The `CcsClient.java` class manages the connection and the message processing.

The entry point class is `EntryPointDev.java` that contains a main method which takes three arguments:

1. The FCM project sender ID
2. The FCM server key
3. A registration id to send a test message

If you start it that way, the GUI of the Smack library is used to show incoming and outgoing messages.

Of course, you also can use CcsClient from within any other project. 
In that case you first have to call `prepareClient()` and pass it the FCM project sender ID and the FCM server key as arguments. The third argument decides whether the GUI should be shown or not. In production servers you have to set this to false.

For this sample all incoming messages must follow a certain format. That is, they must contain at least an action key with a supported value. This action key determines which PayloadProcessor to create. The implementations of PayloadProcessor (EchoProcessor, MessageProcessor and RegisterProcessor) finally handle the incoming messages and perform the appropriate actions.

Additionally you can see an implementation and test of the exponential back-off that Google suggests and should be used in the server to handle requests and resend them. (`BackOffStrategy.java`)

##Credentials
To run this project you need a FCM project sender ID and a FCM server key. You can create the firebase project and get the credentials on the [Create new project page of Firebase's documentation](https://console.firebase.google.com/).

Those are passed into the CcsClient by either calling the prepareClient() method or by providing those as arguments to the EntryPoint main method.

##Chat App - Wifiesta: Firebase + XMPP + FCM
 
 1. When the app is in foreground: You can use Firebase realtime database to send and receive messages, change user's presence, etc. You don't send upstream messages during this time!
 2. When the user navigates away from the app: The user is now considered "Away" or "Offline". You use the XMPP Connection Server and FCM to handle the incoming messages to send notifications to client devices through FCM downstream messages.
 3. Once the user taps on the notification, the app comes back to foreground and we re-connect to Firebase and back to step 1.

##Chat App - Alternative: Ejabberd + XMPP + FCM
 
 1. When the app is in foreground: You use Smack library to connect directly to your Ejabberd server, send messages, change user's presence, etc. The connection to your Ejabberd is kept during that time. You don't send upstream messages during this time!
 2. When the user navigates away from the app (you close the connection): The user is now considered "Away" or "Offline". You use the XMPP Connection Server and FCM to handle the incoming messages to send notifications to client devices through FCM downstream messages.There's no active connection to the Ejabberd server!
 3. Once the user taps on the notification, the app comes back to foreground and you re-connect to Ejabberd and back to step 1.

 
##Related definitions

 * **XMPP**: eXtensible Messaging and Presence Protocol (XMPP). It is a protocol based on Extensible Markup Language (XML) that was originally designed for instant messaging (IM) and online presence detection. XMPP is a protocol for streaming XML elements in order to exchange messages and presence information in close to real time.
 * **Jabber**: Jabber.org is the original IM service based on XMPP and one of the key nodes on the XMPP network.
 * **Smack**: It is an Open Source XMPP (Jabber) client library for instant messaging and presence. A pure Java library, it can be embedded into your applications to create anything from a full XMPP client to simple XMPP integrations such as sending notification messages and presence-enabling devices.
 * **CCS**: Cloud Connection Server. Some of the benefits of CCS include: 1. The asynchronous nature of XMPP allows you to send more messages with fewer resources. 2. Communication is bidirectional—not only can your server send messages to the device, but the device can send messages back to your server. 3. The device can send messages back using the same connection used for receiving, thereby improving battery life.
 * **FCM**: Firebase Cloud Messaging (FCM) is a cross-platform messaging solution that lets you reliably deliver messages at no cost. Using FCM, you can notify a client app that new email or other data is available to sync. You can send notification messages to drive user reengagement and retention. For use cases such as instant messaging, a message can transfer a payload of up to 4KB to a client app.
 * **FCM Connection Server Protocols**: Currently FCM provides two connection server protocols: HTTP and XMPP. XMPP messaging differs from HTTP messaging in the following ways: 1. Upstream/Downstream messages. 2. Messaging (synchronous - HTPP or asynchronous - XMPP).
 * **Upstream Messaging**: device-to-cloud. Send acknowledgments, chats, and other messages from devices back to your server over GCM’s reliable and battery-efficient connection channel.
 * **Downstream Messaging**: cloud-to-device. For purposes such as alerting users, chat messaging or kicking off background processing before the user opens the client app, GCM provides a reliable and battery-efficient connection between your server and devices.
