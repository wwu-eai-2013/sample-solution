using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;

namespace JmsClient
{
    class Runner
    {
        // virtualbox host ip address (10.0.2.2)
        private static string HOST = "10.0.2.2";

        // when running on the same system use localhost
        // private static string HOST = "localhost";

        private static ISession session;

        static void Main(string[] args)
        {
            Uri connectUri = new Uri(String.Format("stomp:tcp://{0}:61613", HOST));
            IConnectionFactory factory = new NMSConnectionFactory(connectUri);

            using (IConnection connection = factory.CreateConnection())
            using (session = connection.CreateSession())
            {
                // durable consumers require a clientId
                connection.ClientId = "CSharpe";

                using (IMessageConsumer orderActionConsumer = session.CreateDurableConsumer(((ITopic)session.GetDestination("topic://OrderActions")), "CSharpeOrderActionListenener", null, false))
                {
                    connection.Start();

                    orderActionConsumer.Listener += new MessageListener(OrderActionListener.OnOrderAction);
                    Console.WriteLine("orderActionConsumer started, waiting for messages...");

                    Console.WriteLine("(Press ENTER to stop.)");
                    Console.ReadLine();
                }
            }
        }
    }
}
