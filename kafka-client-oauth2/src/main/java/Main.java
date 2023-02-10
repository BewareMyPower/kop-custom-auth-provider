import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.security.auth.SecurityProtocol;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final String bootstrapServers = "localhost:9092";

        final Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put("sasl.login.callback.handler.class",
                io.streamnative.pulsar.handlers.kop.security.oauth.OauthLoginCallbackHandler.class);
        producerProps.put("security.protocol", SecurityProtocol.SASL_PLAINTEXT.name());
        producerProps.put("sasl.mechanism", "OAUTHBEARER");
        producerProps.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule "
                + "required oauth.issuer.url=\"http://localhost:4444\" "
                + "oauth.credentials.url=\"file:///tmp/client_credentials.json\";");

        final KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps);
        final RecordMetadata metadata = producer.send(new ProducerRecord<>("my-topic", "hello")).get();
        System.out.println("Sent to offset " + metadata.offset());
        producer.close();

        final Properties consumerProps = new Properties();
        consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put("sasl.login.callback.handler.class",
                io.streamnative.pulsar.handlers.kop.security.oauth.OauthLoginCallbackHandler.class);
        consumerProps.put("security.protocol", SecurityProtocol.SASL_PLAINTEXT.name());
        consumerProps.put("sasl.mechanism", "OAUTHBEARER");
        consumerProps.put("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule "
                + "required oauth.issuer.url=\"http://localhost:4444\" "
                + "oauth.credentials.url=\"file:///tmp/client_credentials.json\";");

        final KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps);
        consumer.subscribe(Collections.singleton("my-topic"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            records.forEach(record -> System.out.println(
                    "Received " + record.value() + " from offset " + record.offset()));
            if (!records.isEmpty()) {
                break;
            }
        }
        consumer.close();
    }
}
