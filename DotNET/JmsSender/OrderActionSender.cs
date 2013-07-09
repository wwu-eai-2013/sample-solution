using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;
// import stomp as well (somehow NMSConnectionFactory won't work in this context)
using Apache.NMS.Stomp;

namespace JmsSender
{
    public static class OrderActionSender
    {
        // virtualbox host ip address (10.0.2.2)
        private static string HOST = "10.0.2.2";

        // when running on the same system use localhost
        // private static string HOST = "localhost";

        static void Main(string[] args)
        {
        }

        public static void Open(int orderId)
        {
            string messageText = String.Format("OPEN;{0}", orderId);

            SendMessage(messageText);
        }

        private static void SendMessage(string messageText)
        {
            Uri connectUri = new Uri(String.Format("stomp:tcp://{0}:61613", HOST));
            IConnectionFactory factory = new ConnectionFactory(connectUri);

            using (IConnection connection = factory.CreateConnection())
            using (ISession session = connection.CreateSession())
            using (IMessageProducer producer = session.CreateProducer(session.GetDestination("topic://OrderActions")))
            {
                ITextMessage message = producer.CreateTextMessage();
                message.Properties.SetString("subsidiary", "CSharpe");
                message.Text = messageText;
                producer.Send(message);
            }
        }

        public static void Add(int pzn, int quantity, int orderId)
        {
            string message = String.Format("ADD;{0};{1};{2}", orderId, pzn, quantity);

            SendMessage(message);
        }
    }
}
