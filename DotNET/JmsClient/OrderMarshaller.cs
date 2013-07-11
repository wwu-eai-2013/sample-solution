using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Pharmacy.BusinessLayer.Data;

namespace JmsClient
{
    class OrderMarshaller
    {
        public static char DELIMITER = ';';
        public static string DATE_FORMAT = "yyyy-MM-dd HH:mm";

        public static String MarshalAll(ICollection<ReplenishmentOrder> orders) {
            return String.Join("\n", orders.Select(item => MarshalSingleForAll(item)));
        }

        private static String MarshalSingleForAll(ReplenishmentOrder order)
        {
            String marshalledResult = "";
            marshalledResult += order.Id;
            marshalledResult += DELIMITER;
            marshalledResult += order.State.ToString().ToUpper();
            if (order.State == OrderState.Ordered
                || order.State == OrderState.Finished)
            {
                marshalledResult += DELIMITER;
                marshalledResult += Format(order.ExpectedDelivery);
            }
            if (order.State == OrderState.Finished)
            {
                marshalledResult += DELIMITER;
                marshalledResult += Format(order.ActualDelivery);
            }
            return marshalledResult;
        }

        private static String Format(DateTime? dateTime)
        {
            if (dateTime == null)
            {
                throw new ArgumentNullException("no datetime passed in to format");
            }
            return ((DateTime)dateTime).ToString(DATE_FORMAT);
        }


        public static String MarshalSingle(ReplenishmentOrder order)
        {
            String marshalledResult = "";
            marshalledResult += order.Id;
            marshalledResult += DELIMITER;
            marshalledResult += order.State.ToString().ToUpper();
            marshalledResult += MarshalPositions(order.Positions);
            if (order.State == OrderState.Ordered
               || order.State == OrderState.Finished)
            {
                marshalledResult += DELIMITER;
                marshalledResult += Format(order.ExpectedDelivery);
            }
            if (order.State == OrderState.Finished)
            {
                marshalledResult += DELIMITER;
                marshalledResult += Format(order.ActualDelivery);
            }
            return marshalledResult;
        }

        private static String MarshalPositions(ICollection<Position> positions)
        {
            String marshalledResult = "";
            marshalledResult += DELIMITER;
            marshalledResult += positions.Count;
            foreach (Position p in positions) {
              marshalledResult += DELIMITER;
              marshalledResult += p.DrugPZN;
              marshalledResult += DELIMITER;
              marshalledResult += p.Quantity;
            }
            return marshalledResult;
        }
    }
}
