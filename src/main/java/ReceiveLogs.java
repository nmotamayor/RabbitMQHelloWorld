import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // Declare a fanout exchange
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        // Declare a temporary queue that is automatically deleted when the consumer disconnects
        String queueName = channel.queueDeclare().getQueue();

        // Bind the queue to the exchange
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // Define the callback to handle message delivery
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };

        // Start consuming messages
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
