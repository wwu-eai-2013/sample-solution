using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Pharmacy.BusinessLayer.Data;

namespace Pharmacy.BusinessLayer.Logic
{
    public static class OrderService
    {
        public static ICollection<ReplenishmentOrder> GetAllOrders()
        {
            using (PharmacyContainer db = new PharmacyContainer())
                return db.ReplenishmentOrderSet.ToList();
        }

        public static ReplenishmentOrder GetOrder(Int32 id)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return GetOrder(id, db);
            }
        }

        internal static ReplenishmentOrder GetOrder(Int32 id, PharmacyContainer db)
        {
            ReplenishmentOrder result = (from o in db.ReplenishmentOrderSet where o.Id == id select o).FirstOrDefault();

            if (result == default(ReplenishmentOrder))
                throw new ArgumentException(String.Format("Order with ID {0} not found", id.ToString()));

            return result;
        }

        public static ReplenishmentOrder GetOrderWithPositions(Int32 id)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                ReplenishmentOrder result = (from o in db.ReplenishmentOrderSet.Include("Positions") where o.Id == id select o).FirstOrDefault();

                if (result == default(ReplenishmentOrder))
                    throw new ArgumentException(String.Format("Order with ID {0} not found", id.ToString()));

                return result;
            }
        }

        public static ICollection<Position> GetPositionsForOrder(Int32 id)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return (from p in db.PositionSet.Include("Drug")
                            where p.Order.Id == id
                            select p).ToList();
            }
        }

        public static ICollection<Position> GetPendingPositionsForDrug(Int32 pzn)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                return PendingPositionsFor(pzn, db).ToList();
            }
        }

        internal static IQueryable<Position> PendingPositionsFor(Int32 pzn, PharmacyContainer db)
        {
            return (from p in db.PositionSet.Include("Order")
                    where p.DrugPZN == pzn
                    && (p.Order.State == OrderState.Open ||
                        p.Order.State == OrderState.Posting ||
                        p.Order.State == OrderState.Ordered)
                    select p);   
        }

        public static void UpdateExpectedDeliveryDate(Int32 id, DateTime expectedDelivery)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                ReplenishmentOrder order = GetOrder(id, db);
                order.ExpectedDelivery = expectedDelivery;
                db.SaveChanges();
            }
        }

        public static void UpdateActualDeliveryDate(Int32 id, DateTime actualDelivery)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                ReplenishmentOrder order = GetOrder(id, db);
                order.ActualDelivery = actualDelivery;
                db.SaveChanges();
            }
        }

        public static void ProceedToNextState(Int32 id)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                ReplenishmentOrder order = GetOrder(id, db);
                order.State = order.State.Next();
                if (order.State == OrderState.Finished)
                {
                    foreach (Position p in order.Positions)
                    {
                        DrugService.Replenish(p.Drug.PZN, p.Quantity, order.ActualDelivery, db);
                    }
                }
                db.SaveChanges();
            }
        }

        public static void Cancel(Int32 id)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                ReplenishmentOrder order = GetOrder(id, db);
                order.State = order.State.Cancel();
                foreach (Position p in order.Positions)
                {
                    InitiateReplenishment(p.DrugPZN, p.Quantity, db);
                }
                // migrate all positions to new or existing open order
                db.SaveChanges();
            }
        }

        public static void InitiateReplenishment(Int32 pzn, int quantity, PharmacyContainer db)
        {
            Drug drug = DrugService.GetDrug(pzn, db);
            if (HasOpenOrders(drug, db))
            {
                AdjustExistingOrderFor(drug, quantity, db);
            }
            else
            {
                CreatePositionOnOpenOrNewOrder(drug, quantity, db);
            }
            db.SaveChanges();
        }
        
        public static void InitiateReplenishment(int pzn, int quantity)
        {
            using (PharmacyContainer db = new PharmacyContainer())
            {
                InitiateReplenishment(pzn, quantity, db);
            }
        }

        public static Boolean HasOpenOrders(Drug drug, PharmacyContainer db)
        {
            return OpenOrdersFor(drug, db).Count() > 0;
        }

        private static IQueryable<ReplenishmentOrder> OpenOrdersFor(Drug drug, PharmacyContainer db)
        {
            return (from o in db.ReplenishmentOrderSet
                    where o.Positions.Where(p => p.DrugPZN == drug.PZN).Count() > 0
                    && o.State == OrderState.Open select o);
        }

        private static void AdjustExistingOrderFor(Drug drug, int quantity, PharmacyContainer db)
        {
            foreach (Position p in GetPendingPositionsForDrug(drug.PZN))
            {
                if (p.Order.State == OrderState.Open)
                {
                    p.Quantity = p.Quantity + quantity;
                    db.Entry(p).State = System.Data.EntityState.Modified;
                }
            }
        }

        private static void CreatePositionOnOpenOrNewOrder(Drug drug, int quantity, PharmacyContainer db)
        {
            if (OpenOrdersAvailable(db))
            {
                ReplenishmentOrder openOrder = GetOpenOrders(db).First();
                openOrder.Positions.Add(new Position
                {
                    Drug = drug,
                    Order = openOrder,
                    Quantity = quantity
                });
            }
            else
            {
                ReplenishmentOrder newOrder = new ReplenishmentOrder();
                newOrder.Positions.Add(new Position
                {
                    Drug = drug,
                    Order = newOrder,
                    Quantity = quantity
                });
                newOrder = db.ReplenishmentOrderSet.Add(newOrder);
            }
        }

        private static bool OpenOrdersAvailable(PharmacyContainer db)
        {
            return GetOpenOrders(db).Count() > 0;
        }

        private static IEnumerable<ReplenishmentOrder> GetOpenOrders(PharmacyContainer db)
        {
            return (from o in db.ReplenishmentOrderSet where o.State == OrderState.Open select o);
        }

    }
}
