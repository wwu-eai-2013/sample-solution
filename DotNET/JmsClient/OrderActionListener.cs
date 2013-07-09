using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Apache.NMS;

namespace JmsClient
{
    class OrderActionListener
    {
        internal static void OnOrderAction(IMessage message)
        {
            Console.WriteLine(((ITextMessage)message).Text);
        }
    }
}
