# Exponential back-off strategy

This is a base class to handle requests and resend them using exponential back-off.

In the source code you will find:

1. The class `BackOffStrategy.java`
2. The unit test`BackOffStrategyTest.java`

Basically, this code gives you an idea of how to implement an exponential back-off strategy to resend a request when there is an unsuccessful call. You can configure the number of retries and the default waiting time. In the unit test you will see some examples of how to use it. You can make it more complex according your development requirements (handle custom exceptions, wait more time,  add some custom logic, etc).

This was my base class for the implementation of:

* A backend webservice with retry logic
* XMPP Connection Server for FCM(Firebase Cloud Messaging)

## Technology used

 * Java 8
 * JUnit 5
 * Maven 3.6.x
 

## About me
I am Carlos Becerra, a very passionate developer. You can contact me via:

* [Google+](https://plus.google.com/+CarlosBecerraRodr%C3%ADguez)
* [Twitter](https://twitter.com/CarlosBecerraRo)

## License
```javas
Copyright 2020 Carlos Becerra

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
