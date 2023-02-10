# kop-custom-auth-provider

[KoP](https://github.com/streamnative/kop) with custom authentication provider.

## Set up the KoP

Run the [Ory Hydra](https://www.ory.sh/hydra/) as the OAuth2 authorization server:

```bash
docker compose -f hydra/docker-compose.yml up -d
```

> **NOTE**
>
> Make sure you have installed the following commands before going ahead:
> - `docker`
> - `jq`
> - `node`/`npm`
> - `javac`/`mvn`

Run the following commands:

```bash
# Generate the standalone.conf
./configure.sh
# Build the custom authentication provider
cd ./custom-auth
mvn clean package
cd -
```

Then, copy the `standalone.conf` and `*.nar` to the path of your Pulsar binary directory and run the standalone.

```bash
# Use Pulsar 2.10.3 and KoP 2.10.3.1 to test
# The NAR package of KoP 2.10.3.1 is built with JDK 11, so you must run it in JRE 11 or higher.
# Otherwise, you should build your own KoP from source.
curl -O -L https://archive.apache.org/dist/pulsar/pulsar-2.10.3/apache-pulsar-2.10.3-bin.tar.gz
tar zxf apache-pulsar-2.10.3-bin.tar.gz
cd apache-pulsar-2.10.3/
mkdir protocols && cd protocols
curl -O -L https://github.com/streamnative/kop/releases/download/v2.10.3.1/pulsar-protocol-handler-kafka-2.10.3.1.nar
cd ..

cp ../standalone.conf ./conf/
cp ../custom-auth/target/custom-auth-1.0-SNAPSHOT.jar ./lib/
./bin/pulsar standalone -nss -nfw
```

## Test the Kafka client with OAuth2 authentication

```bash
# /tmp/client_credentials.json is the hard-coded path of the demo
cp ./client_credentials.json /tmp/
cd ./kafka-client-oauth2
mvn clean package
mvn exec:java -Dexec.mainClass=Main
```

You will see the following output:

```
Sent to offset 0
Received hello from offset 0
```
