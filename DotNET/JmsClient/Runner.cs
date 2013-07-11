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
            using (session = connection.CreateSession(AcknowledgementMode.AutoAcknowledge))
            {
                using (IMessageConsumer orderActionConsumer = session.CreateConsumer(((ITopic)session.GetDestination("topic://OrderActions")), null, false))
                using (IMessageConsumer individualOrderActionConsumer = session.CreateConsumer((IQueue)session.GetDestination("queue://CSharpeOrderActions"), null, false))
                {
                    connection.Start();

                    orderActionConsumer.Listener += new MessageListener(OnOrderAction);
                    Console.WriteLine("orderActionConsumer started, waiting for messages...");

                    individualOrderActionConsumer.Listener += new MessageListener(OnIndividualOrderAction);
                    Console.WriteLine("individualOrderActionConsumer started, waiting for messages...");

                    Console.WriteLine("(Press ENTER to stop.)");
                    Console.ReadLine();
                }
            }
        }

        private static void OnOrderAction(IMessage message)
        {
            Console.WriteLine(((ITextMessage)message).Text);
            string content = ((ITextMessage)message).Text;
            if (content == "ALL")
            {
                using (IMessageProducer producer = session.CreateProducer(message.NMSReplyTo))
                {
                    ITextMessage reply = producer.CreateTextMessage();
                    reply.Properties.SetString("subsidiary", "CSharpe");
                    reply.Text = "5;FINISHED;2013-05-21 15:35;2014-06-12 18:22"
                       + "\n4;OPEN;1";

                    producer.Send(reply);
                }
            }
        }

        private static void OnIndividualOrderAction(IMessage message)
        {
            Console.WriteLine(((ITextMessage)message).Text);
        }
    }
}
