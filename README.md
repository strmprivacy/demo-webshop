# Fictional Webshop Demo

This demo was shown at the OpenValue meetup of November 25th 2020. See the recording [here](https://streammachine.io).

## Prerequisites

In order to run this demo, you need the following:

1. Credentials for a Stream (go to our [portal](https://portal.streammachine.io)) and sign up
2. Set the environment variables `BILLING_ID`, `CLIENT_ID`, and `CLIENT_SECRET` when running the backend.
3. A client for the websocket egress interface of Stream Machine (use one of our drivers)

## Running the demo

In order to start the demo, run the following command:

```
BILLING_ID=<your_billing_id> CLIENT_ID=<your_client_id> CLIENT_SECRET=<your_client_secret> make start
``` 

If you have errors with the client secret characters in your shell, put single quotes around your client secret. Open your browser at `http://localhost:8080/store`.

## Need help?

See our [documentation](https://docs.streammachine.io) or [reach out to us](https://docs.streammachine.io/docs/0.1.0/contact/index.html).
