using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;
using Pharmacy.BusinessLayer.Data;
using Pharmacy.BusinessLayer.Logic;

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
            Console.WriteLine("Received message: " + ((ITextMessage)message).Text);
            string content = ((ITextMessage)message).Text;
            if (content == "ALL")
            {
                using (IMessageProducer producer = session.CreateProducer(message.NMSReplyTo))
                {
                    ITextMessage reply = producer.CreateTextMessage();
                    reply.Properties.SetString("subsidiary", "CSharpe");
                    reply.Text = OrderMarshaller.MarshalAll(OrderService.GetAllOrders());
                    producer.Send(reply);
                }
            }
        }

        private static void OnIndividualOrderAction(IMessage message)
        {
            Console.WriteLine("Received message: " + ((ITextMessage)message).Text);

            string[] fragments = ((ITextMessage)message).Text.Split(OrderMarshaller.DELIMITER);
            string action = fragments[0];
            Int32 orderId = Int32.Parse(fragments[1]);
            switch (action)
            {
                case "GET":
                    {
                        // nothing todo, just send reply
                    }
                    break;
                case "POST":
                    {
                        ValidateOrderIsInState(orderId, OrderState.Open);
                        OrderService.ProceedToNextState(orderId);
                    }
                    break;
                case "ORDER":
                    {
                        ValidateOrderIsInState(orderId, OrderState.Posting);
                        OrderService.UpdateExpectedDeliveryDate(orderId, DateTime.Parse(fragments[2]));
                        OrderService.ProceedToNextState(orderId);
                    }
                    break;
                case "CANCEL":
                    {
                        ValidateOrderIsInState(orderId, OrderState.Posting);
                        OrderService.Cancel(orderId);
                    }
                    break;
                default:
                    throw new NotImplementedException();
            }
            using (IMessageProducer producer = session.CreateProducer(message.NMSReplyTo))
            {
                ITextMessage reply = producer.CreateTextMessage();
                reply.Text = OrderMarshaller.MarshalSingle(OrderService.GetOrderWithPositions(orderId));
                producer.Send(reply);
            }
        }

        private static void ValidateOrderIsInState(int orderId, OrderState state)
        {   
            if (OrderService.GetOrder(orderId).State != state)
                throw new IllegalStateException(String.Format("Order with id {0} was expected to be in state: {1}", orderId, state));
        }
    }
}
