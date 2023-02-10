# kop-custom-auth-provider

[KoP](https://github.com/streamnative/kop) with custom authentication provider.

Run the Ory Hydra as the OAuth2 authorization server:

```bash
docker compose -f hydra/docker-compose.yml up -d
```

Make sure you have installed the following commands:
- `docker`
- `jq`
- `node`/`npm`
- `javac`/`mvn`

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
# $PULSAR is the Pulsar binary directory
cp ./standalone.conf $PULSAR/conf/
cp ./custom-auth/target/custom-auth-1.0-SNAPSHOT.jar $PULSAR/lib
cd $PULSAR
./bin/pulsar standalone -nss -nfw
```
